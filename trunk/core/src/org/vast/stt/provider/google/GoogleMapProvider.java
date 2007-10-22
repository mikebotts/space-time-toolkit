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

import org.vast.cdm.common.DataBlock;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataBlockFactory;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.TiledMapProvider;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.PNGDecodeParam;


/**
 * <p><b>Title:</b><br/>
 * Google Map Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Requests Data from Google Map Keyhole servers
 * Ref: http://kh{x}.google.com/
 * Requests: http://kh0.google.com/kh?n=404&v=10&t=tsrrtqqttrtqrsq
 *           http://mt0.google.com/mt?n=404&v=w2.25&x=0&y=0&zoom=16
 *           http://mt0.google.com/mt?n=404&v=w2t.26&x=0&y=0&zoom=16
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
        
    
    public GoogleMapProvider()
    {
        super(256, 256, 19);
    }
    
    
    class GetTileRunnable implements Runnable
    {
        protected QuadTreeItem item;
        
        
        public GetTileRunnable(QuadTreeItem item)
        {
            this.item = item;
        }
        
        
        public void run()
        {
            try
            {
                // create treeObject
                BlockListItem[] blockArray = new BlockListItem[2];
                String urlString = null;
                
                if (layerId.startsWith("satellite"))
                {
                    // build request URL for satellite data
                    GoogleMapTileNumber tileNumberGen = new GoogleMapTileNumber();
                    item.accept(tileNumberGen);
                    String q = tileNumberGen.getTileNumber();
                    urlString = "http://kh" + serverNum + ".google.com/kh?n=404&v=11&t=t" + q;
                }
                else if (layerId.startsWith("roads"))
                {
                    // build request URL for road/boundary data
                    GoogleMapTileXYZ tileXYZGen = new GoogleMapTileXYZ();
                    item.accept(tileXYZGen);
                    int x = tileXYZGen.getX();
                    int y = tileXYZGen.getY();
                    int z = tileXYZGen.getZoom();
                    urlString = "http://mt" + serverNum + ".google.com/mt?n=404&v=w2t.26&x=" + x + "&y=" + y + "&zoom=" + z;
                }
                else if (layerId.startsWith("map"))
                {
                    // build request URL for map data
                    GoogleMapTileXYZ tileXYZGen = new GoogleMapTileXYZ();
                    item.accept(tileXYZGen);
                    int x = tileXYZGen.getX();
                    int y = tileXYZGen.getY();
                    int z = tileXYZGen.getZoom();
                    urlString = "http://mt" + serverNum + ".google.com/mt?n=404&v=w2.25&x=" + x + "&y=" + y + "&zoom=" + z;
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
                DataBlock imageryDataBlock = null;
                if (buf instanceof DataBufferByte)
                {
                    byte[] data = ((DataBufferByte)buf).getData();
                    imageryDataBlock = DataBlockFactory.createBlock(data);
                }
//                else if (buf instanceof DataBufferInt)
//                {
//                    int[] data = ((DataBufferInt)buf).getData();
//                    byte[] byteData = new byte[data.length*3];
//                    
//                    for (int i=0; i<data.length; i++)
//                    {
//                        int b = i*3;
//                        byteData[b] = (byte)(data[i] & 0xFF);
//                        byteData[b+1] = (byte)((data[i] >> 8) & 0xFF);
//                        byteData[b+2] = (byte)((data[i] >> 16) & 0xFF);
//                    }
//                    
//                    imageryDataBlock = DataBlockFactory.createBlock(byteData);
//                }
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
                DataBlock gridBlock = DataBlockFactory.createBlock(new double[gridLength*gridWidth*2]);
                
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
                
                // add blocks to dataNode
                if (!canceled)
                {
                    // add blocks to data node
                    item.setData(blockArray);
                    blockArray[0] = blockLists[0].addBlock((AbstractDataBlock)imageryDataBlock);
                    blockArray[1] = blockLists[1].addBlock((AbstractDataBlock)gridBlock);
                    
                    // remove sub and super items now that we have the new tile
                    removeChildrenData(item);
                    removeHiddenParent(item);
                    
                    // send event for redraw
                    dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
                }
            }
            catch (Exception e)
            {
                if (!canceled)
                    e.printStackTrace();
            }           
        }        
    }
    
    
    @Override
    protected void getNewTile(QuadTreeItem item)
    {
        GetTileRunnable getTile = new GetTileRunnable(item);
        //Thread newThread = new Thread(getTile, nextItem.toString());
        //newThread.start();
        getTile.run();
    }
    
    
    public void setLayer(String layerId)
    {
        this.layerId = layerId;
        if (layerId.equals("roads"))
            useAlpha = true;
    }
}