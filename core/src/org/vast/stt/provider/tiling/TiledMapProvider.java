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

import java.util.ArrayList;

import org.vast.cdm.common.DataType;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.physics.SpatialExtent;
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


public abstract class TiledMapProvider extends AbstractProvider
{
    protected final static double DTR = Math.PI/180;
    protected QuadTree quadTree;
    protected BlockList[] blockLists = new BlockList[2]; // 0 for imagery, 1 for grid
    protected DataArray gridData;
    protected boolean useAlpha = false;
    protected SpatialExtent maxBbox;
    protected TiledMapSelector tileSelector;
    protected ArrayList<QuadTreeItem> selectedItems;
    protected ArrayList<BlockListItem> deletedItems;
    protected int tileWidth, tileHeight;
    
    
    protected abstract void getNewTile(QuadTreeItem item);
    protected abstract SpatialExtent transformBbox(SpatialExtent extent);
    
    
    public TiledMapProvider()
    {        
    }
    
    
    public TiledMapProvider(int tileWidth, int tileHeight, int maxLevel)
	{
        quadTree = new QuadTree();
        
        // setup objects for tile selection
        selectedItems = new ArrayList<QuadTreeItem>(100);
        deletedItems = new ArrayList<BlockListItem>(100);
        tileSelector = new TiledMapSelector(3, 3, 0, maxLevel);
        tileSelector.setItemLists(selectedItems, deletedItems, blockLists);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
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
        selectedItems.clear();
        deletedItems.clear();
        tileSelector.setROI(newExtent);
        tileSelector.setCurrentLevel(0);
        double tileRatio = Math.floor(spatialExtent.getXTiles()) * Math.floor(spatialExtent.getYTiles());
        tileSelector.setSizeRatio(tileRatio);
        tileSelector.setMaxDistance(Math.sqrt(tileRatio)*3);
        quadTree.accept(tileSelector);
        
        // first round of cached background items to display
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
    
    
    /**
     * Removes children data when lower level of details
     * is shown.
     * @param item
     */
    protected void removeChildrenData(QuadTreeItem item)
    {
        // loop through children
        for (byte i=0; i<4; i++)
        {
            QuadTreeItem childItem = item.getChild(i);
            if (childItem != null)
                tileSelector.removeFromList(childItem);
        }
    }
    
        
    /**
     * Remove parent if all its children are shown
     * @param item
     */
    protected void removeParent(QuadTreeItem item)
    {
        boolean skip = false;
        QuadTreeItem parent = item.getParent();
        if (parent == null)
            return;        
        
        // skip if one of needed children is not loaded
        for (int i=0; i<4; i++)
        {
            QuadTreeItem child = parent.getChild(i);
            if (child != null && child.isNeeded())
            {
                if (child.getData() == null)
                    skip = true;
            }
        }
        
        // remove parent blocks
        if (!skip)
        {
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
            
            parent.needed = false;
        }
        
        //System.out.println("Item unselected " + item);
        
        removeParent(parent);
    }
    
    
    protected void removeHiddenParent(QuadTreeItem item)
    {
//        QuadTreeItem parent = item.getParent();
//        if (parent == null)
//            return;
//        
//        // skip if one of the children is not loaded
//        for (int i=0; i<4; i++)
//        {
//            QuadTreeItem child = parent.getChild(i);
//            
//            if (child == null)
//                return;
//            
//            if (child.getData() == null)
//                return;
//        }
        
        removeParent(item);
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
        {
            startUpdate(true);            
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