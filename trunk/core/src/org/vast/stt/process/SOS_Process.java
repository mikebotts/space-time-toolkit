/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.process;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataComponent;
import org.ogc.cdm.common.DataHandler;
import org.ogc.cdm.common.DataType;
import org.ogc.cdm.reader.DataStreamParser;
import org.vast.data.*;
import org.vast.ows.sos.SOSObservationReader;
import org.vast.ows.sos.SOSQuery;
import org.vast.ows.sos.SOSRequestWriter;
import org.vast.process.*;
import org.vast.unit.UnitConversion;
import org.vast.unit.UnitConverter;


/**
 * <p><b>Title:</b><br/>
 * SOS_Process
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Issues a request to a SOS server using provided parameters
 * and input, and outputs the resulting data block per block.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jan 20, 2006
 * @version 1.0
 */
public class SOS_Process extends DataProcess implements DataHandler
{
    protected DataValue bboxLat1, bboxLon1, bboxLat2, bboxLon2;
    protected DataValue inputStartTime, intputStopTime, inputStepTime;
    protected DataComponent outputObsInfo, outputObsData;
    protected InputStream dataStream;
    protected SOSQuery query;
    protected SOSRequestWriter requestBuilder;
    protected DataStreamParser dataParser;
    protected Thread workerThread;
    protected boolean hasTime, hasBbox; 
    protected boolean done, error, outputReady;
    protected Hashtable<DataComponent, UnitConverter> converters;
    

    public SOS_Process()
    {
        query = new SOSQuery();
        requestBuilder = new SOSRequestWriter();
        converters = new Hashtable<DataComponent, UnitConverter>();
    }


    /**
     * Initializes the process
     * Gets handles to input/output components
     */
    public void init() throws ProcessException
    {
        // Read I/O mappings
        try
        {
            DataGroup input;
            
            // Optional bbox input mappings
            if (inputData.existComponent("bbox"))
            {
                input = (DataGroup)inputData.getComponent("bbox");
                bboxLat1 = (DataValue)input.getComponent("corner1").getComponent(0);
                bboxLon1 = (DataValue)input.getComponent("corner1").getComponent(1);
                bboxLat2 = (DataValue)input.getComponent("corner2").getComponent(0);
                bboxLon2 = (DataValue)input.getComponent("corner2").getComponent(1);
                input.assignNewDataBlock();
                hasBbox = true;
            }
            
            // optional time input mappings
            if (inputData.existComponent("time"))
            {
                input = (DataGroup)inputData.getComponent("time");
                inputStartTime = (DataValue)input.getComponent("start");
                intputStopTime = (DataValue)input.getComponent("stop");
                inputStepTime = (DataValue)input.getComponent("step");
                input.assignNewDataBlock();
                hasTime = true;
            }
            
            // Output Data mapping
            outputObsInfo = outputData.getComponent(0);
            outputObsData = outputData.getComponent(1);
        }
        catch (Exception e)
        {
            throw new ProcessException(ioError, e);
        }
        
        // Read parameters mappings + values
        try
        {
            DataGroup sosParams = (DataGroup)paramData.getComponent("sosOptions");
            
            // service end point url
            String url = sosParams.getComponent("endPoint").getData().getStringValue();
            String requestMethod = sosParams.getComponent("requestMethod").getData().getStringValue();
            if (requestMethod.equalsIgnoreCase("post"))
                query.setPostServer(url);
            else
                query.setGetServer(url);
            
            // version
            String version = sosParams.getComponent("version").getData().getStringValue();
            query.setVersion(version);
            
            // offering ID
            String offeringID = sosParams.getComponent("offering").getData().getStringValue();
            query.setOffering(offeringID);
            
            // observable ID
            String observables = sosParams.getComponent("observables").getData().getStringValue();
            String[] obsArray = observables.split(" ");
            for (int i=0; i<obsArray.length; i++)
                query.getObservables().add(obsArray[i]);
            
            // response format
            String format = sosParams.getComponent("format").getData().getStringValue();
            query.setFormat(format);

            // change exception type
            query.setRequest("GetObservation");
        }
        catch (Exception e)
        {
            throw new ProcessException("Invalid Parameters", e);
        }
        
        // check that output is compatible with reqiested data
        checkData();
        done = true;
        needSync = true;
    }
    
    
    protected void checkData() throws ProcessException
    {
        // TODO check if returned data is compatible with output + call at init...
        endRequest();        
    }
    
    
    /**
     * Executes process algorithm on inputs and set output data
     */
    public void execute() throws ProcessException
    {
        // get input variables only if previous request is done
        if (done)
        {
            final DataHandler handler = this;
            Runnable initiateRequest = new Runnable()
            {
                public void run()
                {
                    try
                    {    
                        // init request using spatial + time extent
                        initRequest();
                        
                        // create reader
                        SOSObservationReader reader = new SOSObservationReader();
                        
                        // select request type (post or get)
                        boolean usePost = (query.getPostServer() != null);
                        dataStream = requestBuilder.sendRequest(query, usePost);
                            
                        // parse response
                        reader.parse(dataStream);
                        dataParser = reader.getDataParser();
                        dataParser.setDataHandler(handler);
                        
                         // start parsing
                        dataParser.parse(reader.getDataStream());
                        done = true;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        error = true;
                    }
                    finally
                    {
                        endRequest();
                    }       
                }
            };
            
            workerThread = new Thread(initiateRequest, "SOS Process Parser");
            workerThread.start();
            done = false;
            
            // set inputs as not needed so that we can continue looping
            for (int i=0; i<inputConnections.size(); i++)
                inputConnections.get(i).setNeeded(false);
        }
        
        // now let the worker thread run one more time
        synchronized (this)
        {
            // let the request thread parse one more block
            try
            {
                outputReady = false;
                this.notify();                
                while (!outputReady)
                    this.wait();
            }
            catch (InterruptedException e)
            {
            }
            
            // if an error occured in the worker thread
            if (error)
            {
                String server = query.getPostServer();
                if (server == null)
                    server = query.getGetServer();
                throw new ProcessException("Error while reading data from " + server);
            }
        
            if (done)
            {
                // set inputs as needed since loop has ended
                for (int i=0; i<inputConnections.size(); i++)
                    inputConnections.get(i).setNeeded(true);
            }
        }
    }    
    
    
    /**
     * Reads all input parameters and set up query accordingly
     */
    protected void initRequest()
    {
        // make sure previous request is cancelled
        endRequest();
        
        // read lat/lon bbox
        if (hasBbox)
        {
            double lon1 = bboxLon1.getData().getDoubleValue();///Math.PI*180;
            double lat1 = bboxLat1.getData().getDoubleValue();///Math.PI*180;
            double lon2 = bboxLon2.getData().getDoubleValue();///Math.PI*180;
            double lat2 = bboxLat2.getData().getDoubleValue();///Math.PI*180;
            double minX = Math.min(lon1, lon2);
            double maxX = Math.max(lon1, lon2);
            double minY = Math.min(lat1, lat2);
            double maxY = Math.max(lat1, lat2);        
            query.getBbox().setMinX(minX);
            query.getBbox().setMaxX(maxX);
            query.getBbox().setMinY(minY);
            query.getBbox().setMaxY(maxY);
        }
        
        // read time range
        if (hasTime)
        {
            double start = inputStartTime.getData().getDoubleValue();
            double stop = intputStopTime.getData().getDoubleValue();
            double step = inputStepTime.getData().getDoubleValue();
            
            if (Double.isInfinite(start))
                query.getTime().setBeginNow(true);
            else
                query.getTime().setStartTime(start);
            
            if (Double.isInfinite(stop))
                query.getTime().setEndNow(true);
            else
                query.getTime().setStopTime(stop);
            
            query.getTime().setTimeStep(step);
        }
    }
    
    
    /**
     * Makes sure request is ended and stream is closed
     */
    protected void endRequest()
    {
        try
        {
            if (dataStream != null)
                dataStream.close();
            
            dataStream = null;
            
            if (dataParser != null)
                dataParser.stop();
        }
        catch (IOException e)
        {
        }
    }
    
    
    @Override
    public void dispose()
    {
        endRequest();
        
        synchronized (this)
        {
            this.notify();
        }        
    }
    
    
    public void endData(DataComponent info, DataBlock data)
    {
        try
        {
            //System.out.println("send block");
            
            synchronized (this)
            {
                outputObsData.setData(data);
                outputReady = true;
                this.notify();
                this.wait();
            }
        }
        catch (InterruptedException e)
        {
        }
    }


    public void endDataAtom(DataComponent info, DataBlock data)
    {
        DataType dataType = data.getDataType();
        
        if (dataType != DataType.UTF_STRING && dataType != DataType.ASCII_STRING)
        {
            UnitConverter converter = converters.get(info);
            
            if (converter == null)
            {
                String uom = (String)info.getProperty(DataComponent.UOM);
                converter = UnitConversion.createConverterToSI(uom);
                converters.put(info, converter);
            }
            
            // convert to SI
            double newVal = converter.convert(data.getDoubleValue());
            data.setDoubleValue(newVal);
        }
        
        //System.out.println(info.getName() + ": " + data.getStringValue());
    }


    public void endDataBlock(DataComponent info, DataBlock data)
    {
    }


    public void startData(DataComponent info)
    {
    }


    public void startDataBlock(DataComponent info)
    {
    }


    public void beginDataAtom(DataComponent info, DataBlock data)
    {       
    }
}