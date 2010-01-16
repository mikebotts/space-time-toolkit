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

package org.vast.stt.provider.ve;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataBlockFactory;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.TiledMapProvider;
import org.vast.util.SpatialExtent;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.PNGDecodeParam;


/**
 * <p><b>Title:</b><br/>
 * Windows Live Local Virtual Earth Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Requests Data from Microsoft Live Local servers
 * Ref: http://local.live.com/
 * Requests: http://r1.ortho.tiles.virtualearth.net/tiles/r0231.png?g=25
 *           http://a1.ortho.tiles.virtualearth.net/tiles/a2301.jpeg?g=25
 *           http://h0.ortho.tiles.virtualearth.net/tiles/h1220330.jpeg?g=25
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 14, 2005
 * @version 1.0
 */
public class VirtualEarthProvider extends TiledMapProvider
{
    protected String layerId = "roads";
    
    
    public VirtualEarthProvider()
    {
        super(256, 256, 19);
                
        // set quad tree root extent (= max request)
        maxBbox = new SpatialExtent();
        maxBbox.setMinX(-Math.PI);
        maxBbox.setMaxX(+Math.PI);
        maxBbox.setMinY(-Math.PI);
        maxBbox.setMaxY(+Math.PI);
        quadTree.init(maxBbox);
        
        // set max proj extent for splitting bbox correctly
        SpatialExtent maxExtent = new SpatialExtent();
        maxExtent.setMinX(-Math.PI);
        maxExtent.setMaxX(+Math.PI);
        maxExtent.setMinY(-Math.PI);
        maxExtent.setMaxY(+Math.PI);
        tileSelector.setMaxExtent(maxExtent);
        
        initThreadPool(4);
    }
    
    
    @Override
    protected SpatialExtent transformBbox(SpatialExtent extent)
    {
        SpatialExtent mercatorExtent = new SpatialExtent();
        double minX = spatialExtent.getMinX() * DTR;
        double maxX = spatialExtent.getMaxX() * DTR;
        double minY = latToY(spatialExtent.getMinY() * DTR);
        double maxY = latToY(spatialExtent.getMaxY() * DTR);
        mercatorExtent.setMinX(minX);
        mercatorExtent.setMaxX(maxX);
        mercatorExtent.setMinY(minY);
        mercatorExtent.setMaxY(maxY);
        return mercatorExtent;
    }
    
    
    @Override
    protected void getNewTile(QuadTreeItem item)
    {
        try
        {
            // create quad id
            VirtualEarthTileNumber tileNumberGen = new VirtualEarthTileNumber();
            item.accept(tileNumberGen);
            String q = tileNumberGen.getTileNumber();
            if (q.length() == 0)
                return;
            
            // build request URL r=roads(png), a=aerial(jpeg), h=hybrid(jpeg)
            char layerChar = layerId.charAt(0);
            String urlString = "http://" + layerChar + q.charAt(q.length()-1) + ".ortho.tiles.virtualearth.net/tiles/" + layerChar + q;
            if (layerChar == 'r')
                urlString += ".png?g=25";
            else
                urlString += ".jpeg?g=25";
            
            //System.out.println(urlString);
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Referer", "http://local.live.com");
            connection.addRequestProperty("Connection", "keep-alive");
            connection.addRequestProperty("Keep-Alive", "300");
            //long startTime = System.currentTimeMillis();
            InputStream is = connection.getInputStream();
            //long connectTime = System.currentTimeMillis();
            MemoryCacheSeekableStream imgStream = new MemoryCacheSeekableStream(is);

            ParameterBlock pb = new ParameterBlock();
            pb.add(imgStream);
            
            if (connection.getContentType().equals("image/png"))
            {
                PNGDecodeParam pngParams = new PNGDecodeParam();
                pngParams.setExpandPalette(true);
                pb.add(pngParams);
            }
            
            RenderedOp rop = JAI.create("stream", pb);
            RenderedImage img = rop.createInstance();
            imgStream.close();
            
            // if DataBufferByte, just wrap image data with a DataBlock
            DataBuffer buf = img.getData().getDataBuffer();
            AbstractDataBlock imageBlock = null;
            if (buf instanceof DataBufferByte)
            {
                byte[] data = ((DataBufferByte)buf).getData();
                imageBlock = DataBlockFactory.createBlock(data);
            }
            else
                throw new IllegalStateException("DataBuffer Type not supported");
            
            // build grid
            int gridWidth = 10;
            int gridLength = 10;
            double minY = item.getMinY();
            double maxY = item.getMaxY();
            double minX = item.getMinX();
            double maxX = item.getMaxX();
            double dX = (maxX - minX) / (gridWidth-1);
            double dY = (maxY - minY) / (gridLength-1);
            AbstractDataBlock gridBlock = DataBlockFactory.createBlock(new double[gridLength*gridWidth*2]);
            
            // compute data for grid block
            int valCount = 0;
            for (int v=0; v<gridLength; v++)
            {
                for (int u=0; u<gridWidth; u++)
                {
                    double x = minX + dX * u;
                    double y = maxY - dY * v;
                    
                    // write lat and lon value
                    gridBlock.setDoubleValue(valCount, yToLat(y));
                    gridBlock.setDoubleValue(valCount+1, x);
                    valCount += 2;
                }
            }
            
            // add blocks to quad tree cache
            BlockListItem[] blockArray = new BlockListItem[2];
            blockArray[0] = new BlockListItem(imageBlock, null, null);
            blockArray[1] = new BlockListItem(gridBlock, null, null);
            item.setData(blockArray);
        }
        catch (Exception e)
        {
            if (!canceled)
                e.printStackTrace();
        }
    }
        
    
    public void setLayer(String layerId)
    {
        this.layerId = layerId;
    }
}