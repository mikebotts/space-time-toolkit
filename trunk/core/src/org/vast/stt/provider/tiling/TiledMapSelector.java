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

import org.vast.stt.data.BlockListItem;
import org.vast.stt.provider.tiling.ExtentSelector;
import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.util.SpatialExtent;


public class TiledMapSelector extends ExtentSelector
{
    protected TiledMapProvider provider;
    protected BlockListItem[] firstSelectedBlocks;
    protected double maxDistance2; // square of max distance in number of tiles
    protected boolean hidePartiallyVisibleParents;
    
    
    public TiledMapSelector(int minLevel, int maxLevel, TiledMapProvider provider)
    {
        super(minLevel, maxLevel);
        this.provider = provider;
        this.firstSelectedBlocks = new BlockListItem[provider.blockLists.length];
    }
    
    
    @Override
    public void setROI(SpatialExtent roi)
    {
        super.setROI(roi);
        
        // reset first block pointers to null
        for (int i=0; i<firstSelectedBlocks.length; i++)
            firstSelectedBlocks[i] = null;
    }
    
    
    /**
     * Computes square of euclidean distance from
     * center of roi to center of item bbox.
     * @param item
     * @return
     */
    protected double distanceTo(QuadTreeItem item)
    {
        double dX1 = (roi1.getMinX() + roi1.getMaxX() - item.getMinX() - item.getMaxX()) / 2;
        double dY1 = (roi1.getMinY() + roi1.getMaxY() - item.getMinY() - item.getMaxY()) / 2;
        double dist1 = dX1*dX1 + dY1*dY1;
        
        if (splitROI)
        {
            double dX2 = (roi2.getMinX() + roi2.getMaxX() - item.getMinX() - item.getMaxX()) / 2;
            double dY2 = (roi2.getMinY() + roi2.getMaxY() - item.getMinY() - item.getMaxY()) / 2;
            double dist2 = dX2*dX2 + dY2*dY2;
            return Math.min(dist1, dist2);
        }
        
        return dist1;
    }
    
    
    /**
     * Add item blocks to block lists
     * @param item
     */
    public void appendToBlockLists(QuadTreeItem item)
    {
        BlockListItem[] itemBlocks = (BlockListItem[])item.getData();
        
        if (itemBlocks != null)
        {
            for (int b=0; b<itemBlocks.length; b++)
            {
                provider.blockLists[b].add(itemBlocks[b]);
                if (firstSelectedBlocks[b] == null)
                    firstSelectedBlocks[b] = itemBlocks[b];
            }
        }
        
        // make sure item is not in discard list
        provider.itemsToDiscard.remove(item);
    }
    
    
    /**
     * Remove the item data from block lists
     * @param item
     */
    public void removeFromBlockLists(QuadTreeItem item)
    {
        //System.out.println("Item removed " + item);
        BlockListItem[] itemBlocks = (BlockListItem[])item.getData();
                
        // remove data blocks from lists
        if (itemBlocks != null)
        {                
            for (int b=0; b<itemBlocks.length; b++)
            {
                // remove items from list
                if (itemBlocks[b] != null)
                    provider.blockLists[b].remove(itemBlocks[b]);
            }
        }
        
        // add to discard list if too far
        boolean tooFar = (distanceTo(item) > item.getTileSize()*maxDistance2);
        if (tooFar)
            provider.itemsToDiscard.add(item);
        
        item.setNeeded(false);
    }
    
    
    /**
     * Recursively remove children data from block lists
     * @param item
     */
    public void removeDescendantsFromBlockLists(QuadTreeItem item)
    {
        for (byte i=0; i<4; i++)
        {
            QuadTreeItem childItem = item.getChild(i);
            if (childItem != null)
            {
                // remove child data from lists
                removeFromBlockLists(childItem);
                
                // recursively remove children
                removeDescendantsFromBlockLists(childItem);
            }
        }
    }
    
    
    /**
     * Recursively remove hidden ancestors data from block lists
     * @param item
     */
    public void removeHiddenAncestorsFromBlockLists(QuadTreeItem item)
    {
        boolean doRemove = false;
        QuadTreeItem parent = item.getParent();
        if (parent == null)
            return;
        
        // skip if one of needed children is not loaded
        doRemove = areAllChildrenLoaded(parent) || hidePartiallyVisibleParents;
        
        // remove parent data from lists
        if (doRemove)
            removeFromBlockLists(parent);
        
        // recursively remove parents
        removeHiddenAncestorsFromBlockLists(parent);
    }
    
    
    protected boolean areAllChildrenLoaded(QuadTreeItem item)
    {
        if (item == null)
            return false;
        
        for (int i=0; i<4; i++)
        {
            QuadTreeItem child = item.getChild(i);
            if (child != null && child.isNeeded())
            {
                if (child.getData() == null)
                    return false;
            }
        }
        
        return true;
    }
    
    
    @Override
    protected void deselectItem(QuadTreeItem item)
    {
        //System.out.println("Item unselected " + item);
        removeFromBlockLists(item);
        removeDescendantsFromBlockLists(item);
    }
    

    @Override
    protected void selectItem(QuadTreeItem item)
    {
        double distance = distanceTo(item);
        item.setScore((float)(1./distance));
        //System.out.println("Item selected " + item);
        
        // add blocks to list now if data is available in cache
        if (item.data != null)
        {
            appendToBlockLists(item);
            removeDescendantsFromBlockLists(item);
            removeHiddenAncestorsFromBlockLists(item);
        }       
        
        // otherwise display ancestor and add to load queue
        else
        {
            // find first cached ancestor
            // except when hiding partially visible parents
            if (!hidePartiallyVisibleParents)
            {
                QuadTreeItem parentItem = item.getParent();
                while (parentItem != null && parentItem.getData() == null)
                    parentItem = parentItem.getParent();
                
                // add parent to list if it's not already there
                if (parentItem != null && parentItem.getData() != null)
                {
                    BlockListItem[] blockArray = (BlockListItem[])parentItem.getData();
                    for (int b=0; b<blockArray.length; b++)
                    {
                        if (!provider.blockLists[b].contains(blockArray[b]))
                        {
                            // append before selected items block if any have already been added
                            // this is to ensure that they highest resolution is rendered last and thus on top
                            if (firstSelectedBlocks[b] != null)
                                provider.blockLists[b].insertBefore(blockArray[b], firstSelectedBlocks[b]);
                            else
                                provider.blockLists[b].add(blockArray[b]);
                        }
                        
                        // make sure item is not in discard list
                        provider.itemsToDiscard.remove(parentItem);
                    }
                }
            }
            
            // add item to loading queue
            // and notify tile loader threads
            synchronized(provider.itemsToLoad)
            {
                provider.itemsToLoad.add(item);
                provider.itemsToLoad.notify();
                //System.out.println("Item in queue " + item);
            }
        }
    }
    
    
    public void setMaxDistance(double maxDistance)
    {
    	this.maxDistance2 = maxDistance * maxDistance;
    }


    public void setHidePartiallyVisibleParents(boolean alwaysRemoveParents)
    {
        this.hidePartiallyVisibleParents = alwaysRemoveParents;
    }
}
