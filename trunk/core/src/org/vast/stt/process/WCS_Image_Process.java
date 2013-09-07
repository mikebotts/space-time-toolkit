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

package org.vast.stt.process;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataHandler;
import org.vast.cdm.common.DataStreamParser;
import org.vast.cdm.common.DataType;
import org.vast.data.*;
import org.vast.ows.OWSUtils;
import org.vast.ows.wcs.GetCoverageRequest;
import org.vast.ows.wcs.WCSResponseReader;
import org.vast.process.*;
import org.vast.sweCommon.SweConstants;
import org.vast.unit.UnitConversion;
import org.vast.unit.UnitConverter;
import org.vast.util.Bbox;
import org.vast.util.TimeExtent;


/**
 * <p><b>Title:</b><br/>
 * WCS Process
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Issues a request to a WCS server using provided parameters
 * and input bounding box and time, and outputs the resulting
 * coverage encapsulated in a data component structure.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Jan 20, 2006
 * @version 1.0
 */
public class WCS_Image_Process extends DataProcess implements DataHandler
{
    protected DataValue bboxLat1, bboxLon1, bboxLat2, bboxLon2;
    protected DataValue inputStartTime, inputStopTime, inputStepTime;
    protected DataValue outputGridWidth, outputGridLength;
    protected DataValue outputCoverageWidth, outputCoverageLength;
    protected DataArray outputGridArray, outputCoverageArray;
    protected DataGroup output;
    protected InputStream dataStream;
    protected GetCoverageRequest query;
    protected OWSUtils owsUtils;
    protected DataStreamParser dataParser;
    protected boolean hasTime, hasBbox;
    protected Hashtable<DataComponent, UnitConverter> converters;
    

    public WCS_Image_Process()
    {
        query = new GetCoverageRequest();
        owsUtils = new OWSUtils();
        converters = new Hashtable<DataComponent, UnitConverter>();
    }


    /**
     * Initializes the process
     * Gets handles to input/output components
     */
    public void init() throws ProcessException
    {
        int skipX, skipY, skipZ;
        DataComponent component;
        DataGroup input;
        
        try
        {
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
                inputStopTime = (DataValue)input.getComponent("stop");
                inputStepTime = (DataValue)input.getComponent("step");
                input.assignNewDataBlock();
                hasTime = true;
            }
            
            // grid output array
            output = (DataGroup)outputData.getComponent("coverageData");
            DataGroup domainData = (DataGroup)output.getComponent("domainData");            
            outputGridArray = (DataArray)domainData.getComponent("grid");            
            outputGridWidth = (DataValue)domainData.getComponent("width");
            outputGridLength = (DataValue)domainData.getComponent("length");
            
            // range output array
            DataGroup rangeData = (DataGroup)output.getComponent("rangeData");
            outputCoverageArray = (DataArray)rangeData.getComponent("coverage");
            outputCoverageWidth = (DataValue)rangeData.getComponent("width");
            outputCoverageLength = (DataValue)rangeData.getComponent("length");
        }
        catch (Exception e)
        {
            throw new ProcessException("Invalid I/O structure", e);
        }
        
        try
        {
            // Read parameter values (must be fixed!)
            DataGroup wcsParams = (DataGroup)paramData.getComponent("wcsOptions");
            
            // service end point url
            String url = wcsParams.getComponent("endPoint").getData().getStringValue();
            String requestMethod = wcsParams.getComponent("requestMethod").getData().getStringValue();
            if (requestMethod.equalsIgnoreCase("post"))
                query.setPostServer(url);
            else
                query.setGetServer(url);
            
            // version
            String version = wcsParams.getComponent("version").getData().getStringValue();
            query.setVersion(version);
            
            // layer ID
            String layerID = wcsParams.getComponent("layer").getData().getStringValue();
            query.setCoverage(layerID);
            
            // image format
            String format = wcsParams.getComponent("format").getData().getStringValue();
            query.setFormat(format);
            
            // skip factor along X
            component = wcsParams.getComponent("skipX");
            if (component != null)
            {
                skipX = component.getData().getIntValue();
                query.setSkipX(skipX);
            }
            
            // skip factor along Y
            component = wcsParams.getComponent("skipY");
            if (component != null)
            {
                skipY = component.getData().getIntValue();
                query.setSkipY(skipY);
            }
            
            // skip factor along Z
            component = wcsParams.getComponent("skipZ");
            if (component != null)
            {
                skipZ = component.getData().getIntValue();
                query.setSkipZ(skipZ);
            }
            
        }
        catch (Exception e)
        {
            throw new ProcessException("Invalid Parameters", e);
        }
    }


    /**
     * Executes process algorithm on inputs and set output data
     */
    public void execute() throws ProcessException
    {
        try
        {
            initRequest();
            WCSResponseReader reader = new WCSResponseReader();
            System.out.println(owsUtils.buildURLQuery(query));
            //  Hardwired to KVP request currently
            dataStream = owsUtils.sendGetRequest(query).getInputStream();
            reader.parse(dataStream);

            dataParser = reader.getDataParser();  // Just instantiates ASCII or BINARY parser and returns it
            dataParser.setDataHandler(this);
            dataParser.parse(reader.getDataStream());
            
            ((DataGroup)output.getComponent(0)).combineDataBlocks();
            ((DataGroup)output.getComponent(1)).combineDataBlocks();
            output.combineDataBlocks();
        }
        catch (Exception e)
        {
            String server = query.getPostServer();
            if (server == null)
                server = query.getGetServer();
            throw new ProcessException("Error while reading data from WCS server " + server, e);
        }
        finally
        {
            endRequest();
        }
    }
        
    
    protected void initRequest()
    {
        // make sure previous request is cancelled
        endRequest();
        outputGridWidth.renewDataBlock();
        outputGridLength.renewDataBlock();
        outputCoverageWidth.renewDataBlock();
        outputCoverageLength.renewDataBlock();
        
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
            Bbox bbox = query.getBbox();
            if (bbox == null) {
            	bbox = new Bbox();
            	query.setBbox(bbox);
            }
            bbox.setMinX(minX);
            bbox.setMaxX(maxX);
            bbox.setMinY(minY);
            bbox.setMaxY(maxY);
            // coverage and bbox crs (Hardwired for now)
            bbox.setCrs("EPSG:4326");
        }
        
        // read time range
        if (hasTime)
        {
            double start = inputStartTime.getData().getDoubleValue();
            double stop = inputStopTime.getData().getDoubleValue();
            double step = inputStepTime.getData().getDoubleValue();
            
            //  Ensure at least one time extent exists in query, or NP gets thrown by 
            //  query.getTime().set*() below
            TimeExtent time = query.getTime();
            if(time == null) {
            	time = new TimeExtent();
            	query.setTime(time);
            }
            
            if (start == TimeExtent.NOW && stop == TimeExtent.NOW)
            {
                time.setBaseAtNow(true);
            }
            else
            {
                if (start == TimeExtent.NOW)
                    time.setBeginNow(true);
                else
                	time.setStartTime(start);
                
                if (stop == TimeExtent.NOW)
                	time.setEndNow(true);
                else
                	time.setStopTime(stop);
            }
            
            time.setTimeStep(step);
        }
    }
    
    
    protected void endRequest()
    {
        try
        {
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


    public void beginDataAtom(DataComponent info)
    {
        // TODO Auto-generated method stub
        
    }


    public void endData(DataComponent info, DataBlock data)
    {
        DataArray gridArray = (DataArray)info.getComponent("domainData").getComponent("grid");
        DataArray coverageArray = (DataArray)info.getComponent("rangeData").getComponent("coverage");
        
        // set grid size
        int gridWidth = gridArray.getComponent(0).getComponentCount();
        int gridLength = gridArray.getComponentCount();                
        outputGridWidth.getData().setIntValue(gridWidth);
        outputGridLength.getData().setIntValue(gridLength);
        
        // set coverage size
        int coverageWidth = coverageArray.getComponent(0).getComponentCount();
        int coverageLength = coverageArray.getComponentCount();                
        outputCoverageWidth.getData().setIntValue(coverageWidth);
        outputCoverageLength.getData().setIntValue(coverageLength);
        
        DataBlock gridData = gridArray.getData();
        DataBlock coverageData = coverageArray.getData();
        
        outputGridArray.setData(gridData);
        outputCoverageArray.setData(coverageData);
    }


    public void endDataAtom(DataComponent info, DataBlock data)
    {
        DataType dataType = data.getDataType();
        
        // TODO unit conversion WILL be handled by global chain unit converters
        if (dataType != DataType.UTF_STRING && dataType != DataType.ASCII_STRING)
        {
            UnitConverter converter = converters.get(info);
            
            if (converter == null)
            {
                String uom = (String)info.getProperty(SweConstants.UOM_CODE);
                converter = UnitConversion.createConverterToSI(uom);
                converters.put(info, converter);
            }
            
            // convert to SI
            double newVal = converter.convert(data.getDoubleValue());
            data.setDoubleValue(newVal);
        }        
    }

    
    public void endDataBlock(DataComponent info, DataBlock data)
    {
        //// TODO Auto-generated method stub
        //System.err.println("End DB #" + dbc++);
    }


    public void startData(DataComponent info)
    {
        // TODO Auto-generated method stub
        
    }


    public void startDataBlock(DataComponent info)
    {
        // TODO Auto-generated method stub
        
    }    
}