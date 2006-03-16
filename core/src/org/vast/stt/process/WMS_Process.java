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

import org.ogc.cdm.reader.DataStreamParser;
import org.ogc.process.ProcessException;
import org.vast.data.*;
import org.vast.ows.OWSExceptionReader;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.ows.wms.WMSQuery;
import org.vast.ows.wms.WMSRequestWriter;
import org.vast.process.*;
import org.vast.stt.data.DataException;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;


/**
 * <p><b>Title:</b><br/>
 * WMS_Process
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Issues a request to a WMS server using provided parameters
 * and input bounding box and time, and outputs the resulting
 * image encapsulated in a data component structure.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jan 20, 2006
 * @version 1.0
 */
public class WMS_Process extends DataProcess
{
    protected DataValue bboxLat1, bboxLon1, bboxLat2, bboxLon2;
    protected DataValue outputWidth, outputHeight;
    protected DataArray outputImage;
    protected InputStream dataStream;
    protected WMSLayerCapabilities layerCaps;
    protected WMSQuery query;
    protected WMSRequestWriter requestBuilder;
    protected DataStreamParser dataParser;
    

    public WMS_Process()
    {
        query = new WMSQuery();
    }


    /**
     * Initializes the process
     * Gets handles to input/output components
     */
    public void init() throws ProcessException
    {
        try
        {
            // I/O mappings
            outputImage = (DataArray)outputData.getComponent("outputImage").getComponent("image");
            outputWidth = (DataValue)outputData.getComponent("outputImage").getComponent("width");
            outputHeight = (DataValue)outputData.getComponent("outputImage").getComponent("height");
            
            // Read parameter values (must be fixed!)
            DataGroup wmsParams = (DataGroup)paramData.getComponent(0);
            
            // service end point url
            String url = wmsParams.getComponent("endPoint").getData().getStringValue();
            String requestMethod = wmsParams.getComponent("requestMethod").getData().getStringValue();
            if (requestMethod.equalsIgnoreCase("post"))
                query.setPostServer(url);
            else
                query.setGetServer(url);
            
            // version
            String version = wmsParams.getComponent("version").getData().getStringValue();
            query.setVersion(version);
            
            // layer ID
            String layerID = wmsParams.getComponent("layer").getData().getStringValue();
            query.getLayers().add(layerID);
            
            // image format
            String format = wmsParams.getComponent("format").getData().getStringValue();
            query.setFormat(format);
            
            // image width
            int width = wmsParams.getComponent("imageWidth").getData().getIntValue();
            query.setWidth(width);
            
            // image height
            int height = wmsParams.getComponent("imageHeight").getData().getIntValue();
            query.setHeight(height);
            
            // image transparency
            boolean transparent = wmsParams.getComponent("imageTransparency").getData().getBooleanValue();
            query.setTransparent(transparent);
        }
        catch (ClassCastException e)
        {
            throw new ProcessException("Invalid I/O data", e);
        }
    }


    /**
     * Executes process algorithm on inputs and set output data
     */
    public void execute() throws ProcessException
    {
        URL url = null;

        try
        {
            initRequest();

            String urlString = requestBuilder.buildGetRequest(query);
            url = new URL(urlString);
            URLConnection urlCon = url.openConnection();

            //  Check mime for exception/xml type
            if (urlCon == null)
            {
                throw new DataException("Connection to " + url + " could be initialized");
            }

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
                dataStream = new MemoryCacheSeekableStream(url.openStream());

                // Create the ParameterBlock and add the SeekableStream to it.
                ParameterBlock pb = new ParameterBlock();
                pb.add(dataStream);

                // decode image using JAI
                RenderedOp rop = JAI.create("stream", pb);
                RenderedImage renderedImage;
                if (rop != null)
                {
                    renderedImage = rop.createInstance();
                    
                    //renderedImage.getClass();             

                    // copy image to DataNode
                    byte[] data = ((DataBufferByte)renderedImage.getData().getDataBuffer()).getData();
                    
                    /*
                    DataGroup pixelData = new DataGroup(renderedImage.getData().getNumBands());
                    pixelData.addComponent("blue", new DataValue(DataType.BYTE));
                    pixelData.addComponent("green", new DataValue(DataType.BYTE));
                    pixelData.addComponent("red", new DataValue(DataType.BYTE));                    
                    DataArray rowData = new DataArray(renderedImage.getWidth());
                    rowData.addComponent(pixelData);                    
                    DataArray imageData = new DataArray(renderedImage.getHeight());
                    imageData.addComponent(rowData);                    
                    cachedData.addComponent("image", imageData);
                    System.out.println(imageData);
                    */
                    
                    AbstractDataBlock dataBlock = DataBlockFactory.createBlock(data);
                    outputImage.setData(dataBlock);
                    outputWidth.getData().setIntValue(renderedImage.getWidth());
                    outputHeight.getData().setIntValue(renderedImage.getHeight());
                }
            }
        }
        catch (Exception e)
        {
            throw new ProcessException("Error while requesting data from WMS server:\n" + url, e);
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
    }
    
    
    protected void endRequest()
    {
        try
        {
            if (dataStream != null)
                dataStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}