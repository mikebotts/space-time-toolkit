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

package org.vast.stt.renderer;

import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import org.vast.stt.style.RasterTileGraphic.BufferType;
import org.vast.util.MessageSystem;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.PNGDecodeParam;


/**
 * <p><b>Title:</b>
 * Icon Manager
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Handles preloading of icon image files using JAI
 * TODO: implement cleanup of unused icons
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Dec 5, 2006
 * @version 1.0
 */
public class IconManager
{
    public class Icon
    {
        public Buffer data;
        public BufferType imgType;
        public int width;
        public int height;
    }
    
    protected Hashtable<String, Icon> iconTable;
    
    
    public IconManager()
    {
        iconTable = new Hashtable<String, Icon>();
    }
    
    
    public Icon getIcon(String url)
    {
        Icon icon = iconTable.get(url);
        if (icon == null)
            icon = loadIcon(url);
        
        if (icon.data != null)
            return icon;
        else
            return null;
    }
    
    
    protected Icon loadIcon(String urlText)
    {
        Icon icon = new Icon();
        
        MemoryCacheSeekableStream dataStream;
        try
        {
            URL url = new URL(urlText);
            dataStream = new MemoryCacheSeekableStream(url.openStream());
        }
        catch (Exception e)
        {
            MessageSystem.display("Icon " + urlText + " not found", true);
            iconTable.put(urlText, icon);
            return icon;
        }

        // Create the ParameterBlock and add the SeekableStream to it.
        ParameterBlock pb = new ParameterBlock();
        pb.add(dataStream);
        
        // add PNG params
        if (urlText.endsWith(".png"))
        {
            PNGDecodeParam pngParams = new PNGDecodeParam();
            pngParams.setExpandPalette(true);
            pb.add(pngParams);
        }
        
        // decode image using JAI
        RenderedOp rop = JAI.create("stream", pb);
        
        if (rop != null)
        {
            RenderedImage renderedImage = rop.createInstance();

            // put data buffer in output datablock
            byte[] data = ((DataBufferByte)renderedImage.getData().getDataBuffer()).getData();
            icon.data = ByteBuffer.wrap(data);
            icon.width = renderedImage.getWidth();
            icon.height = renderedImage.getHeight();            
        }
        
        iconTable.put(urlText, icon);
        return icon;
    }
}
