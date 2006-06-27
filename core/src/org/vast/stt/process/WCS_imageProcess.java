package org.vast.stt.process;

import java.net.URL;

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataArray;
import org.vast.data.DataBlockMixed;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.ows.wcs.CoverageReader;
import org.vast.process.ProcessException;

/**
 * <p><b>Title:</b><br/>
 * WCS_imageProcess 
 * </p>
 * 
 * <p><b>Description:</b><br/>
 * Adds image data to the base WCS_Process for data that is 
 * encoded with a geographic tiepoint grid and separate 
 * data section with differing resolutions.  Using this for 
 * GOES, and maybe Doppler eventually 
 * </p>
 ** 
 * @author Tony Cook
 * @date Jun 16, 2006
 * @version 1.0
 */
public class WCS_imageProcess extends WCS_Process
{
	protected DataValue imageWidth, imageHeight; //  adding for GOES support
	protected DataArray imageData;  
	protected DataGroup coverageGroup;
	
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
            
            // Output mappings 
            output = (DataGroup)outputData.getComponent("coverageData");
            coverageGroup = (DataGroup)output.getComponent("gridData");
            outputCoverage = (DataArray)coverageGroup.getComponent("coverage");
            outputWidth = (DataValue)coverageGroup.getComponent("width");  
            outputLength = (DataValue)coverageGroup.getComponent("length");
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
        
        System.out.println(this);
    }

    
    /**
     * Ovveride base implementation to add image data
     */
    public void execute() throws ProcessException
    {
        URL url = null;

        try
        {
            initRequest();
            CoverageReader reader = new CoverageReader();
            dataStream = requestBuilder.sendRequest(query, false);
            reader.parse(dataStream);
          
            dataParser = reader.getDataParser();
            DataComponent dataInfo = dataParser.getDataComponents();
            dataParser.setDataHandler(this);            
            dataParser.parse(reader.getDataStream());
            
            int width = dataInfo.getComponent(0).getComponentCount();
            int length = dataInfo.getComponentCount();                
            outputWidth.getData().setIntValue(width);
            outputLength.getData().setIntValue(length);
            
            //  get image data...
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            throw new ProcessException("Error while requesting data from WCS server:\n" + url, e);
        }
        finally
        {
            endRequest();
        }
    }
	
    public void endData(DataComponent info, DataBlock data)
    {
        //outputCoverage.setData(data);
        AbstractDataBlock[] barr = ((DataBlockMixed)data).getUnderlyingObject();
        outputCoverage.setData( barr[0]);
        output.combineDataBlocks();
    }

}