/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit SPS Plugin".
 
 The Initial Developer of the Original Code is Spotimage S.A.
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Alexandre Robin <alexandre.robin@spotimage.fr> for more
 information.
 
 Contributor(s): 
    Alexandre Robin <alexandre.robin@spotimage.fr>
 
******************************* END LICENSE BLOCK ***************************/

package com.spotimage.stt.sps.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.XmlEncoding;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataArray;
import org.vast.math.Vector3d;
import org.vast.ows.sps.DescribeTaskingRequest;
import org.vast.ows.sps.DescribeTaskingResponse;
import org.vast.ows.sps.FeasibilityReport;
import org.vast.ows.sps.GetFeasibilityRequest;
import org.vast.ows.sps.GetFeasibilityResponse;
import org.vast.ows.sps.GetStatusRequest;
import org.vast.ows.sps.GetStatusResponse;
import org.vast.ows.sps.SPSUtils;
import org.vast.ows.sps.StatusReport;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.DataException;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.STTPolygonExtent;
import org.vast.sweCommon.SWEData;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;
import com.spotimage.eosps.EOConstants;
import com.spotimage.eosps.EOOpticalParamHelper;
import com.spotimage.eosps.EOOpticalReportHelper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;


/**
 * <p><b>Title:</b><br/>
 * SPS Feasibility Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Issues GetFeasibility Request(s) to a SPS Server, parses SWE Common Result,
 * and places the data in the DataNode for display.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Mar 11, 2008
 * @version 1.0
 */
public class SPSFeasibilityProvider extends AbstractProvider
{
    protected static String errorMsg = "Error while sending ";
    
    protected DescribeTaskingRequest dtRequest;
    protected DataComponent taskingParams, studyParams;
    protected GetFeasibilityRequest gfRequest;
    protected GetFeasibilityResponse gfResponse;
    protected SPSUtils spsUtils = new SPSUtils();
    protected boolean canUpdate = false;
    protected BlockList cellBlockList;
    protected BlockList segmentBlockList;
    

	public SPSFeasibilityProvider()
	{
		super();
		dtRequest = new DescribeTaskingRequest();
		gfRequest = new GetFeasibilityRequest();		
		this.setSpatialExtent(new STTPolygonExtent());
	}
    
    
    @Override
    public void init() throws DataException
    {    	
    	InputStream dataStream = null;
    	
    	try
        {
    		spsUtils.writeXMLQuery(System.out, dtRequest);
    		
    		// send request and parse response
    		dataStream = spsUtils.sendGetRequest(dtRequest).getInputStream();
        	DOMHelper dom = new DOMHelper(dataStream, false);
            DescribeTaskingResponse dtResponse = spsUtils.readDescribeTaskingResponse(dom, dom.getRootElement());

            // get handles to param structures
            taskingParams = dtResponse.getTaskingParameters();
            studyParams = dtResponse.getFeasibilityReportExtendedData();
            taskingParams.assignNewDataBlock();
            
            // create block list for cells     
            DataArray cellArray = (DataArray)studyParams.getComponent("GridCells");
            if (cellArray != null)
            	cellBlockList = dataNode.createList(cellArray.getArrayComponent().copy());
            
            // create block list for segments
            DataArray segArray = (DataArray)studyParams.getComponent("EstimatedSegments");
            if (segArray != null)
            	segmentBlockList = dataNode.createList(segArray.getArrayComponent().copy());
            
            dataNode.setNodeStructureReady(true);
        }
        catch (Exception e)
        {
            String server = dtRequest.getPostServer();
            throw new DataException(errorMsg + dtRequest.getOperation() + " Response from " + server, e);
        }
        finally
        {
        	if (dataStream != null)
				endRequest(dataStream);
        }
    }
	
	
    @Override
	public void updateData() throws DataException
	{
    	InputStream dataStream = null;
    	if (!canUpdate)
    		return;
    	
    	// init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        try
        {
			// instantiate helper
        	SWEData taskingData = new SWEData();
    		taskingData.setDataComponents(taskingParams.copy());
    		taskingData.getDataList().addData(taskingParams.getData());
    		gfRequest.setParameters(taskingData);
        	EOOpticalParamHelper helper = new EOOpticalParamHelper(gfRequest);
			        	        	        	
			// polygon ROI
			GeometryFactory fac = new GeometryFactory();
			ArrayList<Vector3d> polyPoints = ((STTPolygonExtent)this.spatialExtent).getPointList();
			int numPoints = polyPoints.size();
			Coordinate[] coords = new Coordinate[numPoints + 1];
			for (int p=0; p<numPoints; p++)
			{
				Vector3d point = polyPoints.get(p);
				coords[p] =	new Coordinate(point.x, point.y);
			}
			coords[numPoints] = coords[0];
			LinearRing outer = fac.createLinearRing(coords);
			helper.setPolygonROI(fac.createPolygon(outer, null));
			
			// select request encoding
			gfRequest.getParameters().setDataEncoding(new XmlEncoding(EOConstants.EO_PREFIX, EOConstants.EO_NAMESPACE));
			System.out.println("\n== GetFeasibility Request ==\n");
			spsUtils.writeXMLQuery(System.out, gfRequest);
			
        	// send request
			HttpURLConnection conn = spsUtils.sendSoapRequest(gfRequest);
			dataStream = conn.getInputStream();
			DOMHelper dom = new DOMHelper(dataStream, false);
			Element reqElt = dom.getElement("Body/*");
        	
            // parse response
        	gfResponse = (GetFeasibilityResponse)spsUtils.readParameterizedResponse(dom, reqElt, studyParams, "2.0");
        	System.out.println("\n== GetFeasibility Response ==\n");
        	spsUtils.writeXMLResponse(System.out, gfResponse);
        	StatusReport finalReport = gfResponse.getReport();
        	String taskID = finalReport.getTaskID();
        	
        	// if response is still pending, issue GetStatus requests until we get a result
        	while (finalReport.getStatusCode().equals(StatusReport.PENDING))
        	{
        		// wait half of the estimated delay
        		double delay = finalReport.getEstimatedDelay();
        		Thread.sleep((long)(delay * 1000.0 / 2.0));
        		
        		// prepare GetStatus request
        		GetStatusRequest gsRequest = new GetStatusRequest();
        		gsRequest.setTaskID(taskID);
        		gsRequest.setGetServer(gfRequest.getGetServer());
        		gsRequest.setVersion(gfRequest.getVersion());
        		
        		// send request via GET
        		conn = spsUtils.sendGetRequest(gfRequest);
    			dataStream = conn.getInputStream();
    			dom = new DOMHelper(dataStream, false);
    			reqElt = dom.getRootElement();
            	
                // parse response
    			GetStatusResponse gsResponse = (GetStatusResponse)spsUtils.readParameterizedResponse(dom, reqElt, studyParams, "2.0");
            	System.out.println("\n== GetStatus Response ==\n");
            	spsUtils.writeXMLResponse(System.out, gsResponse);
            	finalReport = gsResponse.getReport();
        	}
        	
        	// add study results to block lists if feasible
        	if (finalReport.getStatusCode().equals(FeasibilityReport.FEASIBLE))
        	{    		
	            EOOpticalReportHelper reportHelper = new EOOpticalReportHelper(gfResponse);
	        	
	            // cells
	            if (cellBlockList != null)
	            {
		            DataArray cellArray = reportHelper.findGridCellList();
		            int cellCount = cellArray.getComponentCount();
		            for (int i=0; i<cellCount; i++)
		            {
		            	AbstractDataBlock block = (AbstractDataBlock)cellArray.getComponent(i).getData();
		            	cellBlockList.addBlock(block);
		            }
	            }
	            
	            // segments
	            if (segmentBlockList != null)
	            {
		            DataArray segmentArray = reportHelper.findSegmentList();
		            int segmentCount = segmentArray.getComponentCount();
		            for (int i=0; i<segmentCount; i++)
		            {
		            	AbstractDataBlock block = (AbstractDataBlock)segmentArray.getComponent(i).getData();
		            	segmentBlockList.addBlock(block);
		            }
	            }
	            
	            this.dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
        	}
		}
		catch (Exception e)
		{
			if (!canceled)
            {
                String server = gfRequest.getPostServer();
                throw new DataException(errorMsg + gfRequest.getOperation() + " request to " + server, e);
            }
		}
		finally
		{
			if (dataStream != null)
				endRequest(dataStream);
		}
	}
    
        
    public DescribeTaskingRequest getDescribeTaskingRequest()
	{
		return dtRequest;
	}
	
	
	public void setDescribeTaskingRequest(DescribeTaskingRequest dtRequest)
	{
		this.dtRequest = dtRequest;
		this.setServer(dtRequest.getPostServer(), dtRequest.getVersion());
		this.setSensorID(dtRequest.getSensorID());
	}


	public GetFeasibilityRequest getGetFeasibilityRequest()
	{
		return gfRequest;
	}
	
	
	public void setGetFeasibilityRequest(GetFeasibilityRequest gfRequest)
	{
		this.gfRequest = gfRequest;
	}
	
	
	public GetFeasibilityResponse getGetFeasibilityResponse()
	{
		return gfResponse;
	}


	public DataComponent getTaskingParams()
	{
		return taskingParams;
	}


	public DataComponent getStudyParams()
	{
		return studyParams;
	}
	
	
	public void setSensorID(String sensorID)
	{
		dtRequest.setSensorID(sensorID);
		gfRequest.setSensorID(sensorID);
	}
	
	
	public void setServer(String server, String version)
	{
		dtRequest.setGetServer(server);
		dtRequest.setVersion(version);
		gfRequest.setPostServer(server);
		gfRequest.setVersion(version);
	}


	protected void endRequest(InputStream dataStream)
	{
		// close stream(s)
		try
		{
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
    }
    
    
    public boolean isSpatialSubsetSupported()
    {
        return true;
    }


    public boolean isTimeSubsetSupported()
    {
        return false;
    }


	public void setCanUpdate(boolean canUpdate)
	{
		this.canUpdate = canUpdate;
	}
}