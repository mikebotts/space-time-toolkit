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

package org.vast.stt.provider.google;

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
import org.vast.physics.SpatialExtent;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.DataException;
import org.vast.stt.data.DataNode;
import org.vast.stt.dynamics.MyBboxUpdater;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.tiling.QuadTree;
import org.vast.stt.provider.tiling.QuadTreeItem;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.PNGDecodeParam;


/**
 * <p><b>Title:</b><br/>
 * Windows Live Local Virtual Earth Provider
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
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 14, 2005
 * @version 1.0
 */
public class GoogleMapProvider extends AbstractProvider
{
    private final static double DTR = Math.PI/180;
    protected QuadTree quadTree;
    protected BlockList[] blockLists = new BlockList[2]; // 0 for imagery, 1 for grid
    protected DataArray gridData;
    protected String layerId = "roads";
    protected SpatialExtent maxBbox;
    
    
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
                    item.acceptUp(tileNumberGen);
                    String q = tileNumberGen.getTileNumber();
                    urlString = "http://kh3.google.com/kh?n=404&v=10&t=t" + q;
                }
                else if (layerId.startsWith("roads"))
                {
                    // build request URL for road/boundary data
                    GoogleMapTileXYZ tileXYZGen = new GoogleMapTileXYZ();
                    item.acceptUp(tileXYZGen);
                    int x = tileXYZGen.getX();
                    int y = tileXYZGen.getY();
                    int z = tileXYZGen.getZoom();
                    urlString = "http://mt0.google.com/mt?n=404&v=w2t.26&x=" + x + "&y=" + y + "&zoom=" + z;
                }
                else if (layerId.startsWith("map"))
                {
                    // build request URL for map data
                    GoogleMapTileXYZ tileXYZGen = new GoogleMapTileXYZ();
                    item.acceptUp(tileXYZGen);
                    int x = tileXYZGen.getX();
                    int y = tileXYZGen.getY();
                    int z = tileXYZGen.getZoom();
                    urlString = "http://mt0.google.com/mt?n=404&v=w2.25&x=" + x + "&y=" + y + "&zoom=" + z;
                }
                
                System.out.println(urlString);
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("Referer", "http://maps.google.com");
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
                    removeParent(item);
                    
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
        
    
    public GoogleMapProvider()
	{
        quadTree = new QuadTree();
        
        // also set max requestable bbox
        maxBbox = new SpatialExtent();
        maxBbox.setMinX(-Math.PI);
        maxBbox.setMaxX(+Math.PI);
        maxBbox.setMinY(-Math.PI);
        maxBbox.setMaxY(+Math.PI);
        quadTree.init(maxBbox);
        
        this.autoUpdate = true;
	}


    @Override
    public void init() throws DataException
    {
        // create block list for textures
        DataGroup pixelData = new DataGroup(3);
        pixelData.setName("pixel");
        pixelData.addComponent("red", new DataValue(DataType.BYTE));
        pixelData.addComponent("green", new DataValue(DataType.BYTE));
        pixelData.addComponent("blue", new DataValue(DataType.BYTE));
        if (layerId.equals("roads"))
            pixelData.addComponent("alpha", new DataValue(DataType.BYTE));
        
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
        STTSpatialExtent mercatorExtent = new STTSpatialExtent();
        double minX = spatialExtent.getMinX() * DTR;
        double maxX = spatialExtent.getMaxX() * DTR;
        double minY = latToY(spatialExtent.getMinY() * DTR);
        double maxY = latToY(spatialExtent.getMaxY() * DTR);
        mercatorExtent.setMinX(Math.max(minX, maxBbox.getMinX()));
        mercatorExtent.setMaxX(Math.min(maxX, maxBbox.getMaxX()));
        mercatorExtent.setMinY(Math.max(minY, maxBbox.getMinY()));
        mercatorExtent.setMaxY(Math.min(maxY, maxBbox.getMaxY()));
        
        // query tree for matching and unused items
        ArrayList<QuadTreeItem> matchingItems = new ArrayList<QuadTreeItem>(30);
        ArrayList<QuadTreeItem> unusedItems = new ArrayList<QuadTreeItem>(30);
        quadTree.findItems(matchingItems, unusedItems, mercatorExtent, 18, 5);
        
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
            //System.out.println("Item unused " + nextItem);
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
     * but keep the underlying data in cache.
     * @param item
     * @param list
     */
    protected void removeChildrenData(QuadTreeItem item)
    {
        // loop through children
        for (byte i=0; i<4; i++)
        {
            QuadTreeItem childItem = item.getChild(i);
            if (childItem != null)
            {
                BlockListItem[] blockArray = (BlockListItem[])childItem.getData();
                
                if (blockArray != null)
                {                
                    for (int b=0; b<blockArray.length; b++)
                    {
                        // remove items from list
                        if (blockArray[b] != null)
                            blockLists[b].remove(blockArray[b]);
                    }
                }
                
                removeChildrenData(childItem);
            }
        }
    }
    
    
    /**
     * Remove parent if all its children are shown
     * @param item
     */
    protected void removeParent(QuadTreeItem item)
    {
        QuadTreeItem parent = item.getParent();
        for (byte i=0; i<4; i++)
        {
            QuadTreeItem child = parent.getChild(i);
            if (child.getData() == null)
                return;
        }
        
        // remove parent blocks from lists
        BlockListItem[] blockArray = (BlockListItem[])parent.getData();        
        if (blockArray != null)
        {                
            for (int b=0; b<blockArray.length; b++)
            {
                // remove items from list
                if (blockArray[b] != null)
                    blockLists[b].remove(blockArray[b]);
            }
        }
        
        removeParent(parent);
    }
    
    
    protected double yToLat(double y)
    {
        double lat = 0;
        if (Math.abs(y) < Math.PI-(1e-4))
            //lat = 2 * Math.atan(Math.exp(y)) - Math.PI/2;
            lat = Math.PI/2 - 2 * Math.atan(Math.exp(-y));
        else
            lat = Math.signum(y)*Math.PI/2;
        
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
    
    
    public void setLayer(String layerId)
    {
        this.layerId = layerId;
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