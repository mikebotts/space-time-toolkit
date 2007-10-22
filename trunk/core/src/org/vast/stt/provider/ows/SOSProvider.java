/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.ows;

import java.io.IOException;

import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataEncoding;
import org.vast.cdm.common.DataStreamParser;
import org.vast.ows.OWSRequest;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.sos.SOSQuery;
import org.vast.ows.sos.SOSResponseReader;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.DataException;
import org.vast.stt.provider.swe.SWEDataHandler;


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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 11, 2005
 * @version 1.0
 */
public class SOSProvider extends OWSProvider
{
    protected SOSLayerCapabilities layerCaps;
	protected SOSQuery query;
	protected DataStreamParser dataParser;
	protected SWEDataHandler dataHandler;
	protected boolean usePost;
    

	public SOSProvider()
	{
		dataHandler = new SWEDataHandler(this);
	}
    
    
    @Override
    public void init() throws DataException
    {
        try
        {
            initRequest();  
            
            // create reader
            SOSResponseReader reader = new SOSResponseReader();
            
            // create template request here
            //double time = new DateTime().getJulianTime();
            //query.getTime().setBaseTime(time - 3600);
            //query.getTime().setDeltaTimes(0, 0);
            //query.setResponseMode(SOSQuery.ResponseMode.RESULT_TEMPLATE);
            
            // select request type (post or get)
            if (query.getPostServer() != null)
                usePost = true;
            else if (query.getGetServer() == null)
                throw new DataException("No GET or POST Server URL specified");
            
            // send request
            if(usePost)
            	dataStream = owsUtils.sendPostRequest(query).getInputStream();
            else 
            	dataStream = owsUtils.sendGetRequest(query).getInputStream();
            
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
	
	
    @Override
	public void updateData() throws DataException
	{
		// init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        try
        {    
            // init request using spatial + time extent
            initRequest();
            query.setResponseMode(SOSQuery.ResponseMode.INLINE);
            
			// create reader
			SOSResponseReader reader = new SOSResponseReader();
			
            if (canceled)
                return;
            
            // send request
            if(usePost)
            	dataStream = owsUtils.sendPostRequest(query).getInputStream();
            else 
            	dataStream = owsUtils.sendGetRequest(query).getInputStream();
            
            if (canceled)
                return;
                
			// parse response
            dataHandler.reset();
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
            {
                String server = query.getPostServer();
                if (server == null)
                    server = query.getGetServer();
                throw new DataException("Error while reading data from " + server, e);
            }
		}
		finally
		{
			endRequest();
		}		
	}
	
	
	protected void initRequest()
	{
	    // update time range
        query.getTime().setBaseTime(timeExtent.getAdjustedLagTime());
        query.getTime().setStopTime(timeExtent.getAdjustedLeadTime());
        query.getTime().setTimeStep(timeExtent.getTimeStep());
        
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
		query.setOperation("GetObservation");
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
	public void setQuery(OWSRequest query)
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
            double step = this.query.getTime().getTimeStep();
			this.timeExtent.setBaseTime(start);
			this.timeExtent.setLeadTimeDelta(stop - start);
            this.timeExtent.setTimeStep(step);
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