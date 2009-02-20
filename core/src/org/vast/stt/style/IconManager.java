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

package org.vast.stt.style;

import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import org.vast.ows.sld.functions.StringIdProvider;
import org.vast.stt.style.RasterTileGraphic.BufferType;
import org.vast.util.MessageSystem;

import com.sun.media.jai.codec.JPEGDecodeParam;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.PNGDecodeParam;
import com.sun.media.jai.codec.TIFFDecodeParam;


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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Dec 5, 2006
 * @version 1.0
 */
public class IconManager implements StringIdProvider
{
    protected static IconManager managerInstance;
    protected ArrayList<Icon> iconList;
    protected Stack<Integer> emptyIndexList;
    protected Hashtable<String, Integer> urlTable;
    protected String urlBase = "";
    
    
    public class Icon
    {
        public String url;
        public BufferType pixelFormat;
        public Buffer data;
        public int width;
        public int height;
    }
    
    
    protected IconManager()
    {
        iconList = new ArrayList<Icon>();
        emptyIndexList = new Stack<Integer>();
        urlTable = new Hashtable<String, Integer>();
    }
    
    
    public static IconManager getInstance()
    {
        if (managerInstance == null)
            managerInstance = new IconManager();
        
        return managerInstance;
    }
    
    
    public Icon getIcon(int iconId)
    {
        if (iconId < 0)
            return null;
        
        Icon icon = iconList.get(iconId);
        
        // load image data the first time its use
        if (icon.data == null)
            loadIconData(icon);
        
        // if data wasn't read correctly
        if (icon.data != null)
            return icon;
        else
            return null;
    }
    
    
    public int addIcon(String urlText)
    {
        // if icon url is already registered
        if (urlTable.containsKey(urlText))
            return urlTable.get(urlText);
                
        // otherwise add a new icon
        Icon icon = new Icon();
        icon.url = urlText;
        int iconIndex;
        
        if (emptyIndexList.isEmpty())
        {
            iconList.add(icon);
            iconIndex = iconList.size() - 1;                       
        }
        else
        {
            iconIndex = emptyIndexList.pop();
            iconList.set(iconIndex, icon);
        }
        
        urlTable.put(urlText, iconIndex);
        return iconIndex;
    }
    
    
    public void loadIconData(Icon icon)
    {
        MemoryCacheSeekableStream dataStream;
        try
        {
            URL url = new URL(icon.url);
            dataStream = new MemoryCacheSeekableStream(url.openStream());
        }
        catch (Exception e)
        {
            MessageSystem.display("Icon " + icon.url + " not found", true);
            return;
        }

        // Create the ParameterBlock and add the SeekableStream to it.
        ParameterBlock pb = new ParameterBlock();
        pb.add(dataStream);
        
        // add PNG params
        if (icon.url.endsWith(".png"))
        {
            PNGDecodeParam pngParams = new PNGDecodeParam();
            pngParams.setExpandPalette(true);
            pb.add(pngParams);
        }
        
        // add PNG params
        if (icon.url.endsWith(".tif") || icon.url.endsWith(".tiff"))
        {
            TIFFDecodeParam tiffParams = new TIFFDecodeParam();
            tiffParams.setDecodePaletteAsShorts(true);
            pb.add(tiffParams);
        }
        
        // add JPEG params
        if (icon.url.endsWith(".jpg") || icon.url.endsWith(".jpeg"))
        {
            JPEGDecodeParam jpegParams = new JPEGDecodeParam();
            pb.add(jpegParams);
        }
        
        // decode image using JAI
        RenderedOp rop = JAI.create("stream", pb);
        
        if (rop != null)
        {
            RenderedImage renderedImage = rop.createInstance();

            // put data buffer in byte array
            byte[] data = ((DataBufferByte)renderedImage.getData().getDataBuffer()).getData();

            icon.data = ByteBuffer.wrap(data);
            icon.width = renderedImage.getWidth();
            icon.height = renderedImage.getHeight();
            
            // set buffer data type
            switch (renderedImage.getColorModel().getNumComponents())
            {
                case 1:
                    icon.pixelFormat = BufferType.LUM;
                    break;
                    
                case 2:
                    icon.pixelFormat = BufferType.LUMA;
                    break;
                    
                case 3:
                    icon.pixelFormat = BufferType.RGB;
                    break;
                    
                case 4:
                    icon.pixelFormat = BufferType.RGBA;
                    break;
            }
        }
    }
    
    
    public void removeIcon(int iconId)
    {
        iconList.set(iconId, null);
        emptyIndexList.push(iconId);
    }


    public int getStringId(String text)
    {
        return addIcon(urlBase + text);
    }
    
    
    public void setPrefix(String prefix)
    {
        urlBase = prefix;
    }
}
