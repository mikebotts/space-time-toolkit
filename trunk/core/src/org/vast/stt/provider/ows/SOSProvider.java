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
import org.vast.ogc.OGCRegistry;
import org.vast.ows.OWSRequest;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.om.ObservationStreamReader;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.sos.GetObservationRequest.ResponseMode;
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
	protected GetObservationRequest request;
	protected boolean usePost;
    

	public SOSProvider()
	{		
	}
    
    
    @Override
    public void init() throws DataException
    {
        try
        {
            initRequest();  
            
            // create template request here
            //double time = new DateTime().getJulianTime();
            //query.getTime().setBaseTime(time - 3600);
            //query.getTime().setDeltaTimes(0, 0);
            //query.setResponseMode(SOSQuery.ResponseMode.RESULT_TEMPLATE);
            
            // select request type (post or get)
            if (request.getPostServer() != null)
                usePost = true;
            else if (request.getGetServer() == null)
                throw new DataException("No GET or POST Server URL specified");
            
            // send request
            if(usePost)
            	dataStream = owsUtils.sendPostRequest(request).getInputStream();
            else 
            	dataStream = owsUtils.sendGetRequest(request).getInputStream();
            
            // create reader and parse response
            ObservationStreamReader reader = (ObservationStreamReader)OGCRegistry.createReader("OM", "ObservationStream", request.getVersion());
            reader.readObservationStream(dataStream, null);
            
            // display data structure and encoding
            DataComponent dataInfo = reader.getDataComponents();
            DataEncoding dataEnc = reader.getDataEncoding();
            System.out.println("--- " + dataInfo.getName() + " ---");
            System.out.println(dataInfo);
            System.out.println(dataEnc);

            // create BlockList
            dataNode.createList(dataInfo.copy());
            dataNode.setNodeStructureReady(true);
        }
        catch (Exception e)
        {
            String server = request.getPostServer();
            if (server == null)
                server = request.getGetServer();
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
            request.setResponseMode(ResponseMode.INLINE);
			
            if (canceled)
                return;
            
            // send request
            if(usePost)
            	dataStream = owsUtils.sendPostRequest(request).getInputStream();
            else 
            	dataStream = owsUtils.sendGetRequest(request).getInputStream();
            
            if (canceled)
                return;
            
            // create the right reader
            ObservationStreamReader reader =
                (ObservationStreamReader)OGCRegistry.createReader("OM", "ObservationStream", request.getVersion());
                            
			// create data handler
            SWEDataHandler dataHandler = new SWEDataHandler(this, dataNode.getListArray().get(0));
                        
            // launch parser
            reader.readObservationStream(dataStream, dataHandler);
		}
		catch (Exception e)
		{
			if (!canceled)
            {
                String server = request.getPostServer();
                if (server == null)
                    server = request.getGetServer();
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
        request.getTime().setBaseTime(timeExtent.getAdjustedLagTime());
        request.getTime().setStopTime(timeExtent.getAdjustedLeadTime());
        request.getTime().setTimeStep(timeExtent.getTimeStep());
        
        // update bounding box
        request.getBbox().setMinX(spatialExtent.getMinX());
        request.getBbox().setMaxX(spatialExtent.getMaxX());
        request.getBbox().setMinY(spatialExtent.getMinY());
        request.getBbox().setMaxY(spatialExtent.getMaxY());
	}
	
	
	protected void endRequest()
	{
		// close stream(s)
		try
		{
            //if (dataParser != null)
            //    dataParser.stop();
            
            if (dataStream != null)
				dataStream.close();
            
            dataStream = null;
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
		
		request = new GetObservationRequest();
		request.setGetServer(layerCaps.getParent().getGetServers().get(request));
		request.setPostServer(layerCaps.getParent().getPostServers().get(request));
		request.setService(layerCaps.getParent().getService());
		request.setService("SOS");
		request.setVersion(layerCaps.getParent().getVersion());
		request.setOffering(layerCaps.getIdentifier());
		request.setFormat(layerCaps.getFormatList().get(0));
		request.getObservables().add(layerCaps.getObservableList().get(0));
		request.getProcedures().add(layerCaps.getProcedureList().get(0));
		request.getTime().setStartTime(layerCaps.getTimeList().get(0).getStartTime());
		request.getTime().setStopTime(request.getTime().getStartTime());		
	}

    
    @Override
	public GetObservationRequest getQuery()
	{
		return request;
	}


    @Override
	public void setQuery(OWSRequest request)
	{
		this.request = (GetObservationRequest)request;
		
		// set up spatial extent
		if (this.request.getBbox() != null)
		{
			this.spatialExtent.setMaxX(this.request.getBbox().getMaxX());
			this.spatialExtent.setMaxY(this.request.getBbox().getMaxY());
			this.spatialExtent.setMinX(this.request.getBbox().getMinX());
			this.spatialExtent.setMinY(this.request.getBbox().getMinY());
		}
		
		// set up time extent
		if (this.request.getTime() != null)
		{
			double start = this.request.getTime().getStartTime();
			double stop = this.request.getTime().getStopTime();
            double step = this.request.getTime().getTimeStep();
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