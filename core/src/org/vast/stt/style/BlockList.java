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

package org.vast.stt.style;

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
    protected BlockListItem currentBlock;
    protected BlockListItem firstBlock;
    protected BlockListItem lastBlock;
    //protected BlockListItem[] fastAccessBlocks; // use if random access needed
    public boolean hasNext;
    
    
    public BlockList()
    {
        this.clear();        
    }
    
    public boolean hasNext()
    {
        return hasNext;
    }
    
    
    public BlockListItem next()
    {
        BlockListItem block = currentBlock;
        currentBlock = currentBlock.nextBlock;
        if (currentBlock == null)
            hasNext = false;
        return block;
    }
    
    
    public void reset()
    {
        if (firstBlock == null)
            hasNext = false;
        else
            hasNext = true;
        currentBlock = firstBlock;
    }
    
    
    public void clear()
    {
        firstBlock = null;
        lastBlock = null;
        size = 0;
    }
    
    
    public void remove()
    {
        currentBlock.prevBlock.nextBlock = currentBlock.nextBlock;
        currentBlock.nextBlock.prevBlock = currentBlock.prevBlock;
        currentBlock = currentBlock.nextBlock;
        size--;
    }
    
    
    public void insertBlock(AbstractDataBlock dataBlock)
    {
        currentBlock = new BlockListItem(dataBlock, currentBlock, currentBlock.nextBlock);
        size++;
    }
    
    
    public void addBlock(AbstractDataBlock dataBlock)
    {
        lastBlock = new BlockListItem(dataBlock, lastBlock, null);
        
        if (firstBlock == null)
            firstBlock = lastBlock;
        
        size++;
    }


    public DataComponent getBlockStructure()
    {
        return blockStructure;
    }


    public void setBlockStructure(DataComponent blockStructure)
    {
        this.blockStructure = blockStructure;
    }


    public int getSize()
    {
        return size;
    }
}
