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

package org.vast.sttx.kml;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.vast.stt.style.GridPatchGraphic;
import org.vast.stt.style.RasterPixelGraphic;
import org.vast.stt.style.RasterTileGraphic;
import org.vast.stt.style.TextureStyler;
import org.vast.util.MessageSystem;


/**
 * <p><b>Title:</b><br/>
 * Texture Manager
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class COLLADATextureManager
{
    protected boolean npotSupported;
    protected boolean normalizationRequired;
    protected int maxSize = 512;
    protected int maxWastedPixels = 100;
    
    
    public COLLADATextureManager()
    {        
        npotSupported = true;
    }
    
    
    /**
     * Creates a new texture by transfering data from styler to image file
     * @param styler
     * @param tex
     * @param texFile
     * @param out
     */
    protected void writeTexture(TextureStyler styler, RasterTileGraphic tex, String texFile, OutputStream out)
    {
        // fetch texture data from styler
        fillTexData(styler, tex);
        
        // if texture was successfully constructed write file
        if (tex.hasRasterData)
        {
            try
            {
                // create JPG/PNG file using JAI
                //BufferedImage bufferedImage = new BufferedImage(tex.width, tex.height, BufferedImage.TYPE_INT_RGB);
                ByteBuffer buf = (ByteBuffer)tex.rasterData;
                DataBuffer db = new DataBufferByte(buf.array(), tex.width * tex.height);
                
                ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                int[] bits = new int[tex.bands];
                for (int b=0; b<tex.bands; b++)
                    bits[b] = 8;
                boolean alpha = (tex.bands == 4);
                ColorModel colorModel = new ComponentColorModel(cs, bits, alpha, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
                
                int[] bandOffsets = new int[] {0, 1, 2};
                WritableRaster raster = Raster.createInterleavedRaster(db, tex.width, tex.height, tex.width*tex.bands, tex.bands, bandOffsets, null); 
                BufferedImage image = new BufferedImage(colorModel, raster, false, null);
                
                if (tex.bands == 3)
                    ImageIO.write(image, "png", out);
                //else if (tex.bands == 4)
                //    ImageIO.write(image, "png", out);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    
     /**
     * Create a texture based on data passed by styler
     * @param styler
     * @param tex
     * @param texInfo
     */
    protected void fillTexData(TextureStyler styler, RasterTileGraphic tex)
    {
        int paddedWidth = tex.width;
        int paddedHeight = tex.height;
        int initialWidth = tex.width;
        int initialHeight = tex.height;
                
        // handle case of padding for npot
        if (!npotSupported)
        {
            // determine closest power of 2
            paddedWidth = closestHigherPowerOfTwo(initialWidth);
            paddedHeight = closestHigherPowerOfTwo(initialHeight);          
            
            // display warning message if padding is needed
            if (paddedWidth != initialWidth || paddedHeight != initialHeight)
            {
                MessageSystem.display("Texture will be padded to have a power of two size.\n" +
                                      "    initial size: " + initialWidth + " x " + initialHeight + "\n" +
                                      "     padded size: " + paddedWidth + " x " + paddedHeight, false);
            }
        }
        
        // create byte buffer of the right size
        ByteBuffer buffer = ByteBuffer.allocate(paddedWidth*paddedHeight*tex.bands);
        //System.err.println("Creating " + paddedWidth + " x " + paddedHeight + " Texture with " + tex.bands + " bands");
        int index = 0;
        
        for (int j=0; j<initialHeight; j++)
        {
            for (int i=0; i<initialWidth; i++)
            {
                RasterPixelGraphic pixel = styler.getPixel(i + tex.xPos, j + tex.yPos);
                buffer.put(index, (byte)pixel.r);
                index++;
                
                // only if RGB
                if (tex.bands > 2)
                {
                    buffer.put(index, (byte)pixel.g);
                    index++;
                    buffer.put(index, (byte)pixel.b);
                    index++;
                }
                
                // only if RGBA
                if (tex.bands == 2 || tex.bands == 4)
                {
                    buffer.put(index, (byte)pixel.a);
                    index++;
                }
                
                //System.out.println(i + "," + j + ": " + pixel.r + "," + pixel.g + "," + pixel.b);
            }
            
            // skip padding bytes
            index += (paddedWidth - initialWidth)*tex.bands;
        }
        
        tex.rasterData = buffer;
        tex.hasRasterData = true;
    }
    
    
    /**
     * Split a Grid in several tiles with dimensions
     * equal to full powers of two
     * @param tex
     * @return
     */
    protected List<GridPatchGraphic> splitGrid(GridPatchGraphic tex, List<RasterTileGraphic> rasterTiles)
    {
        return null;
    }
    
    
    /**
     * Split a Raster in several tiles with dimensions
     * equal to full powers of two
     * @param tex
     * @return
     */
    public List<RasterTileGraphic> splitTexture(RasterTileGraphic tex)
    {
        List<Integer> widthList =  getPower2SizeList(tex.width);
        List<Integer> heightList =  getPower2SizeList(tex.height);
        int xSegs = widthList.size();
        int ySegs = heightList.size();
        
        List<RasterTileGraphic> tileList = new ArrayList<RasterTileGraphic>(xSegs*ySegs);
        int dX = 0;
        int dY = 0;
        
        for (int i=0; i<xSegs; i++)
        {
            int width = widthList.get(i);
            int widthPadding = 0;
            if (i == xSegs-1)
                widthPadding = width - (tex.width - dX);
                
            dY = 0;
            for (int j=0; j<ySegs; j++)
            {
                RasterTileGraphic nextTile = new RasterTileGraphic();
                int height = heightList.get(j);
                int heightPadding = 0;
                if (j == ySegs-1)
                    heightPadding = height - (tex.height - dY);
                
                nextTile.width = width;
                nextTile.height = height;
                nextTile.xPos = dX;
                nextTile.yPos = dY;
                nextTile.widthPadding = widthPadding;
                nextTile.heightPadding = heightPadding;
                dY += height;
                
                tileList.add(nextTile);
            }

            dX += width;
        }
        
        for (int i=0; i<tileList.size(); i++)
        {
            RasterTileGraphic t = tileList.get(i);
            //GridPatchGraphic g = gridList.get(i);
            
            System.out.println("Tile: " + t.width + "x" + t.height + " @ " +
                                          t.xPos + "," + t.yPos + " pad " +
                                          t.widthPadding + "x" + t.heightPadding);
        }
        
        return tileList;
    }
    
    
    /**
     * Breaks done the argument into a list of power of two values
     * @param size
     * @return
     */
    protected List<Integer> getPower2SizeList(int size)
    {
        List<Integer> sizeList = new ArrayList<Integer>();
        int remainSize = size;
        boolean done = false;

        do
        {
            int nextSize = closestHigherPowerOfTwo(remainSize);
            int wastedPixels = nextSize - remainSize;
            
            if (nextSize <= maxSize && wastedPixels <= maxWastedPixels)
            {
                done = true;
            }
            else
            {
                nextSize = closestLowerPowerOfTwo(remainSize);
                remainSize = remainSize - nextSize;
            }

            sizeList.add(nextSize);
        }
        while(!done);

        return sizeList;
    }
    
    
    /**
     * Calculate closest power of two value lower than argument
     * @param val
     * @return
     */
    protected int closestLowerPowerOfTwo(int val)
    {
        int power = (int)Math.floor(log2(val));
        int pow2 = (int)Math.pow(2, power);
        
        if (pow2 > maxSize)
            return maxSize;
        else
            return pow2; 
    }


    /**
     * Calculate closest power of two value higher than argument
     * TODO closestHigherPowerOfTwo method description
     * @param val
     * @return
     */
    protected int closestHigherPowerOfTwo(int val)
    {
        int power = (int)Math.ceil(log2(val));
        return (int)Math.pow(2, power);
    }


    /**
     * Calculate log base 2 of the argument
     * @param val
     * @return
     */
    protected double log2(double val)
    {
        return Math.log(val)/Math.log(2);
    }


    public boolean isNpotSupported()
    {
        return npotSupported;
    }


    public boolean isNormalizationRequired()
    {
        return normalizationRequired;
    }
}