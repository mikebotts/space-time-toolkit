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

package org.vast.stt.provider.tiling;

import java.util.Iterator;
import java.util.LinkedList;
import org.vast.cdm.common.DataType;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.DataException;
import org.vast.stt.data.DataNode;
import org.vast.stt.dynamics.SceneBboxUpdater;
import org.vast.stt.dynamics.SpatialExtentUpdater;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.tiling.QuadTree;
import org.vast.util.SpatialExtent;


public abstract class TiledMapProvider extends AbstractProvider
{
    protected final static double DTR = Math.PI/180;
    protected double fixedAltitude = 0.0;
    protected QuadTree quadTree;
    protected BlockList[] blockLists = new BlockList[2]; // 0 for imagery, 1 for grid
    protected DataArray gridData;
    protected boolean useAlpha = false;
    protected SpatialExtent maxBbox;
    protected TiledMapSelector tileSelector;
    protected LinkedList<QuadTreeItem> itemsToLoad;
    protected LinkedList<QuadTreeItem> itemsToDiscard;
    protected int tileWidth, tileHeight;
    protected TileThread[] threadPool;
    
    
    protected class TileThread extends Thread
    {
        public void run()
        {
            while (!itemsToLoad.isEmpty())
            {
                // find best item
                QuadTreeItem bestItem = itemsToLoad.getFirst();
                Iterator<QuadTreeItem> it = itemsToLoad.iterator();
                while (it.hasNext())
                {
                    QuadTreeItem nextItem = it.next();
                    if (nextItem.getScore() > bestItem.getScore())
                        bestItem = nextItem;
                }
                
                // remove from queue
                //System.out.println("Loading " + bestItem);
                itemsToLoad.remove(bestItem);
                
                if (canceled)
                    return;
                
                // load and update lists
                getNewTile(bestItem);
                
                //BlockListItem[] itemBlocks = (BlockListItem[])bestItem.getData();
                //System.out.println(itemBlocks[0] + " loaded");
                
                if (canceled)
                    return;                
                
                tileSelector.appendToBlockLists(bestItem);
                tileSelector.removeDescendantsFromBlockLists(bestItem);
                tileSelector.removeHiddenAncestorsFromBlockLists(bestItem);
                
                //System.out.println(itemBlocks[0] + " added to block list");
                
                // send event for redraw
                dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
            }
                        
            
            /*QuadTreeItem bestItem = null;
            
            while (true)
            {
                synchronized(itemsToLoad)
                {
                    try
                    {
                        while (itemsToLoad.isEmpty())
                            itemsToLoad.wait();
                        if (itemsToLoad.isEmpty())
                            return;
                        
                        // get item with best score
                        bestItem = itemsToLoad.getFirst();
                        Iterator<QuadTreeItem> it = itemsToLoad.iterator();
                        while (it.hasNext())
                        {
                            QuadTreeItem nextItem = it.next();
                            if (nextItem.getScore() > bestItem.getScore())
                                bestItem = nextItem;
                        }
                        
                        itemsToLoad.remove(bestItem);
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }
                }
                
                getNewTile(bestItem);
                
                // update lists
                tileSelector.appendToBlockLists(bestItem);
                tileSelector.removeDescendantsFromBlockLists(bestItem);
                tileSelector.removeHiddenAncestorsFromBlockLists(bestItem);
                
                // send event for redraw
                dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
            }*/
        }
    };
    
    
    protected abstract void getNewTile(QuadTreeItem item);
    protected abstract SpatialExtent transformBbox(SpatialExtent extent);
    
    
    public TiledMapProvider()
    {
    	quadTree = new QuadTree();
    	itemsToLoad = new LinkedList<QuadTreeItem>();
    	itemsToDiscard = new LinkedList<QuadTreeItem>();
    }
    
    
    public TiledMapProvider(int tileWidth, int tileHeight, int maxLevel)
    {
        this();
        tileSelector = new TiledMapSelector(0, maxLevel, this);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }
    
    
    protected void initThreadPool(int numThreads)
    {
        threadPool = new TileThread[numThreads];
        
        for (int t=0; t<numThreads; t++)
        {
            threadPool[t] = new TileThread();
            //threadPool[t].start();
        }
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
        if (useAlpha)
            pixelData.addComponent("alpha", new DataValue(DataType.BYTE));
        
        DataArray rowData = new DataArray(256);
        rowData.addComponent(pixelData);
        rowData.setName("row");
        DataArray imageData = new DataArray(256);
        imageData.addComponent(rowData);
        imageData.setName("image");
        blockLists[0] = dataNode.createList(imageData);
        //System.out.println(imageData);
        
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
        //System.out.println(gridData);
        
        // set tile sizes in updater
        SpatialExtentUpdater updater = this.getSpatialExtent().getUpdater();
        if (updater != null)
        {
            if (updater instanceof SceneBboxUpdater)
                ((SceneBboxUpdater)updater).setTilesize(tileWidth, tileHeight);
            updater.update();
        }
        
        dataNode.setNodeStructureReady(true);
    }
    
    
    @Override
    public void updateData() throws DataException
    {
        // init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        // convert extent to mercator projection
        SpatialExtent newExtent = transformBbox(spatialExtent);
        
        // query tree for matching and unused items 
        itemsToLoad.clear();
        itemsToDiscard.clear();
        tileSelector.setROI(newExtent);
        tileSelector.setCurrentLevel(0);
        double tileRatio = spatialExtent.getXTiles() * spatialExtent.getYTiles();
        tileSelector.setSizeRatio(tileRatio*0.35);
        tileSelector.setMaxDistance(Math.sqrt(tileRatio));
        tileSelector.setHidePartiallyVisibleParents(useAlpha);
        quadTree.accept(tileSelector);

        // send event for redraw
        if (!canceled)
            dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
        
        // discard items
        if (itemsToDiscard.size() > 0)
        {
            LinkedList<Object> blocksToDiscard = new LinkedList<Object>();
            while (!itemsToDiscard.isEmpty())
            {
                QuadTreeItem item = itemsToDiscard.poll();
                                
                // add blocks to be deleted
                BlockListItem[] itemBlocks = (BlockListItem[])item.getData();
                
                if (itemBlocks != null && blockLists[0].contains(itemBlocks[0]))
                    System.out.println(">>>>" + itemBlocks[0] + " STILL IN LIST !!");
                
                if (itemBlocks != null && !blockLists[0].contains(itemBlocks[0]))
                {
                    for (int b=0; b<itemBlocks.length; b++)
                        blocksToDiscard.add(itemBlocks[b]);
                }
                
                item.setData(null);
                
                // need to make sure we don't have children with data
                // since they can still have associated textures and DL
                // if not, we can even remove quad tree item
                //if (!item.hasChildren())
                //    item.removeFromParent(); 
            }
            
            //System.out.println(blocksToDiscard.size() + " items to be deleted");
            if (blocksToDiscard.size() > 0)
                dispatchEvent(new STTEvent(blocksToDiscard.toArray(), EventType.PROVIDER_DATA_REMOVED));
        }
        
        threadPool[0].run();
        
        // print debug info
        //blockLists[0].checkConsistency();
        //System.out.println("Lists size = " + blockLists[0].getSize() + ", " + blockLists[1].getSize());
        //QuadTreeItemCounter counter = new QuadTreeItemCounter();
        //quadTree.accept(counter);
        //System.out.println("quadtree size = " + counter.numItems + " ("
        //                                      + counter.numItemsWithData + ")");
    }
    
    
    @Override
    public void cancelUpdate()
    {
        super.cancelUpdate();
        
        /*synchronized(itemsToLoad)
        {
            itemsToLoad.clear();
        }*/
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
        
        y = Math.max(y, -Math.PI);
        y = Math.min(y, Math.PI);
        
        return y;
    }
    
    
    @Override
    public DataNode getDataNode()
    {
        if (!dataNode.isNodeStructureReady())
            startUpdate(true);            
                
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