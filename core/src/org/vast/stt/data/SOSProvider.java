/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.data;

import java.io.*;
import org.ogc.cdm.common.*;
import org.ogc.cdm.reader.*;
import org.vast.ows.OWSQuery;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.sos.SOSObservationReader;
import org.vast.ows.sos.SOSQuery;
import org.vast.ows.sos.SOSRequestWriter;


/**
 * <p><b>Title:</b><br/>
 * SOS Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Requests Data from an SOS server and fill up a DataNode
 * with it.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 11, 2005
 * @version 1.0
 */
public class SOSProvider extends OWSProvider
{
    protected SOSLayerCapabilities layerCaps;
	protected SOSQuery query;
	protected SOSRequestWriter requestBuilder;
	protected DataStreamParser dataParser;
	protected SWEDataHandler dataHandler;
	

	public SOSProvider()
	{
		requestBuilder = new SOSRequestWriter();
		dataHandler = new SWEDataHandler(this);
	}
    
    
    @Override
    public void init() throws DataException
    {
        try
        {
            initRequest();  
            
            // create reader
            SOSObservationReader reader = new SOSObservationReader();
            
            // create null request here
            double startTime = query.getTime().getStartTime();
            query.getTime().setStopTime(startTime);
            
            // select request type (post or get)
            boolean usePost = true;
            dataStream = requestBuilder.sendRequest(query, usePost);
                        
            // parse response
            reader.parse(dataStream);
            
            // display data structure and encoding
            DataComponent dataInfo = reader.getDataComponents();
            DataEncoding dataEnc = reader.getDataEncoding();
            System.out.println("--- " + dataInfo.getName() + " ---");
            System.out.println(dataInfo);
            System.out.println(dataEnc);

            // create BlockList
            BlockList blockList = dataNode.createList(dataInfo.copy());
            dataNode.setNodeStructureReady(true);
            dataHandler.setBlockList(blockList);
        }
        catch (Exception e)
        {
            String server = query.getPostServer();
            if (server == null)
                server = query.getGetServer();
            throw new DataException("Error while reading data from " + server, e);
        }
        finally
        {
            endRequest();
        }
    }
	
	
	public void updateData() throws DataException
	{
		// init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        try
        {    
            // init request using spatial + time extent
            initRequest();
            
			// create reader
			SOSObservationReader reader = new SOSObservationReader();
			
            if (canceled)
                return;
            
			// select request type (post or get)
            boolean usePost = true;
            dataStream = requestBuilder.sendRequest(query, usePost);
            
//            int car;
//            while ((car = dataStream.read()) != -1)
//                System.err.print((char)car);
            
            if (canceled)
                return;
                
			// parse response
			reader.parse(dataStream);
			dataParser = reader.getDataParser();
        	dataParser.setDataHandler(dataHandler);
        	
            if (canceled)
                return;
        	
        	 // start parsing
            dataParser.parse(reader.getDataStream());
		}
		catch (Exception e)
		{
			if (!canceled)
				throw new DataException(e.getMessage(), e);
		}
		finally
		{
			endRequest();
		}		
	}
	
	
	protected void initRequest()
	{
	    // update time range
        query.getTime().setStartTime(timeExtent.getAdjustedLagTime());
        query.getTime().setStopTime(timeExtent.getAdjustedLeadTime());
        
        // update bounding box
        query.getBbox().setMinX(spatialExtent.getMinX());
        query.getBbox().setMaxX(spatialExtent.getMaxX());
        query.getBbox().setMinY(spatialExtent.getMinY());
        query.getBbox().setMaxY(spatialExtent.getMaxY());
	}
	
	
	protected void endRequest()
	{
		// close stream(s)
		try
		{
            if (dataParser != null)
                dataParser.stop();
            
            if (dataStream != null)
				dataStream.close();
            
            dataStream = null;
            dataParser = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
    
    
    @Override
    public void cancelUpdate()
    {
        canceled = true;
        endRequest();
    }
	
	
    @Override
	public void createDefaultQuery()
	{
		if (layerCaps == null) return;
		
		String request = "GetObservation";
		query = new SOSQuery();
		query.setGetServer(layerCaps.getParent().getGetServers().get(request));
		query.setPostServer(layerCaps.getParent().getPostServers().get(request));
		query.setService(layerCaps.getParent().getService());
		query.setVersion(layerCaps.getParent().getVersion());
		query.setRequest(request);
		query.setOffering(layerCaps.getId());
		query.setFormat(layerCaps.getFormatList().get(0));
		query.getObservables().add(layerCaps.getObservableList().get(0));
		query.getProcedures().add(layerCaps.getProcedureList().get(0));
		query.getTime().setStartTime(layerCaps.getTimeList().get(0).getStartTime());
		query.getTime().setStopTime(query.getTime().getStartTime());		
	}

    
    @Override
	public SOSQuery getQuery()
	{
		return query;
	}


    @Override
	public void setQuery(OWSQuery query)
	{
		this.query = (SOSQuery)query;
		
		// set up spatial extent
		if (this.query.getBbox() != null)
		{
			this.spatialExtent.setMaxX(this.query.getBbox().getMaxX());
			this.spatialExtent.setMaxY(this.query.getBbox().getMaxY());
			this.spatialExtent.setMinX(this.query.getBbox().getMinX());
			this.spatialExtent.setMinY(this.query.getBbox().getMinY());
		}
		
		// set up time extent
		if (this.query.getTime() != null)
		{
			double start = this.query.getTime().getStartTime();
			double stop = this.query.getTime().getStopTime();
			this.timeExtent.setBaseTime(start);
			this.timeExtent.setLagTimeDelta(stop - start);
		}
	}


    @Override
    public void setServiceCapabilities(OWSServiceCapabilities caps)
    {
        this.layerCaps = (SOSLayerCapabilities)caps.getLayers().get(0);
        
        // set up max spatial extent
        if (!layerCaps.getBboxList().isEmpty())
        {
            this.spatialExtent.setMaxX(layerCaps.getBboxList().get(0).getMaxX());
            this.spatialExtent.setMaxY(layerCaps.getBboxList().get(0).getMaxY());
            this.spatialExtent.setMinX(layerCaps.getBboxList().get(0).getMinX());
            this.spatialExtent.setMinY(layerCaps.getBboxList().get(0).getMinY());
        }
        
        // set up max time extent
        if (!layerCaps.getTimeList().isEmpty())
        {
            double start = layerCaps.getTimeList().get(0).getStartTime();
            double stop = layerCaps.getTimeList().get(0).getStopTime();
            this.timeExtent.setBaseTime(start);
            this.timeExtent.setLagTimeDelta(stop - start);
        }        
    }
    
    
    @Override
    public SOSLayerCapabilities getLayerCapabilities()
    {
        return this.layerCaps;
    }
    
    
    public boolean isSpatialSubsetSupported()
    {
        // TODO isSpatialSubsetSupported() depends on capabilities
        return true;
    }


    public boolean isTimeSubsetSupported()
    {
        return true;
    }
}