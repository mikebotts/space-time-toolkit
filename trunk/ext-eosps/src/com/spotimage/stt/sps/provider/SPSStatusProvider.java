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
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataType;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataArray;
import org.vast.data.DataBlockFactory;
import org.vast.data.DataBlockMixed;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.ogc.OGCRegistry;
import org.vast.ows.OWSUtils;
import org.vast.ows.sps.DescribeResultAccessRequest;
import org.vast.ows.sps.DescribeResultAccessResponse;
import org.vast.ows.sps.DescribeTaskingRequest;
import org.vast.ows.sps.DescribeTaskingResponse;
import org.vast.ows.sps.GetStatusRequest;
import org.vast.ows.sps.GetStatusResponse;
import org.vast.ows.sps.SPSUtils;
import org.vast.ows.sps.StatusReport;
import org.vast.ows.sps.SubmitRequest;
import org.vast.ows.sps.SubmitResponse;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.DataException;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.sweCommon.SWEData;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;
import com.spotimage.eosps.EOConstants;
import com.spotimage.eosps.EOReportHelper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;


/**
 * <p><b>Title:</b><br/>
 * SPS Status Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Issues GetStatus Requests to a SPS Server, parses SWE Common Result,
 * and places the data in the DataNode for display.
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Mar 11, 2008
 * @version 1.0
 */
public class SPSStatusProvider extends AbstractProvider
{
    protected static String errorMsg = "Error while reading ";
    
    protected SPSUtils spsUtils = new SPSUtils();
    protected SubmitRequest sRequest;
    protected DescribeTaskingRequest dtRequest;
    protected GetStatusRequest gsRequest;
    protected GetStatusResponse gsResponse;
    protected DescribeResultAccessRequest draRequest;
    protected DescribeResultAccessResponse draResponse;
    
    protected DataComponent reportParams;
	protected DataComponent cellParams;
	protected DataComponent segmentParams;
	protected DataComponent quicklookParams;
    
    protected BlockList cellBlockList;
    protected BlockList segmentBlockList;
    protected BlockList quicklookList;
    

	public SPSStatusProvider()
	{
		super();
		sRequest = new SubmitRequest();
		dtRequest = new DescribeTaskingRequest();
		gsRequest = new GetStatusRequest();
		draRequest = new DescribeResultAccessRequest();
	}
    
    
    @Override
    public void init() throws DataException
    {    	
    	InputStream dataStream = null;
    	
    	try
        {
    		// send request and parse response
    		spsUtils.writeXMLQuery(System.out, dtRequest);
    		dataStream = spsUtils.sendGetRequest(dtRequest).getInputStream();
        	DOMHelper dom = new DOMHelper(dataStream, false);
            DescribeTaskingResponse dtResponse = spsUtils.readDescribeTaskingResponse(dom, dom.getRootElement());
            
            // get handles to param structures
            reportParams = dtResponse.getStatusReportExtendedData();
            
            // create block list for cells     
            DataArray cellArray = (DataArray)reportParams.getComponent("GridCells");
            if (cellArray != null)
            {
            	cellParams = cellArray.getArrayComponent();
            	cellBlockList = dataNode.createList(cellParams.copy());
            }
            
            // create block list for segments     
            DataArray segArray = (DataArray)reportParams.getComponent("ProgrammedSegments");
            segmentParams = segArray.getArrayComponent();
            segmentBlockList = dataNode.createList(segmentParams.copy());
                        
            // create block list for quicklook imagery
            DataGroup patch = new DataGroup(2, "Quicklook");
            
            DataArray grid = new DataArray(2);
            grid.setName("grid");
            DataArray line = new DataArray(2);
            line.setName("row");
            DataGroup point = new DataGroup(2, "point");
            DataValue lat = new DataValue("lat", DataType.FLOAT);
            point.addComponent(lat);
            DataValue lon = new DataValue("lon", DataType.FLOAT);
            point.addComponent(lon);
            line.addComponent(point);
            grid.addComponent(line);
            patch.addComponent(grid);
            
            DataArray img = new DataArray(512);
            img.setName("imagery");
            DataArray row = new DataArray(512);
            row.setName("row");
            DataGroup pixel = new DataGroup(3, "pixel");
            DataValue blue = new DataValue("blue", DataType.BYTE);
            pixel.addComponent(blue);
            DataValue green = new DataValue("green", DataType.BYTE);
            pixel.addComponent(green);
            DataValue red = new DataValue("red", DataType.BYTE);
            pixel.addComponent(red);
            row.addComponent(pixel);
            img.addComponent(row);
            patch.addComponent(img);
            
            quicklookParams = patch;
            quicklookList = dataNode.createList(quicklookParams.copy());
            
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
    	
    	// init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        try
        {           
        	// send request
        	System.out.println("\n== GetStatus Request ==\n");
        	System.out.println(spsUtils.buildURLQuery(gsRequest) + "\n");
        	spsUtils.writeXMLQuery(System.out, gsRequest);
			dataStream = spsUtils.sendGetRequest(gsRequest).getInputStream();
        	DOMHelper dom = new DOMHelper(dataStream, false);
            
            // parse response
        	gsResponse = (GetStatusResponse)spsUtils.readParameterizedResponse(dom, dom.getRootElement(), reportParams, "2.0");
    		System.out.println("\n== GetStatus Response ==\n");
    		spsUtils.writeXMLResponse(System.out, gsResponse);
    		clearData();
    		
    		if (gsResponse.getParameters() != null)
    		{    		
    			EOReportHelper helper = new EOReportHelper(gsResponse.getReport());
    			DataArray segmentList = helper.findSegmentList();
    			int segmentCount = helper.getNumSegments();
    			    			
    			// add results to block list
	            for (int i=0; i<segmentCount; i++)
	            {
	            	// copy data to item data node
	            	AbstractDataBlock block = (AbstractDataBlock)segmentList.getComponent(i).getData();
	            	segmentBlockList.addBlock(block);
	            	
	            	helper.loadSegment(i);
	            	
	            	// create quicklook data
	            	String previewUrl = null;//"http://ws.spotimage.com/ows/quicklook.jpg";//helper.getPreviewUrl();
	            	if (previewUrl != null)
	            	{
	            		// image block
	            		URL imgUrl = new URL(previewUrl);
		            	ImageDescriptor imageDesc = ImageDescriptor.createFromURL(imgUrl);
						Image img = imageDesc.createImage();
						AbstractDataBlock imgBlock = DataBlockFactory.createBlock(img.getImageData().data);
						
						// grid block				
						double DTR = Math.PI / 180.0;
		            	AbstractDataBlock gridBlock = DataBlockFactory.createBlock(new float[8]);
		            	Polygon poly = helper.getFootprint();
		            	int[] indices = new int[] {0, 1, 3, 2};
		            	for (int p=0; p<indices.length; p++)
		            	{
		            		Coordinate coord = poly.getCoordinates()[indices[p]]; 
		            		gridBlock.setDoubleValue(p*2, coord.y * DTR);
		            		gridBlock.setDoubleValue(p*2+1, coord.x * DTR);
		            	}
		            	
		            	// combine them
		            	DataBlockMixed qlBlock = DataBlockFactory.createMixedBlock(gridBlock, imgBlock);
		            	quicklookList.addBlock(qlBlock);
	            	}
	            }
	            
	            // also call describe result access to get result locations
	            describeResultAccess();
	            
	            this.dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
    		}
		}
		catch (Exception e)
		{
			if (!canceled)
            {
                String server = gsRequest.getGetServer();
                throw new DataException(errorMsg + gsRequest.getOperation()
		                + " Response from " + server, e);
            }
		}
		finally
		{
			if (dataStream != null)
				endRequest(dataStream);
		}
	}
    
    
    public void startPollingThread()
    {
    	Thread pollingThread = new Thread()
    	{
			public void run()
			{
				String statusCode;
				
				do
				{
					try {Thread.sleep(10000);}
					catch (InterruptedException e) {}
					
					if (gsResponse != null)
					{
						statusCode = gsResponse.getReport().getStatusCode();
					
						if (statusCode.equals(StatusReport.COMPLETED) ||
								statusCode.equals(StatusReport.CANCELLED) ||
								statusCode.equals(StatusReport.FAILED))
							break;
					}
					
					startUpdate(true);					
				}
				while (true);
				
			}    		
    	};
    	
    	pollingThread.start();
    }
    
    
    public void subscribe(NotificationOptions notifOpts, String taskID)
    {
    	try
		{
			// send Subscribe request
			/*<wsnt:Subscribe>
			   <wsnt:ConsumerReference>
			      <wsa:Address>http://ws.spotimage.com/axis2/services/SimpleWNS</wsa:Address>
			      <wsa:ReferenceParameters>
			         <UserID>alexandre.robin</UserID>
			      </wsa:ReferenceParameters>
			   </wsnt:ConsumerReference>
			   <wsnt:Filter>
			      <wsnt:TopicExpression Dialect="http://docs.oasis-open.org/wsn/t-1/TopicExpression/Full" xmlns:sps="http://www.opengis.net/sps/2.0">sps:TASK_ACCEPTED|sps:TASK_COMPLETED</wsnt:TopicExpression>
			      <wsnt:MessageContent Dialect="http://www.w3.org/TR/1999/REC-xpath-19991116">//*[local-name()='taskID']='http://ws.spotimage.com/sps/PR00010'</wsnt:MessageContent>
			   </wsnt:Filter>
			</wsnt:Subscribe>*/
			
			DOMHelper dom = new DOMHelper();
			dom.addUserPrefix("wsn", "http://docs.oasis-open.org/wsn/b-2");
			dom.addUserPrefix("wsa", "http://www.w3.org/2005/08/addressing");
			dom.addUserPrefix("soap", "http://schemas.xmlsoap.org/soap/envelope/");
			dom.addUserPrefix("wns", OGCRegistry.getNamespaceURI(OWSUtils.WNS));
			dom.getXmlDocument().addNS("sps", "http://www.opengis.net/sps/2.0");
			dom.getXmlDocument().addNS("eo", EOConstants.EO_NAMESPACE);
			
			Element rootElt = dom.createElement("soap:Envelope");
			Element reqElt = dom.addElement(rootElt, "soap:Body/wsn:Subscribe");
			
			// notif uri
			dom.setElementValue(reqElt, "wsn:ConsumerReference/wsa:Address", "http://localhost:8080/axis2/services/SimpleWNS");//"http://ws.spotimage.com/axis2/services/SimpleWNS");
			dom.setElementValue(reqElt, "wsn:ConsumerReference/wsa:ReferenceParameters/wns:UserID", notifOpts.destUri);
			
			// topic filter
			//dom.setElementValue(reqElt, "wsn:Filter/wsn:TopicExpression", notifOpts.topicList);
			//dom.setAttributeValue(reqElt, "wsn:Filter/wsn:TopicExpression/Dialect", "http://docs.oasis-open.org/wsn/t-1/TopicExpression/Full");
						
			// taskID filter
			dom.setElementValue(reqElt, "wsn:Filter/wsn:MessageContent", "//*[local-name()='taskID']='" + taskID + "'");
			dom.setAttributeValue(reqElt, "wsn:Filter/wsn:MessageContent/Dialect", "http://www.w3.org/TR/1999/REC-xpath-19991116");
			
			// print request
			System.out.println("\n== Subscribe Request ==\n");
			dom.serialize(rootElt, System.out, true);
			
			// initialize HTTP connection
			URL url = new URL(sRequest.getPostServer());
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-type", "text/xml");
			connection.setRequestProperty("SOAPAction", "Subscribe");
			PrintStream out = new PrintStream(connection.getOutputStream());
			
			// send post data to server	
			dom.serialize(rootElt, out, false);
			out.flush();
			connection.connect();
			out.close();
			
			// catch server side exceptions
			if (connection.getResponseCode() > 202)
			{
				int c = 0;
				while ((c = connection.getErrorStream().read()) != -1)
					System.err.print((char)c);
				return;
			}
			
			dom = new DOMHelper(connection.getInputStream(), false);
			dom.serialize(dom.getRootElement(), System.out, true);
			//String id = dom.getElementValue("Envelope/Body/SubscribeResponse/SubscriptionReference");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }
    
    
    public StatusReport submitTask(SWEData requestData) throws DataException
    {
    	try
		{
    		dispatchEvent(new STTEvent(this, EventType.PROVIDER_UPDATE_START));
    		
    		sRequest.setParameters(requestData);
    		System.out.println("\n== Submit Request ==\n");
			spsUtils.writeXMLQuery(System.out, sRequest);
			
			// send request
			InputStream dataStream = spsUtils.sendSoapRequest(sRequest).getInputStream();
			DOMHelper dom = new DOMHelper(dataStream, false);
			Element reqElt = dom.getElement("Body/*");

			// parse response
			SubmitResponse sResponse = (SubmitResponse)spsUtils.readXMLResponse(dom, reqElt, OWSUtils.SPS, "SubmitResponse");
			System.out.println("\n== Submit Response ==\n");
			spsUtils.writeXMLResponse(System.out, sResponse);
			
			return sResponse.getReport();
		}
    	catch (Exception e)
		{
    		String server = sRequest.getPostServer();
            throw new DataException(errorMsg + sRequest.getOperation() + " request to " + server, e);
		}
    	finally
    	{
    		dispatchEvent(new STTEvent(this, EventType.PROVIDER_UPDATE_DONE));
    	}
    }
    
    
    public void describeResultAccess()
    {
    	try
		{
			// send request and parse response
    		System.out.println("\n== DescribeResultAccess Request ==\n");
    		System.out.println(spsUtils.buildURLQuery(draRequest) + "\n");
    		spsUtils.writeXMLQuery(System.out, draRequest);
			InputStream dataStream = spsUtils.sendGetRequest(draRequest).getInputStream();
			DOMHelper dom = new DOMHelper(dataStream, false);
			
			// parse response
			draResponse = (DescribeResultAccessResponse)spsUtils.readXMLResponse(dom, dom.getRootElement(), OWSUtils.SPS, "DescribeResultAccessResponse");
			System.out.println("\n== DescribeResultAccess Response ==\n");
			spsUtils.writeXMLResponse(System.out, draResponse);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }
    	
	
	public DescribeTaskingRequest getDescribeTaskingRequest()
	{
		return dtRequest;
	}
	
	
	public void setDescribeTaskingRequest(DescribeTaskingRequest dtRequest)
	{
		this.dtRequest = dtRequest;
	}
	
	
	public GetStatusRequest getGetStatusRequest()
	{
		return gsRequest;
	}
	
	
	public void setGetStatusRequest(GetStatusRequest gsRequest)
	{
		this.gsRequest = gsRequest;
	}
	
	
	public GetStatusResponse getGetStatusResponse()
	{
		return gsResponse;
	}
	
	
	public DescribeResultAccessResponse getDescribeResultAccessResponse()
	{
		return draResponse;
	}


	public DataComponent getSegmentParams()
	{
		return segmentParams;
	}
	
	
	public void setSensorID(String sensorID)
	{
		sRequest.setSensorID(sensorID);
		dtRequest.setSensorID(sensorID);
	}
	
	
	public void setTaskID(String taskID)
	{
		gsRequest.setTaskID(taskID);
		draRequest.setTaskID(taskID);
	}
	
	
	public void setServer(String server, String version)
	{
		sRequest.setPostServer(server);
		sRequest.setVersion(version);
		dtRequest.setGetServer(server);
		dtRequest.setVersion(version);
		gsRequest.setGetServer(server);
		gsRequest.setVersion(version);
		draRequest.setGetServer(server);
		draRequest.setVersion(version);
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
}