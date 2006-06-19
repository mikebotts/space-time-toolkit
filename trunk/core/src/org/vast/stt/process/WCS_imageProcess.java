package org.vast.stt.process;

import java.net.URL;

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataArray;
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
	
    public void init() throws ProcessException
    {
    	super.init();
    	// Added image output mappings
        try
        {
	        imageData = (DataArray)output.getComponent("imagery");
	        //  Could we alternately pull this out of the DataArrays?
	        //  Or are they even needed if encoded as attributes of the array size?
	        //imageWidth = (DataValue)output.getComponent("imageWidth");  
	        //imageHeight = (DataValue)output.getComponent("imageHeight");
        } catch (Exception e) {
            throw new ProcessException("Invalid I/O structure", e);
        }
            	
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
		super.endData(info, data);
	}
}