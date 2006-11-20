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

package org.vast.stt.provider.tiling;

import java.util.ArrayList;
import java.util.Iterator;
import org.ogc.cdm.common.DataComponent;
import org.vast.physics.SpatialExtent;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.DataException;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.TiledMapProvider;


/**
 * <p><b>Title:</b>
 * Spatial Tiling Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Generic Spatial Tiling Provider used by wrapping another
 * spatially subsetable provider.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 14, 2006
 * @version 1.0
 */
public class SpatialTilingProvider extends TiledMapProvider
{
    protected DataProvider subProvider;
    
    
    public SpatialTilingProvider(DataProvider subProvider)
    {
        this.subProvider = subProvider;
        this.setSpatialExtent(subProvider.getSpatialExtent());
        this.subProvider.setSpatialExtent(new STTSpatialExtent());
        this.quadTree = new QuadTree();
        
        // also set max requestable bbox
        maxBbox = new SpatialExtent();
        maxBbox.setMinX(-180);
        maxBbox.setMaxX(+180);
        maxBbox.setMinY(-90);
        maxBbox.setMaxY(+90);
        quadTree.init(maxBbox);

        // setup objects for tile selection
        selectedItems = new ArrayList<QuadTreeItem>(100);
        deletedItems = new ArrayList<BlockListItem>(100);
        tileSelector = new TiledMapSelector(3, 3, 0, 18);        
        tileSize = 512;
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
                // run sub provider with item extent
                subProvider.clearData();
                subProvider.getSpatialExtent().setMinX(item.getMinX());
                subProvider.getSpatialExtent().setMinY(item.getMinY());
                subProvider.getSpatialExtent().setMaxX(item.getMaxX());
                subProvider.getSpatialExtent().setMaxY(item.getMaxY());
                subProvider.getSpatialExtent().setTilingEnabled(false);
                subProvider.updateData();
                
                // add blocks to dataNode
                if (!canceled)
                {
                    BlockListItem[] blockArray = new BlockListItem[blockLists.length];
                    
                    // get blocks from underlying provider
                    ArrayList<BlockList> subProviderLists = subProvider.getDataNode().getListArray();
                    for (int i=0; i<blockLists.length; i++)
                    {
                        Iterator<BlockListItem> it = subProviderLists.get(i).getIterator();
                        if (it.hasNext())
                        {
                            blockArray[i] = it.next();
                            blockLists[i].add(blockArray[i]);
                        }
                        else
                            return;
                    }
                    
                    // add blocks to data node
                    item.setData(blockArray);
                    
                    // remove sub items now that we have the new tile
                    removeChildrenData(item);
                    removeHiddenParent(item);
                    
                    // send event for redraw
                    dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
                }
                //System.out.println(nextItem);
                dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
            }
            catch (DataException e)
            {
                if (!canceled)
                    e.printStackTrace();
            }
            
            subProvider.clearData();
        }        
    }
    
    
    @Override
    public void init() throws DataException
    {
        subProvider.init();
        
        // duplicate block lists of sub provider
        ArrayList<BlockList> subProviderLists = subProvider.getDataNode().getListArray();
        blockLists = new BlockList[subProviderLists.size()];
        for (int i=0; i<blockLists.length; i++)
        {
            DataComponent blockStructure = subProviderLists.get(i).getBlockStructure();
            blockLists[i] = dataNode.createList(blockStructure.copy());
        }
        tileSelector.setItemLists(selectedItems, deletedItems, blockLists);  
        dataNode.setNodeStructureReady(true);
    }
    
    
    @Override
    protected SpatialExtent transformBbox(SpatialExtent extent)
    {
        SpatialExtent newExtent = new SpatialExtent();
        double minX = spatialExtent.getMinX();
        double maxX = spatialExtent.getMaxX();
        double minY = spatialExtent.getMinY();
        double maxY = spatialExtent.getMaxY();
        newExtent.setMinX(minX);
        newExtent.setMaxX(maxX);
        newExtent.setMinY(minY);
        newExtent.setMaxY(maxY);
        return newExtent;
    }
    
    
    @Override
    public void updateData() throws DataException
    {
        // init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        // query tree for matching and unused items
        selectedItems.clear();
        deletedItems.clear();        
        tileSelector.setROI(spatialExtent.copy());
        tileSelector.setCurrentLevel(0);
        tileSelector.setSizeRatio(spatialExtent.getXTiles());
        quadTree.accept(tileSelector);
        
//      first round of cached background items to display
        for (int i=0; i<selectedItems.size(); i++)
        {
            QuadTreeItem nextItem = selectedItems.get(i);
            
            if (canceled)
                break;
            
            if (nextItem.getData() == null)
            {
                // find first parent ready to display
                QuadTreeItem parentItem = nextItem.getParent();
                while (parentItem != null && parentItem.getData() == null)
                    parentItem = parentItem.getParent();
                
                // add parent to list if it's not already there
                if (parentItem != null && parentItem.getData() != null)
                {
                    BlockListItem[] blockArray = (BlockListItem[])parentItem.getData();
                    for (int b=0; b<blockArray.length; b++)
                    {
                        if (!blockArray[b].isLinked())
                            blockLists[b].add(blockArray[b]);
                    }
                }            
            }
        }
        
        // 2nd round of cached items to display
        for (int i=0; i<selectedItems.size(); i++)
        {
            QuadTreeItem nextItem = selectedItems.get(i);
            
            if (canceled)
                break;
            
            if (nextItem.getData() != null)
            {
                BlockListItem[] blockArray = (BlockListItem[])nextItem.getData();
                for (int b=0; b<blockArray.length; b++)
                    blockLists[b].add(blockArray[b]);
                
                // remove children and parent of that item
                removeChildrenData(nextItem);
                removeHiddenParent(nextItem);
            }
        }
        
        // send event for redraw
        if (!canceled)
            dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));

        // 3rd round of new items to load and display
        for (int i=0; i<selectedItems.size(); i++)
        {
            QuadTreeItem nextItem = selectedItems.get(i);
            
            if (canceled)
                break;
            
            if (nextItem.getData() == null)
            {
                getNewTile(nextItem);
            }
        }
        
        // send event to cleanup stylers cache
        if (deletedItems.size() > 0)
        {
            //System.out.println("-" + deletedItems.size()/2);
            dispatchEvent(new STTEvent(deletedItems.toArray(), EventType.PROVIDER_DATA_REMOVED));
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


    @Override
    public boolean isSpatialSubsetSupported()
    {
        return subProvider.isSpatialSubsetSupported();
    }


    @Override
    public boolean isTimeSubsetSupported()
    {
        return subProvider.isTimeSubsetSupported();
    }
}