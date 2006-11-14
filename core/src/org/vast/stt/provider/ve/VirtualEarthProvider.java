/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.provider.ve;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import org.ogc.cdm.common.DataBlock;
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
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 14, 2005
 * @version 1.0
 */
public class VirtualEarthProvider extends TiledMapProvider
{
    protected String layerId = "roads";
    
    
    public VirtualEarthProvider()
    {
        super(256, 18);
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
                
                // create quad id
                VirtualEarthTileNumber tileNumberGen = new VirtualEarthTileNumber();
                item.accept(tileNumberGen);
                String q = tileNumberGen.getTileNumber();

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
                PNGDecodeParam pngParams = new PNGDecodeParam();
                pngParams.setExpandPalette(true);
                pb.add(pngParams);
                
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
                else if (buf instanceof DataBufferInt)
                {
                    int[] data = ((DataBufferInt)buf).getData();
                    byte[] byteData = new byte[data.length*3];
                    
                    for (int i=0; i<data.length; i++)
                    {
                        int b = i*3;
                        byteData[b] = (byte)(data[i] & 0xFF);
                        byteData[b+1] = (byte)((data[i] >> 8) & 0xFF);
                        byteData[b+2] = (byte)((data[i] >> 16) & 0xFF);
                    }
                    
                    imageryDataBlock = DataBlockFactory.createBlock(byteData);
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
                    
                    // remove sub items now that we have the new tile
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
    }
}