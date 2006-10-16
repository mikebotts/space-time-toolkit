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
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.provider.tiling.ExtentSelector;
import org.vast.stt.provider.tiling.QuadTreeItem;


public class TiledMapSelector extends ExtentSelector
{
    protected ArrayList<QuadTreeItem> selectedItems;
    protected ArrayList<BlockListItem> deletedItems;
    protected BlockList[] blockLists;
    protected double maxDistance2; // square of max distance in number of tiles
    
    
    public TiledMapSelector(double sizeRatio, double maxDistance, int maxLevel)
    {
        super(sizeRatio, maxLevel);
        this.maxDistance2 = maxDistance * maxDistance;
    }
    
    
    public void setItemLists(ArrayList<QuadTreeItem> selectedItems,
                             ArrayList<BlockListItem> deletedItems,
                             BlockList[] blockLists)
    {
        this.selectedItems = selectedItems;
        this.deletedItems = deletedItems;
        this.blockLists = blockLists;
    }
    
    
    /**
     * Computes square of euclidean distance from
     * center of roi to center of item bbox.
     * @param item
     * @return
     */
    protected double distanceTo(QuadTreeItem item)
    {
        double dX = (roi.getMinX() + roi.getMaxX() - item.getMinX() - item.getMaxX()) / 2;
        double dY = (roi.getMinY() + roi.getMaxY() - item.getMinY() - item.getMaxY()) / 2;
        double distance2 = dX*dX + dY*dY;
        return distance2;
    }
    
    
    /**
     * Recursively remove data from blockLists
     * but keep the underlying data in cache.
     * @param item
     */
    public void removeFromList(QuadTreeItem item)
    {
        BlockListItem[] blockArray = (BlockListItem[])item.getData();
        
        // remove data blocks from list
        if (blockArray != null)
        {                
            boolean tooFar = (distanceTo(item) > item.getTileSize()*maxDistance2);
            
            for (int b=0; b<blockArray.length; b++)
            {
                // remove items from list
                if (blockArray[b] != null)
                    blockLists[b].remove(blockArray[b]);
                
                // add to deleted items list if too far from roi
                if (tooFar)
                    deletedItems.add(blockArray[b]);
            }
        
            if (tooFar)
            {
                item.getParent().setChild(item.getQuadrant(), null);
                item.setData(null);
                // System.out.println("Item deleted " + item);                
            }
                
        }
        
        // loop through children
        for (byte i=0; i<4; i++)
        {
            QuadTreeItem childItem = item.getChild(i);
            if (childItem != null)
                removeFromList(childItem);
        }
    }
    
    
    @Override
    protected void deselectItem(QuadTreeItem item)
    {
        removeFromList(item);        
        //System.out.println("Item unselected " + item);
    }
    

    @Override
    protected void selectItem(QuadTreeItem item)
    {
        double dX =  (roi.getMinX() + roi.getMaxX() - item.getMinX() - item.getMaxX()) / 2;
        double dY =  (roi.getMinY() + roi.getMaxY() - item.getMinY() - item.getMaxY()) / 2;
        double distance = dX*dX + dY*dY;
        item.setDistance(distance);
        
        if (selectedItems.isEmpty())
        {
            selectedItems.add(item);
        }
        else
        {
            // find the position where to insert this (order by distance to roi)
            for (int i=0; i<selectedItems.size(); i++)
            {
                if (selectedItems.get(i).getDistance() >= distance)
                {
                    selectedItems.add(i, item);
                    //System.out.println("Item selected " + item);
                    return;
                }
            }
            
            // if we didn't add it yet, add at end of list
            selectedItems.add(item);            
        }
        
        //System.out.println("Item selected " + item);
    }
}