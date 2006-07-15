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
	
	
	public void updateData() throws DataException
	{
		try
		{
			initRequest();	
			
			// create reader
			SOSObservationReader reader = new SOSObservationReader();
			
			// select request type (post or get)
			boolean usePost = true;
			String url = query.getPostServer();
			if (url != null)
			{
				query.setPostServer(url);
			}
			else
			{
				url = layerCaps.getParent().getGetServers().get("GetObservation");
				
				if (url != null)
					query.setGetServer(url);
				else
					throw new DataException("No server specified");
			}			
			
			dataStream = requestBuilder.sendRequest(query, usePost);
						
			// parse response
			// TODO handle SOS service exception here -> remove from observation reader
			reader.parse(dataStream);
			
			// display data structure and encoding
			dataParser = reader.getDataParser();
			DataComponent dataInfo = dataParser.getDataComponents();
        	DataEncoding dataEnc = dataParser.getDataEncoding();
        	System.out.println("--- " + dataInfo.getName() + " ---");
        	System.out.println(dataInfo);
        	System.out.println(dataEnc);
            dataNode.clearAll();
        	BlockList blockList = dataNode.createList(dataInfo.copy());
            dataNode.setNodeStructureReady(true);
			
        	// reset node - HACK
        	//DataNode node = item.getNode();
        	//node.changePointCount(0);
        	
			// register the CDM data handler
        	//SWEDataNodeHandler dataNodeHandler = new SWEDataNodeHandler(dataInfo, node);
            dataHandler.setBlockList(blockList);
        	dataParser.setDataHandler(dataHandler);
        	
        	// start parsing if not cancelled
        	if (!canceled)
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
	
	
	protected void updateQuery()
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
	
	
	protected void initRequest()
	{
		// make sure previous request is cancelled
		endRequest();
		updateQuery();
		canceled = false;
		
		if (dataNode == null)
			dataNode = new DataNode();
	}
	
	
	protected void endRequest()
	{
		canceled = true;
		
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
    
    
    public void cancelOperation()
    {
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