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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

import javax.media.Buffer;
import javax.media.MediaLocator;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataType;
import org.vast.data.DataArray;
import org.vast.data.DataBlockByte;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.ows.OWSExceptionReader;
import org.vast.ows.wms.WMSQuery;
import org.vast.ows.wms.WMSRequestWriter;
import org.vast.process.DataProcess;
import org.vast.process.ProcessException;
import org.vast.video.JMFMain;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
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
            Thread.sleep(500);
            Buffer bb = jmfm.getFrame(0);
            byte[] data = ((DataBufferByte)bb.getData()).getData();
            //byte[] data = ((DataBufferByte)renderedImage.getData().getDataBuffer()).getData();
            ((DataBlockByte)outputImage.getData()).setUnderlyingObject(data);
            
            outputWidth.getData().setIntValue(160);
            outputHeight.getData().setIntValue(120);
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
    public static InputStream newInputStream(final ByteBuffer buf) {
        return new InputStream() {
            public synchronized int read() throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                return buf.get();
            }
    
            public synchronized int read(byte[] bytes, int off, int len) throws IOException {
                // Read only what's left
                len = Math.min(len, buf.remaining());
                buf.get(bytes, off, len);
                return len;
            }
        };
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