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

package org.vast.stt.data;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import org.ogc.cdm.common.DataBlock;
import org.ogc.cdm.common.DataType;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataArray;
import org.vast.data.DataBlockFactory;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.stt.data.tiling.QuadTree;
import org.vast.stt.data.tiling.QuadTreeItem;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.SpatialExtent;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.PNGDecodeParam;


/**
 * <p><b>Title:</b><br/>
 * Windows Live Local Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Requests Data from Microsoft Live Local servers
 * with it.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 14, 2005
 * @version 1.0
 */
public class MsRoadsProvider extends AbstractProvider
{
    private final static double PI_180 = Math.PI/180;
    protected QuadTree quadTree;
    protected BlockList[] blockLists = new BlockList[2]; // 0 for imagery, 1 for grid
    protected DataArray gridData;
    
    
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
                StringBuffer quadId = new StringBuffer();
                item.appendId2(quadId);
                String q = quadId.toString();

                // build request URL r=roads(png), a=aerial(jpeg), h=hybrid(jpeg)
                String urlString = "http://r" + q.charAt(q.length()-1) + ".ortho.tiles.virtualearth.net/tiles/r" + q + ".png?g=25";
                System.out.println(urlString);
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("Referer", "http://local.live.com");
                //connection.addRequestProperty("Connection", "Keep-Alive");
                //connection.addRequestProperty("Keep-Alive", "1");
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
                
                // wrap image data with a DataBlock
                byte[] data = ((DataBufferByte)img.getData().getDataBuffer()).getData();
                DataBlock imageryDataBlock = DataBlockFactory.createBlock(data);
                
                // build grid
                double minY = yToLat(item.getMinY());
                double maxY = yToLat(item.getMaxY());
                double minX = item.getMinX();
                double maxX = item.getMaxX();
                double dX = (maxX - minX) / 9;
                double dY = (maxY - minY) / 9;
                gridData.renewDataBlock();
                
                // compute data for grid block
                for (int u=0; u<10; u++)
                {
                    for (int v=0; v<10; v++)
                    {
                        double lon = minX + dX * u;
                        double lat = maxY - dY * v;
                        
                        // write lat and lon value
                        gridData.getComponent(v).getComponent(u).getComponent(0).getData().setDoubleValue(lat);
                        gridData.getComponent(v).getComponent(u).getComponent(1).getData().setDoubleValue(lon);
                    }
                }
                
                // add blocks to dataNode
                if (!canceled)
                {
                    // add blocks to data node
                    item.setData(blockArray);
                    blockArray[0] = blockLists[0].addBlock((AbstractDataBlock)imageryDataBlock);
                    blockArray[1] = blockLists[1].addBlock((AbstractDataBlock)gridData.getData());
                    
                    // remove sub items now that we have the new tile
                    removeChildrenData(item);
                    
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
    
    
    // http://local.live.com/
    // http://r1.ortho.tiles.virtualearth.net/tiles/r1.png?g=15
    public MsRoadsProvider()
	{
        quadTree = new QuadTree();
        SpatialExtent initBbox = new SpatialExtent();
        initBbox.setMinX(-Math.PI);
        initBbox.setMaxX(+Math.PI);
        initBbox.setMinY(-Math.PI);
        initBbox.setMaxY(+Math.PI);
        quadTree.init(initBbox);
        this.autoUpdate = true;
	}


    @Override
    public void init() throws DataException
    {
        // create block list for textures
        DataGroup pixelData = new DataGroup(3);
        pixelData.setName("pixel");
        pixelData.addComponent("blue", new DataValue(DataType.BYTE));
        pixelData.addComponent("green", new DataValue(DataType.BYTE));
        pixelData.addComponent("red", new DataValue(DataType.BYTE));                    
        DataArray rowData = new DataArray(256);
        rowData.addComponent(pixelData);
        rowData.setName("row");                  
        DataArray imageData = new DataArray(256);
        imageData.addComponent(rowData);
        imageData.setName("image");
        blockLists[0] = dataNode.createList(imageData);
        System.out.println(imageData);
        
        // create block list for grid
        DataGroup pointData = new DataGroup(2);
        pointData.setName("point");
        pointData.addComponent("lat", new DataValue(DataType.FLOAT));
        pointData.addComponent("lon", new DataValue(DataType.FLOAT));                   
        rowData = new DataArray(10);
        rowData.addComponent(pointData);
        rowData.setName("row");                  
        gridData = new DataArray(10);
        gridData.addComponent(rowData);
        gridData.setName("grid");
        blockLists[1] = dataNode.createList(gridData);
        System.out.println(gridData);
        
        dataNode.setNodeStructureReady(true);
    }
    
    
    @Override
    public void updateData() throws DataException
    {
        // init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        // convert extent to mercator projection
        SpatialExtent mercatorExtent = new SpatialExtent();
        double minX = spatialExtent.getMinX() * PI_180;
        double maxX = spatialExtent.getMaxX() * PI_180;
        double minY = latToY(spatialExtent.getMinY() * PI_180);
        double maxY = latToY(spatialExtent.getMaxY() * PI_180);
        mercatorExtent.setMinX(Math.max(minX, -Math.PI));
        mercatorExtent.setMaxX(Math.min(maxX, +Math.PI));
        mercatorExtent.setMinY(Math.max(minY, -Math.PI));
        mercatorExtent.setMaxY(Math.min(maxY, +Math.PI));
        
        // query tree for matching and unused items
        ArrayList<QuadTreeItem> matchingItems = new ArrayList<QuadTreeItem>(30);
        ArrayList<QuadTreeItem> unusedItems = new ArrayList<QuadTreeItem>(30);
        quadTree.findItems(matchingItems, unusedItems, mercatorExtent, 20, 5);
        
        // clean up old items
        int unusedItemCount = unusedItems.size();
        ArrayList<BlockListItem> deletedItems = new ArrayList<BlockListItem>(unusedItemCount*2);
        for (int i=0; i<unusedItemCount; i++)
        {
            QuadTreeItem nextItem = unusedItems.get(i);
            BlockListItem[] blockArray = (BlockListItem[])nextItem.getData();
            nextItem.setData(null);
            
            if (blockArray != null)
            {
                for (int b=0; b<blockArray.length; b++)
                {
                    if (blockArray[b] != null)
                    {
                        // remove BlockListItem from list
                        blockLists[b].remove(blockArray[b]);
                        
                        // added to deletedItems list
                        deletedItems.add(blockArray[b]);
                    }
                }
            }
            
            // also remove tree item itself from parent
            nextItem.getParent().setChild(nextItem.getQuadrant(), null);
        }
        
        // send event to cleanup stylers
        if (deletedItems.size() > 0)
        {
            dispatchEvent(new STTEvent(deletedItems.toArray(), EventType.PROVIDER_DATA_REMOVED));
            //System.out.println(deletedItems.size() + " items deleted");
        }
        
        // get items to display
        for (int i=0; i<matchingItems.size(); i++)
        {
            QuadTreeItem nextItem = matchingItems.get(i);
            
            if (canceled)
                return;
            
            if (nextItem.getData() == null)
            {
                GetTileRunnable getTile = new GetTileRunnable(nextItem);
                //Thread newThread = new Thread(getTile, nextItem.toString());
                //newThread.start();
                getTile.run();
                continue;
            }
            else
            {
                BlockListItem[] blockArray = (BlockListItem[])nextItem.getData();
                for (int b=0; b<blockArray.length; b++)
                {
                    blockLists[b].remove(blockArray[b]);
                    blockLists[b].add(blockArray[b]);
                }
                removeChildrenData(nextItem);
            }
        }
    }
    
    
    /**
     * Recursively remove children data from blockLists
     * @param item
     * @param list
     */
    protected void removeChildrenData(QuadTreeItem item)
    {
        // loop through children
        for (byte i=0; i<4; i++)
        {
            QuadTreeItem nextItem = item.getChild(i);
            if (nextItem != null)
            {
                BlockListItem[] blockArray = (BlockListItem[])nextItem.getData();
                
                if (blockArray == null)
                    continue;
                
                for (int b=0; b<blockArray.length; b++)
                {
                    // remove items from list
                    if (blockArray[b] != null)
                        blockLists[b].remove(blockArray[b]);
                }
                
                removeChildrenData(nextItem);
            }
        }
    }
    
    
    protected double yToLat(double y)
    {
        double lat = 2 * Math.atan(Math.exp(y)) - Math.PI/2;
        return lat;
    }
    
    
    protected double latToY(double lat)
    {
        double sinLat = Math.sin(lat);
        double y = 0.5 * Math.log((1 + sinLat) / (1 - sinLat));
        return y;
    }
    
    
    @Override
    public void clearData()
    {
//        error = false;
//        
//        if (dataNode != null)
//        {
//            dataNode.clearAll();
//            dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CLEARED));
//        }       
    }
    
    
    @Override
    public DataNode getDataNode()
    {
        if (!dataNode.isNodeStructureReady())
        {
            //startUpdate(false);
            new MyBboxUpdater(spatialExtent);
        }
        return dataNode;
    }
    
    
    public boolean isSpatialSubsetSupported()
    {
        return true;
    }


    public boolean isTimeSubsetSupported()
    {
        return false;
    }
}