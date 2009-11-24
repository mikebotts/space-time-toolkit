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

package org.vast.stt.provider.google;

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
 * Google Map Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Requests Data from Google Map Keyhole servers
 * 
 * Imagery Request: http://khm{x}.google.com/kh/v=48&x=133&y=93&z=8&s= G Ga Gal Gali Galil Galile Galileo
 * Roads Request: http://mt{x}.google.com/vt/lyrs=h@112&hl=en&x=1&y=0&z=1&s= G Ga Gal Gali Galil Galile Galileo
 * Map Request: http://mt{x}.google.com/vt/lyrs=m@112&hl=en&x=256&y=192&z=9&s= G Ga Gal Gali Galil Galile Galileo
 *
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 14, 2005
 * @version 1.0
 */
public class GoogleMapProvider extends TiledMapProvider
{
    protected String layerId = "roads";
    protected int serverNum = 0;
    protected int seqNumber = 0;
    protected String seqString = "Galileo";
    
    
    public GoogleMapProvider()
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
        
        initThreadPool(1);
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
            // create treeObject
            String urlString = null;
            
            // update sequence string
            String s = seqNumber == 0 ? "" : seqString.substring(0, seqNumber);
            seqNumber = seqNumber >= seqString.length() ? 0 : seqNumber+1;
            
            if (layerId.startsWith("satellite"))
            {
                // build request URL for satellite data
                GoogleMapTileXYZ tileXYZGen = new GoogleMapTileXYZ();
                item.accept(tileXYZGen);
                int x = tileXYZGen.getX();
                int y = tileXYZGen.getY();
                int z = tileXYZGen.getZ();                    
                urlString = "http://khm" + serverNum + ".google.com/kh/v=48&x=" + x + "&y=" + y + "&z=" + z + "&s=" + s;
            }
            else if (layerId.startsWith("roads"))
            {
                // build request URL for road/boundary data
                GoogleMapTileXYZ tileXYZGen = new GoogleMapTileXYZ();
                item.accept(tileXYZGen);
                int x = tileXYZGen.getX();
                int y = tileXYZGen.getY();
                int z = tileXYZGen.getZ();
                urlString = "http://mt" + serverNum + ".google.com/vt/lyrs=h@112&hl=en&x=" + x + "&y=" + y + "&z=" + z + "&s=" + s;
            }
            else if (layerId.startsWith("map"))
            {
                // build request URL for map data
                GoogleMapTileXYZ tileXYZGen = new GoogleMapTileXYZ();
                item.accept(tileXYZGen);
                int x = tileXYZGen.getX();
                int y = tileXYZGen.getY();
                int z = tileXYZGen.getZ();
                urlString = "http://mt" + serverNum + ".google.com/vt/lyrs=m@112&hl=en&x=" + x + "&y=" + y + "&z=" + z + "&s=" + s;
            }
            
            // increment server number
            serverNum++;
            if (serverNum > 3)
                serverNum = 0;                
                                    
            //System.out.println(urlString);
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Referer", "http://maps.google.com");
            connection.addRequestProperty("Connection", "keep-alive");
            connection.addRequestProperty("Keep-Alive", "300");
            
            // return if connection cannot be made
            InputStream is = null;
            try {is = connection.getInputStream();}
            catch (Exception e) {return;}
            
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
            
            // add blocks to quad tree cache and to block lists
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
        if (layerId.equals("roads"))
            useAlpha = true;
    }
}