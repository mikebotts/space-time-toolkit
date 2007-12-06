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
    Johannes Echterhoff <echterhoff@uni-muenster.de>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.process;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.opengis.sps.x10.DescribeResultAccessDocument;
import net.opengis.sps.x10.DescribeResultAccessRequestResponseDocument;
import net.opengis.sps.x10.DescribeResultAccessRequestType;
import net.opengis.sps.x10.DescribeTaskingDocument;
import net.opengis.sps.x10.DescribeTaskingRequestResponseDocument;
import net.opengis.sps.x10.DescribeTaskingRequestType;
import net.opengis.sps.x10.InputParameterType;
import net.opengis.sps.x10.SubmitDocument;
import net.opengis.sps.x10.SubmitRequestResponseDocument;
import net.opengis.sps.x10.InputDescriptorDocument.InputDescriptor;
import net.opengis.sps.x10.SubmitRequestResponseDocument.SubmitRequestResponse.Status;
import net.opengis.sps.x10.DescribeResultAccessRequestResponseDocument.DescribeResultAccessRequestResponse;
import net.opengis.sps.x10.DescribeResultAccessRequestResponseDocument.DescribeResultAccessRequestResponse.Service;
import net.opengis.sps.x10.DescribeTaskingRequestResponseType.TaskingDescriptor;
import net.opengis.swe.x10.BooleanDocument;
import net.opengis.swe.x10.CategoryDocument;
import net.opengis.swe.x10.CountDocument;
import net.opengis.swe.x10.QuantityDocument;
import net.opengis.swe.x10.TextDocument;
import net.opengis.swe.x10.TimeDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataType;
import org.vast.data.*;
import org.vast.ows.sps.SPSConstants;
import org.vast.ows.sps.SPSException;
import org.vast.ows.sps.SPSSubmitRequest;
import org.vast.ows.sps.SPSUtil;
import org.vast.ows.sps.ServiceException;
import org.vast.process.*;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * <p><b>Title:</b><br>
 * SOS_Process
 * </p>
 *
 * <p><b>Description:</b><br>
 * Issues a request to a SPS server using provided parameters
 * and input, and outputs the SPS response to the request and
 * the service and URL where data are stored if available.
 * </p>
 * 
 * <p><b>Limitations:</b><br>
 * Right now only the following input types are supported: Boolean, Category, 
 * Quantity, Count, Text, Time, SimpleDataRecord, Vector, DataRecord and Position 
 * <br>
 * NormalizedCurve, AbstractDataArrays or any components that take these as 
 * child are not supported. 
 * <br>
 * This process only supports SPSs that use InputDescriptors with cardinality 1.
 * </p>
 * <p>Copyright (c) 2007</p>
 * @author Gregoire Berthiau and Johannes Echterhoff
 * @date Nov 29, 2007
 * @version 1.0
 */
public class SPS_Process extends DataProcess
{
    private static final String CONFIRMED = "confirmed";
    private static final String REJECTED = "rejected";
    private static final String PENDING = "pending";
    
    private static final String NA = "N/A";
          
    // inputs are handled automatically
    
    // outputs
    protected DataComponent responseData, dataRetrievalService;
    protected DataComponent serviceTuple, serviceType, serviceURL, serviceRequest;
    
    // parameters
//    protected DataComponent spsRequestParametersData, queryEndPoint, queryVersion, querySensorID;
        
    
    protected SPSSubmitRequest submitRequest;
    
    protected Map<String,DataComponent> inputMap = new HashMap<String,DataComponent>();
    
    /**
    * key: parameterID | value: InputDescriptor (XMLBeans)
    */
   protected Map<String,InputDescriptor> descriptorMap = new HashMap<String,InputDescriptor>();
   
    protected URL spsUrl;
    protected String sensorID;
    
    
    protected String wnsId;
    protected String wnsUrl;
   protected boolean treatPendingAsRejected;

    
    public void init() throws ProcessException
    {
        // Read I/O mappings
        try
        {
         
         for(int i=0; i<inputData.getComponentCount(); i++) {
            DataComponent input = inputData.getComponent(i);
            this.inputMap.put(input.getName(),input);            
         }
               
         // initialize / get output elements
         responseData = outputData.getComponent("response");
         
         dataRetrievalService = outputData.getComponent("dataRetrievalService");
        	serviceTuple = dataRetrievalService.getComponent("serviceTuple");
        	serviceType = serviceTuple.getComponent("serviceType");
        	serviceURL = serviceTuple.getComponent("serviceURL");
        	serviceRequest = serviceTuple.getComponent("request");
        	
        	// initialize / get parameters
        	DataComponent spsRequestParametersData = paramData.getComponent("spsRequestParameters");
        	DataComponent queryEndPoint = spsRequestParametersData.getComponent("endPoint");
        	// right now we do not need the version parameter
        	DataComponent queryVersion = spsRequestParametersData.getComponent("version");
        	DataComponent querySensorID = spsRequestParametersData.getComponent("sensorID");
        	DataComponent queryWnsId = spsRequestParametersData.getComponent("wnsId");
        	DataComponent queryWnsUrl = spsRequestParametersData.getComponent("wnsUrl");
        	DataComponent paramPendingAsRejected = spsRequestParametersData.getComponent("treatPendingAsRejected");
         
        	spsUrl = new URL(queryEndPoint.getData().getStringValue());
        	sensorID = querySensorID.getData().getStringValue();
        	wnsId = queryWnsId.getData().getStringValue();
        	wnsUrl = queryWnsUrl.getData().getStringValue();
        	treatPendingAsRejected = paramPendingAsRejected.getData().getBooleanValue();
        	
        	// perform a DescribeTasking request to retrieve parameter descriptors
        	DescribeTaskingDocument dtd = DescribeTaskingDocument.Factory.newInstance();         	
        	DescribeTaskingRequestType dtrt = dtd.addNewDescribeTasking();
        	dtrt.setVersion(SPSConstants.VERSION);
        	dtrt.setService(SPSConstants.SERVICE);
        	dtrt.addNewSensorID().setStringValue(sensorID);
        	
        	XmlObject response = SPSUtil.sendPOSTRequest(dtd,spsUrl);
        	DescribeTaskingRequestResponseDocument dtrrd = (DescribeTaskingRequestResponseDocument)response;
        	TaskingDescriptor td = dtrrd.getDescribeTaskingRequestResponse().getTaskingDescriptorArray(0);
        	
        	InputDescriptor[] descriptors = td.getInputDescriptorArray();
        	for(InputDescriptor descriptor : descriptors) {
        	   
        	 String paramId = descriptor.getParameterID();
        	 
        	   if(descriptor.isSetCardinality()) {
        	     String cardinality = descriptor.getCardinality().toString().trim();
        	     if(cardinality.equalsIgnoreCase("unbounded") || !cardinality.equals("1")) {
        	        throw new ProcessException("SPS uses InputDescriptor with " +
        	        		"cardinality != 1 for parameter "+paramId+" which is not supported right now.");
        	     }
        	   }        	   
        	   
        	   descriptorMap.put(paramId, descriptor);
        	 
        	   // if the descriptor is for a mandatory parameter, make sure that
        	   // an input of the process matches it
        	   if(descriptor.getUse().intValue() == InputDescriptor.Use.INT_REQUIRED) {
        	      if(!this.inputMap.containsKey(paramId)) {
        	         throw new ProcessException("Mandatory parameter "+paramId
        	               +" is not contained in inputs of this process.");
        	      }
        	   }
        	}
        	
        	// now make sure that for each input parameter of this process there
        	// also exists an InputDescriptor with same name/parameterID
        	for(String inputName : this.inputMap.keySet()) {
        	   if(!this.descriptorMap.containsKey(inputName)) {
        	      throw new ProcessException("The SPS does not support a " +
        	      		"parameter called "+inputName);
        	   }
        	}
        	
        	// now create the SubmitRequest
        	this.submitRequest = new SPSSubmitRequest(wnsId,wnsUrl,sensorID,descriptorMap, this.inputMap.keySet());
        	
        }
        catch (Exception e)
        {
            throw new ProcessException(ioError, e);
        }
    }
    
    
//    @Override
//    public void reset() throws ProcessException
//    {
//        endRequest();
//        outputReady = false;
//        done = true;        
//        error = false;        
//        lastException = null;
//        for (int i=0; i<inputConnections.size(); i++)
//            inputConnections.get(i).setNeeded(true);
//    }
//    
    
//    protected void checkData() throws ProcessException
//    {
//        //TODO check that output is compatible with SOS data
//    }


    public void execute() throws ProcessException
    {
    	
       // populate Submit request
       SubmitDocument request = (SubmitDocument) this.submitRequest.getSubmitRequest().copy();
       
       InputParameterType[] params = request.getSubmit().getSensorParam().getParameters().getInputParameterArray();
       
       for(InputParameterType param : params) {
          this.insertValues(param,inputMap.get(param.getParameterID()));
          System.out.println(param);
       }
       
    	// send request, read response
       SubmitRequestResponseDocument srd = null;
       String taskID = null;
       
       try {
          
         srd = (SubmitRequestResponseDocument)SPSUtil.sendPOSTRequest(request, this.spsUrl);
         taskID = srd.getSubmitRequestResponse().getTaskID();
         
       } catch (SPSException e) {
         throw new ProcessException("Exception when sending Submit request to SPS "+spsUrl,e);
      } catch (ServiceException e) {
         throw new ProcessException("ServiceException occurred at SPS "+spsUrl,e);
      }
    	
    	// depending on response value, set outputs
      Status.Enum responseStatus = srd.getSubmitRequestResponse().getStatus();
      
    	String response;
    	
    	if(responseStatus.equals(Status.CONFIRMED)) {
    	   response = CONFIRMED;
    	} else if(responseStatus.equals(Status.PENDING) && !this.treatPendingAsRejected) {
    	   response = PENDING;
    	  throw new ProcessException("No support for WNS right now. Pending requests can therefore not be handled.");
        // TODO: you would probably have to create a Thread which pulls the SPS until services are available
    	} else {
    	   response = REJECTED;
    	}
      
    	// create DescribeResultAccess request
    	
    	// check on whether the SPS provides result access for the taskID
    	DescribeResultAccessDocument dradTaskID = DescribeResultAccessDocument.Factory.newInstance();
    	DescribeResultAccessRequestType drartTaskID = dradTaskID.addNewDescribeResultAccess();
    	drartTaskID.setVersion("1.0.0");
    	drartTaskID.setService("SPS");
    	drartTaskID.setTaskID(taskID);
    	
    	DescribeResultAccessRequestResponseDocument drarrd = null;
    	
    	try {
    	   
    	  drarrd = (DescribeResultAccessRequestResponseDocument)SPSUtil.sendPOSTRequest(dradTaskID, spsUrl);
    	  
      } catch (SPSException e1) {

         throw new ProcessException("Exception when sending Submit request to SPS "+spsUrl,e1);
         
      } catch (ServiceException e1) {
         
         // if the SPS does not remember or recognise the given taskID in the 
         // DescribeResultAccess operation, perform the operation with sensorID
         // to retrieve the default data services for that sensor
         if(e1.containsCode(ServiceException.ExceptionCode.TaskIDExpired)) {
            
            try {
               
               DescribeResultAccessDocument dradSensorID = DescribeResultAccessDocument.Factory.newInstance();
               DescribeResultAccessRequestType drartSensorID = dradSensorID.addNewDescribeResultAccess();
               drartSensorID.setVersion("1.0.0");
               drartSensorID.setService("SPS");
               drartSensorID.addNewSensorID().setStringValue(this.sensorID);
                              
               drarrd = (DescribeResultAccessRequestResponseDocument)SPSUtil.sendPOSTRequest(dradSensorID, spsUrl);
               
             } catch (SPSException e2) {

                throw new ProcessException("Exception when sending Submit request to SPS "+spsUrl,e2);
                
             } catch (ServiceException e2) {
                
                throw new ProcessException("ServiceException occurred at SPS "+spsUrl,e2);
                
             }
             
         } else {
            throw new ProcessException("ServiceException occurred at SPS "+spsUrl,e1);
         }
      }
    	
    	// depending on response value, set outputs
    	responseData.getData().setStringValue(response);

    	DescribeResultAccessRequestResponse drarr = drarrd.getDescribeResultAccessRequestResponse();
    	if(drarr.isSetDataNotAvailable()) {
    	   this.serviceType.getData().setStringValue(NA);
    	   this.serviceURL.getData().setStringValue(NA);
    	   this.serviceRequest.getData().setStringValue(NA);
    	} else {
    	   
    	   Service[] serviceElements = drarr.getServiceArray();
    	   StringWriter writer;
    	   
    	   for(int i=0; i<serviceElements.length; i++) {
    	      
    	     this.serviceType.getData().setStringValue(i,serviceElements[i].getServiceType());
           this.serviceURL.getData().setStringValue(i,serviceElements[i].getServiceURL());
           
           if(serviceElements[i].isSetRequest()) {
              writer = new StringWriter();
              
              try {
                 
               serviceElements[i].getRequest().save(writer);
               this.serviceRequest.getData().setStringValue(i,writer.toString());
               
            } catch (IOException e) {
               throw new ProcessException("Could not store request element data from DescribeResultAccess response.",e);
            }
           } else {
              this.serviceRequest.getData().setStringValue(i,NA);
           }
    	   }
    	}
    	
    }    
    
    
    private void insertValues(InputParameterType param,
         DataComponent dataComponent) throws ProcessException {

//      Node n = param.getValueArray(0).getDomNode();
       XmlCursor cursor = param.getValueArray(0).newCursor();
      
      LinkedList<String> values = new LinkedList<String>();
      this.parseComponent(values, dataComponent);
      
      this.populateValues(cursor,values);
      
//      System.out.println(param);
      cursor.dispose();
   }
    
    /**
    * Recursively goes through the given DataComponent and stores all basic 
    * values into the given FIFO queue.
    * 
    * @param valueList
    * @param component
    * @throws ProcessException if invalid element types are encountered
    */
   protected void parseComponent(LinkedList<String> valueList, DataComponent component) throws ProcessException {
       
       if(component == null) return;
       
       if(component instanceof DataArray) 
          throw new ProcessException("This process cannot handle DataArrays.");
       
       int componentCount = component.getComponentCount();
       
       if(componentCount==0){
          valueList.add(getComponentValue(component));
       } else {
          
          for(int j=0; j<componentCount; j++){
             parseComponent(valueList,component.getComponent(j));
          }
       }
     }
    
    protected String getComponentValue(DataComponent component) {
       
       DataType Type = component.getData().getDataType();
       
       String result = null;
       
       if(Type == DataType.DOUBLE){
          result = ""+component.getData().getDoubleValue();
       }
       
       if(Type == DataType.BOOLEAN){
          result = ""+component.getData().getBooleanValue();
       }
       
       if(Type == DataType.FLOAT){
          result = ""+component.getData().getFloatValue();
       }
       
       if((Type == DataType.ASCII_STRING)||(Type == DataType.UTF_STRING)){
          result = component.getData().getStringValue();
       }
       
       if((Type == DataType.BYTE)||(Type == DataType.UBYTE)){
          result = ""+component.getData().getByteValue();
       }
       
       if((Type == DataType.INT)||(Type == DataType.UINT)){
          result = ""+component.getData().getIntValue();
       }
       
       if((Type == DataType.LONG)||(Type == DataType.ULONG)){
          result = ""+component.getData().getLongValue();
       }
       
       if((Type == DataType.SHORT)||(Type == DataType.USHORT)){
          result = ""+component.getData().getShortValue();
       }
       
       return result;
    }


   private void populateValues(XmlCursor cursor, LinkedList<String> values) throws ProcessException {
      
      while(cursor.hasNextToken()) {
         
//         if(cursor.isText()) {
//            System.out.println(cursor.currentTokenType()
//              + " " + cursor.getChars());
//          } else {
//            System.out.println(cursor.currentTokenType()
//            + " " + cursor.getName());
//          }
         
         TokenType ttype = cursor.currentTokenType();
//         System.out.println(cursor.getName());
         if(ttype.equals(TokenType.START)) {
            
            String name = cursor.getName().getLocalPart();
            
            if(name.equals("Count")  || name.equals("Quantity") || 
                name.equals("Boolean") || name.equals("Text") || 
                name.equals("Category") || name.equals("Time")) {
               
               boolean wasMoved = cursor.toChild("http://www.opengis.net/swe/1.0", "value");
               
               if(wasMoved) {
                  cursor.setTextValue(values.poll());
               } else {
                  cursor.toEndToken();
                  cursor.insertElementWithText("value", "http://www.opengis.net/swe/1.0", values.poll());
//                  cursor.beginElement("value", "http://www.opengis.net/swe/1.0");
//               cursor.insertChars(values.poll());
               }
            }
         }        
//         System.out.println("1");
         cursor.toNextToken();
      }
      
      
      
      
      
      
      
      
      
      
      
      
//      if(n.getNodeType() == Node.ELEMENT_NODE) {
//         
//         String name = n.getNodeName();
//         
////         try {
////            if(name.equals("Count")) {            
////               CountDocument cd = CountDocument.Factory.parse(n);
////               cd.getCount().setValue(new BigInteger(values.poll()));
////               n = cd.getDomNode();
////            } else if(name.equals("Quantity")) {    
////               
////               XmlObject xobj = XmlObject.Factory.parse(n);
////               XmlCursor cursor = xobj.newCursor();
////               boolean moved = cursor.toChild("http://www.opengis.net/swe/1.0", "value");
////               if(moved) {
////                  cursor.setTextValue(values.poll());
////               } else {
////                  cursor.toEndToken();
////                  cursor.insertElementWithText("value", "http://www.opengis.net/swe/1.0", values.poll());
////               }
////               cursor.dispose();
////               
//////               QuantityDocument qd = QuantityDocument.Factory.parse(n);
//////               System.out.println(qd.getQuantity().getValue());
//////               qd.getQuantity().setValue(Double.parseDouble(values.poll()));
//////               System.out.println("1: "+qd);
////               
//////               n = qd.getDomNode();
////            } else if(name.equals("Text")) {            
////               TextDocument td = TextDocument.Factory.parse(n);
////               td.getText().setValue(values.poll());
////            } else if(name.equals("Boolean")) {            
////               BooleanDocument bd = BooleanDocument.Factory.parse(n);
////               bd.getBoolean().setValue(Boolean.parseBoolean(values.poll()));
////            } else if(name.equals("Category")) {            
////               CategoryDocument cd = CategoryDocument.Factory.parse(n);
////               cd.getCategory().setValue(values.poll());
////            } else if(name.equals("Time")) {            
////               TimeDocument td = TimeDocument.Factory.parse(n);
////               td.getTime().setValue(values.poll());
//            
//               
//               
//             if(name.equals("Count")  || name.equals("Quantity") || 
//               name.equals("Boolean") || name.equals("Text") || 
//               name.equals("Category") || name.equals("Time")) {
//
////               try {
//                  
//                  boolean valueFound = false;
//                  
//                  NodeList children = n.getChildNodes();
//                  
//                  for(int i=0; i<children.getLength(); i++) {
//                     
//                     Node child = children.item(i);
//                     
//                     if(child.getNodeType() == Node.ELEMENT_NODE) {
//                        
//                        if(child.getNodeName().equals("value")) {
//                           valueFound = true;
//                           child.setTextContent(values.poll());
//                           break;
//                        }                        
//                        
//                     } else {
//                        continue;
//                     }
//                  }
//                  
//                  if(!valueFound) {
//                     
//                     Element valueNode;
//                     
//                     Document doc = n.getOwnerDocument();
//                     
//                     String swePrefix = "swe";
//                     if(swePrefix == null) {
//                        valueNode = doc.createElementNS(n.getNamespaceURI(), "value");
//                     } else {
//                        valueNode = doc.createElementNS(n.getNamespaceURI(), swePrefix+":value");
//                     }
////                     System.out.println(values.poll());
////                     valueNode.setNodeValue(values.poll());
//                     XmlObject xobj;
//                     try {
//                        xobj = XmlObject.Factory.parse(valueNode);
//                        XmlCursor cursor = xobj.newCursor();
////                        cursor.
//                        cursor.setTextValue(values.poll());
////                        cursor.insertChars(values.poll());
//                        
////                        n.getOwnerDocument().importNode(xobj.getDomNode(),true);
//                        cursor.dispose();
//                        System.out.println(xobj);
//                     } catch (XmlException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                     }
//                     
//                     
////                     NodeList valueChildren = valueNode.getChildNodes();
////                     for(int j=0; j<valueChildren.getLength(); j++) {
////                        Node tmp = valueChildren.item(j);
////                        if(tmp.getNodeType() == Node.TEXT_NODE) {
////                           System.out.println("foundit");
////                           tmp.setNodeValue(values.poll());
////                        }
////                     }
//                     
////                     n.appendChild(valueNode);
//                  }
//                  
////                  DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
////                  Document doc = builder.newDocument();
////                  Node nodeCopy = doc.importNode(n, true);
////                  
////                  DOMHelper helper = new DOMHelper(doc);
////                  
////                  Element valueElement = null;
////                  
////                  if(helper.existElement("value")) {
////                     
////                     valueElement = helper.getElement("value");
////                     
////                  } else {
////                     // create value element
////                     valueElement = helper.createElement("value");
////                  }
////                  
////                  if(values.isEmpty()) {
////                     throw new ProcessException("Number of given input values does not match required number of values.");
////                  } else {
////                     valueElement.setTextContent(values.poll());
////                     n = nodeCopy;
////                  }
//                  
////               } catch (ParserConfigurationException e) {
////                  throw new ProcessException("Could not create document.",e);
////               }
//            
//            } else {
//               
//               NodeList list = n.getChildNodes();
//               
//               if(list == null || list.getLength() == 0) 
//                  return;
//               else {
//                  for(int i=0; i<list.getLength(); i++) {
//                     Node childNode = list.item(i);
//                     this.populateValues(cursor, values);
//                  }
//               }
//            }
////         } catch(XmlException e) {
////            throw new ProcessException("Exception while assigning values to parameter.",e);
////         }
//         
//         
//      } else {
//         
//         // we are not interested in any other type of node (attribute, comment
//         // etc.), so just go on         
//         
//      }
      
   }

}