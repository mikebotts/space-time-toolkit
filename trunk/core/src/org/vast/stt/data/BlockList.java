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

import org.ogc.cdm.common.DataComponent;
import org.vast.data.AbstractDataBlock;


/**
 * <p><b>Title:</b><br/>
 * Block List
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO BlockList type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class BlockList
{
    protected int size = 0;
    protected DataComponent blockStructure;
    protected BlockListItem firstItem;
    protected BlockListItem lastItem;
    protected BlockListItem currentItem;
    protected int currentIndex = -1;
    //protected BlockListItem[] fastAccessBlocks; // use if random access needed
    
    
    public BlockList()
    {
        this.clear();        
    }
    
    
    public BlockList copy()
    {
        BlockList newList = new BlockList();
        newList.blockStructure = this.blockStructure.copy();
        return newList;
    }
    
    
    public BlockListIterator getIterator()
    {
        return new BlockListIterator(this);
    }
    
    
    public void clear()
    {
        firstItem = null;
        lastItem = null;
        currentItem = null;
        currentIndex = -1;
        size = 0;
    }
    
    
    public void remove(BlockListItem item)
    {
        boolean endItem = false;
        
        if (item.nextItem == null && item.prevItem == null)
            return;
        
        // if item is first in list
        if (item == firstItem)
        {
            firstItem = item.nextItem;
            if (firstItem != null)
                firstItem.prevItem = null;
            endItem = true;
        }
        
        // if item is last in list
        if (item == lastItem)
        {
            lastItem = item.prevItem;
            if (lastItem != null)
                lastItem.nextItem = null;
            endItem = true;
        }
        
        // if item is in middle of list
        if (!endItem)
        {
            // connect previous to next
            item.prevItem.nextItem = item.nextItem;
            item.nextItem.prevItem = item.prevItem;
        }
        
        // set both prev and next to null
        item.nextItem = null;
        item.prevItem = null;
        
        // reduce list size
        size--;        
    }
    
    
    public BlockListItem addBlock(AbstractDataBlock dataBlock)
    {
        BlockListItem newItem = new BlockListItem(dataBlock, null, null);
        add(newItem);        
        return newItem;
    }
    
    
    public void add(BlockListItem newItem)
    {
        this.remove(newItem);
        
        newItem.prevItem = lastItem;
        newItem.nextItem = null;
        
        if (lastItem != null)
            lastItem.nextItem = newItem;
        
        if (firstItem == null)
            firstItem = newItem;
        
        lastItem = newItem;
        
        size++;
    }
    
    
    public AbstractDataBlock get(int index)
    {
        BlockListItem item = null;
        
        int distCurrent = index - currentIndex;
        int distFirst = index;
        //int distLast = size - 1 - index;
            
        if (currentIndex != -1 && Math.abs(distCurrent) <= distFirst)
        {
            item = currentItem;
            
            if (distCurrent >= 0)
            {
                while (item.nextItem != null && currentIndex != index)
                {
                    item = item.nextItem;
                    currentIndex++;
                }
            }
            else
            {
                while (item.prevItem != null && currentIndex != index)
                {
                    item = item.prevItem;
                    currentIndex--;
                }
            }
        }
        else
        {
            item = firstItem;
            int i;
            for (i=0; i<index && item.nextItem != null; i++)
                item = item.nextItem;
            currentIndex = i;
        }
        
        currentItem = item;
        return item.data;
    }
    
    
    public int getSize()
    {
        return size;
    }


    public DataComponent getBlockStructure()
    {
        return blockStructure;
    }


    public void setBlockStructure(DataComponent blockStructure)
    {
        this.blockStructure = blockStructure;
    }
}
