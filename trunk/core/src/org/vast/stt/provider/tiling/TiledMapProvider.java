/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.tiling;

import java.util.ArrayList;

import org.ogc.cdm.common.DataType;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.physics.SpatialExtent;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.DataException;
import org.vast.stt.data.DataNode;
import org.vast.stt.dynamics.SceneBboxUpdater;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.tiling.QuadTree;


public abstract class TiledMapProvider extends AbstractProvider
{
    protected QuadTree quadTree;
    protected BlockList[] blockLists = new BlockList[2]; // 0 for imagery, 1 for grid
    protected DataArray gridData;
    protected boolean useAlpha = false;
    protected SpatialExtent maxBbox;
    protected TiledMapSelector tileSelector;
    protected ArrayList<QuadTreeItem> selectedItems;
    protected ArrayList<BlockListItem> deletedItems;
    
    
    public TiledMapProvider()
	{
        quadTree = new QuadTree();
        
        // also set max requestable bbox
        maxBbox = new SpatialExtent();
        maxBbox.setMinX(-Math.PI);
        maxBbox.setMaxX(+Math.PI);
        maxBbox.setMinY(-Math.PI);
        maxBbox.setMaxY(+Math.PI);
        quadTree.init(maxBbox);

        // setup objects for tile selection
        selectedItems = new ArrayList<QuadTreeItem>(30);
        deletedItems = new ArrayList<BlockListItem>(100);
        tileSelector = new TiledMapSelector(2, 5, 18);
        tileSelector.setItemLists(selectedItems, deletedItems, blockLists);
        
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
        if (useAlpha)
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
    public abstract void updateData() throws DataException;
    
    
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
        QuadTreeItem parent = item.getParent();
        if (parent == null)
            return;
                
        // remove parent blocks
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
        
        //System.out.println("Item unselected " + item);
        
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
            new SceneBboxUpdater(spatialExtent);
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