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
import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataComponent;
import org.ogc.cdm.common.DataHandler;
import org.ogc.cdm.reader.DataStreamParser;
import org.vast.data.*;
import org.vast.io.xml.DOMReader;
import org.vast.ows.wcs.CoverageReader;
import org.vast.ows.wcs.WCSQuery;
import org.vast.ows.wcs.WCSRequestWriter;
import org.vast.process.*;
import org.vast.sensorML.reader.ProcessLoader;
import org.vast.sensorML.reader.ProcessReader;


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
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jan 20, 2006
 * @version 1.0
 */
public class WCS_Image_Process extends DataProcess implements DataHandler
{
    protected DataValue bboxLat1, bboxLon1, bboxLat2, bboxLon2;
    protected DataValue outputGridWidth, outputGridLength;
    protected DataValue outputCoverageWidth, outputCoverageLength;
    protected DataArray outputGridArray, outputCoverageArray;
    protected DataGroup output;
    protected InputStream dataStream;
    protected WCSQuery query;
    protected WCSRequestWriter requestBuilder;
    protected DataStreamParser dataParser;
    

    public WCS_Image_Process()
    {
        query = new WCSQuery();
        requestBuilder = new WCSRequestWriter();
    }


    /**
     * Initializes the process
     * Gets handles to input/output components
     */
    public void init() throws ProcessException
    {
        int skipX, skipY, skipZ;
        DataComponent component;
        
        try
        {
            // Input mappings
            DataGroup input = (DataGroup)inputData.getComponent("bbox");
            bboxLat1 = (DataValue)input.getComponent("corner1").getComponent(0);
            bboxLon1 = (DataValue)input.getComponent("corner1").getComponent(1);
            bboxLat2 = (DataValue)input.getComponent("corner2").getComponent(0);
            bboxLon2 = (DataValue)input.getComponent("corner2").getComponent(1);
            input.assignNewDataBlock();
            
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
            query.setLayer(layerID);
            
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
            
            // coverage and bbox crs 
            query.setSrs("EPSG:4329");
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
            CoverageReader reader = new CoverageReader();
            dataStream = requestBuilder.sendRequest(query, false);
            reader.parse(dataStream);
          
            dataParser = reader.getDataParser();  // Just instantiates ASCII or BINARY parser and returns it
            DataComponent serverData = dataParser.getDataComponents();
            dataParser.setDataHandler(this);            
            dataParser.parse(reader.getDataStream());
            
            // set grid size
            DataArray gridArray = (DataArray)serverData.getComponent("coverage");
            int gridWidth = gridArray.getComponent(0).getComponentCount();
            int gridLength = gridArray.getComponentCount();                
            outputGridWidth.getData().setIntValue(gridWidth);
            outputGridLength.getData().setIntValue(gridLength);
            
            // set coverage size
            DataArray coverageArray = (DataArray)serverData.getComponent("imagery");
            int coverageWidth = coverageArray.getComponent(0).getComponentCount();
            int coverageLength = coverageArray.getComponentCount();                
            outputCoverageWidth.getData().setIntValue(coverageWidth);
            outputCoverageLength.getData().setIntValue(coverageLength);
            
            output.combineDataBlocks();
        }
        catch (Exception e)
        {
            throw new ProcessException("Error while requesting data from WCS server:\n" +  e);
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


    public void beginDataAtom(DataComponent info, DataBlock data)
    {
        // TODO Auto-generated method stub
        
    }


    public void endData(DataComponent info, DataBlock data)
    {
        //Object obj = ((AbstractDataBlock)data).getUnderlyingObject();
        //DataBlock gridData = ((DataBlock[])obj)[0];
        //DataBlock coverageData = ((DataBlock[])obj)[1];
        
        //DataBlock gridData = info.getComponent(0).getData();
        //DataBlock coverageData = info.getComponent(1).getData();
        
        DataBlock gridData = info.getComponent("coverage").getData();
        DataBlock coverageData = info.getComponent("imagery").getData();
        
        outputGridArray.setData(gridData);
        outputCoverageArray.setData(coverageData);
    }


    public void endDataAtom(DataComponent info, DataBlock data)
    {
        // TODO Auto-generated method stub
        
    }

//int dbc = 0;
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
    
	public static void main(String args[])
	{
        try
        {
            // load file and set up process reader
            String processFileUrl = WCS_Image_Process.class.getResource("GOES_Process.xml").toString();
            DOMReader dom = new DOMReader(processFileUrl+"#GOES_WCS_PROCESS", false);
            ProcessReader processReader = new ProcessReader(dom);
            processReader.setReadMetadata(false);
            processReader.setCreateExecutableProcess(true);
            
            // load process map and parse process chain
            String processMapUrl = WCS_Image_Process.class.getResource("ProcessMap.xml").toString();
            ProcessLoader.reloadMaps(processMapUrl);
            DataProcess process = processReader.readProcess(dom.getBaseElement());
            
            // intitialize process and print out info
            process.init();
            System.out.println(process);
            
            // set bbox input values
            DataBlock bboxData = new DataBlockDouble(4);
            bboxData.setDoubleValue(0, 33.2);
            bboxData.setDoubleValue(1, -89.8);
            bboxData.setDoubleValue(2, 33.8);
            bboxData.setDoubleValue(3, -89.1);
            process.getInputList().getComponent("bbox").setData(bboxData);
            
            //System.out.println("Image buffer will be: " + process.getOutputList().getComponent(0).getComponent(2).getData());
            System.out.println("Process Running...");
            process.execute();
            
            WCS_Image_Process proc = (WCS_Image_Process)process;
            DataGroup rangeData = (DataGroup)proc.output.getComponent("rangeData");
            DataValue imWidth = (DataValue)rangeData.getComponent("width");
            DataValue imHeight = (DataValue)rangeData.getComponent("length");
            DataArray outputImage = (DataArray)rangeData.getComponent("coverage");
//            DataArray outputImage = (DataArray)process.getOutputList().getComponent(0).getComponent("image");
            DataBlock data = outputImage.getData();
            
            // print blue component values
            for (int i=0; i<data.getAtomCount(); i++)
            {
                System.out.print(data.getByteValue(i) + ",");                  
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
    
}