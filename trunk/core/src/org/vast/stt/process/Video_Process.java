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

import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;
import org.ogc.cdm.common.DataType;
import org.vast.data.*;
import org.vast.ows.OWSExceptionReader;
import org.vast.ows.wms.WMSQuery;
import org.vast.ows.wms.WMSRequestWriter;
import org.vast.process.*;
import org.vast.video.*;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;


public class Video_Process extends DataProcess
{
    protected DataValue ttime;
    protected DataValue outputWidth, outputHeight;
    protected DataArray outputImage;
    protected DataGroup output;
    protected InputStream dataStream;
    protected WMSQuery query;
    protected WMSRequestWriter requestBuilder;
    protected int originalWidth;
    protected int originalHeight;
    protected int count=0;
    protected boolean preserveAspectRatio = true;
    protected JMFMain jmfm;
    

    public Video_Process()
    {
        query = new WMSQuery();
        requestBuilder = new WMSRequestWriter();
    }


    /**
     * Initializes the process
     * Gets handles to input/output components
     */
    public void init() throws ProcessException
    {
        try
        {
            // Input mappings
            DataGroup input = (DataGroup)inputData.getComponent("time");
            ttime = (DataValue)input.getComponent("ttime");
            input.assignNewDataBlock();
            
            // Output mappings
            output = (DataGroup)outputData.getComponent("imageData");
            outputImage = (DataArray)output.getComponent("image");
            outputWidth = (DataValue)output.getComponent("width");
            outputHeight = (DataValue)output.getComponent("height");
            
            // image data type
            DataGroup pixelData = (DataGroup)outputImage.getComponent(0).getComponent(0);
            for (int i=0; i<pixelData.getComponentCount(); i++)
                ((DataValue)pixelData.getComponent(i)).setDataType(DataType.BYTE);
        }
        catch (Exception e)
        {
            throw new ProcessException("Invalid I/O structure", e);
        }
        
        try
        {
            // Read parameter values (must be fixed!)
            DataGroup videoParams = (DataGroup)paramData.getComponent("videoOptions");
            
            // service end point url
            String url = videoParams.getComponent("endPoint").getData().getStringValue();
            MediaLocator ml=new MediaLocator(url);
            jmfm = new JMFMain();
        	if(!jmfm.open(ml)){
        		System.err.println("Could not open location: "+ml.toString());
        		System.exit(0);
        	}
        	jmfm.init();
        	
            String requestMethod = videoParams.getComponent("requestMethod").getData().getStringValue();
            if (requestMethod.equalsIgnoreCase("post"))
                query.setPostServer(url);
            else
                query.setGetServer(url);
            
            // version
            String version = videoParams.getComponent("version").getData().getStringValue();
            query.setVersion(version);
            
            // layer ID
            String layerID = videoParams.getComponent("layer").getData().getStringValue();
            query.getLayers().add(layerID);
            
            // image format
            String format = videoParams.getComponent("format").getData().getStringValue();
            query.setFormat(format);
            
            // image width
            originalWidth = videoParams.getComponent("imageWidth").getData().getIntValue();
            query.setWidth(originalWidth);
            
            // image height
            originalHeight = videoParams.getComponent("imageHeight").getData().getIntValue();
            query.setHeight(originalHeight);
            
            // image transparency
            boolean transparent = videoParams.getComponent("imageTransparency").getData().getBooleanValue();
            query.setTransparent(transparent);
            
            query.setSrs("EPSG:4326");
            query.setExceptionType("application/vnd.ogc.se_xml");
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
    	count++;
        URL url = null;
        RenderedImage renderedImage = null;
        
        try
        {
            initRequest();

            byte[] fData = jmfm.getFrame(count);
            String urlString = requestBuilder.buildGetRequest(query);
            url = new URL(urlString);
            URLConnection urlCon = url.openConnection();

            //  Check on mimeType catches all three types (blank, inimage, xml)
            //  of OGC service exceptions
            String mimeType = urlCon.getContentType();
            if (mimeType.contains("xml") || mimeType.startsWith("application"))
            {
                OWSExceptionReader reader = new OWSExceptionReader();
                reader.parseException(urlCon.getInputStream());
            }
            else
            {
                // use JAI MemorySeekableStream for better performance
                dataStream = new MemoryCacheSeekableStream(url.openStream());

                // Create the ParameterBlock and add the SeekableStream to it.
                ParameterBlock pb = new ParameterBlock();
                pb.add(dataStream);

                // decode image using JAI
                RenderedOp rop = JAI.create("stream", pb);
                
                if (rop != null)
                {
                    renderedImage = rop.createInstance();

                    // put data buffer in output datablock
                    byte[] data = ((DataBufferByte)renderedImage.getData().getDataBuffer()).getData();
                    ((DataBlockByte)outputImage.getData()).setUnderlyingObject(fData);
                }
            }
            
            // adjust width and height of the output
            int width = 0;
            int height = 0;
            
            if (renderedImage != null)
            {
                width = renderedImage.getWidth();
                height = renderedImage.getHeight();
            }
            
            outputWidth.getData().setIntValue(width);
            outputHeight.getData().setIntValue(height);
            output.combineDataBlocks();
        }
        catch (Exception e)
        {
            throw new ProcessException("Error while requesting data from Video server:\n" + url, e);
        }
        finally
        {
            endRequest();
        }
    }
    
    
    protected void initRequest()
    {
        endRequest();
        outputWidth.renewDataBlock();
        outputHeight.renewDataBlock();
    }
    
    
    protected void endRequest()
    {
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


	public boolean getPreserveAspectRatio() {
		return preserveAspectRatio;
	}


	public void setPreserveAspectRatio(boolean preserveAspectRatio) {
		this.preserveAspectRatio = preserveAspectRatio;
	}
}