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
    protected BlockListItem firstBlock;
    protected BlockListItem lastBlock;
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
        firstBlock = null;
        lastBlock = null;
        size = 0;
    }
    
    
    public void remove(BlockListItem item)
    {
        boolean endItem = false;
        
        if (item.nextBlock == null && item.prevBlock == null)
            return;
        
        // if item is first in list
        if (item == firstBlock)
        {
            firstBlock = item.nextBlock;
            if (firstBlock != null)
                firstBlock.prevBlock = null;
            endItem = true;
        }
        
        // if item is last in list
        if (item == lastBlock)
        {
            lastBlock = item.prevBlock;
            if (lastBlock != null)
                lastBlock.nextBlock = null;
            endItem = true;
        }
        
        // if item is in middle of list
        if (!endItem)
        {
            // connect previous to next
            item.prevBlock.nextBlock = item.nextBlock;
            item.nextBlock.prevBlock = item.prevBlock;
        }
        
        // set both prev and next to null
        item.nextBlock = null;
        item.prevBlock = null;
        
        // reduce list size
        size--;        
    }
    
    
    public BlockListItem addBlock(AbstractDataBlock dataBlock)
    {
        BlockListItem newBlock = new BlockListItem(dataBlock, null, null);
        add(newBlock);        
        return newBlock;
    }
    
    
    public void add(BlockListItem newItem)
    {
        this.remove(newItem);
        
        newItem.prevBlock = lastBlock;
        newItem.nextBlock = null;
        
        if (lastBlock != null)
            lastBlock.nextBlock = newItem;
        
        if (firstBlock == null)
            firstBlock = newItem;
        
        lastBlock = newItem;
        
        size++;
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
