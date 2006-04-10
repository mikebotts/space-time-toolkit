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
    protected DataComponent blockStructure;
    protected BlockInfo currentBlock;
    protected BlockInfo firstBlock;
    protected BlockInfo lastBlock;
    //protected BlockInfo[] fastAccessBlocks; // use if random access needed
    public boolean hasNext;
    
    
    public boolean hasNext()
    {
        return hasNext;
    }
    
    
    public BlockInfo nextInfo()
    {
        BlockInfo block = currentBlock;
        currentBlock = currentBlock.nextBlock;
        if (currentBlock == null)
            hasNext = false;
        return block;
    }
    
    
    public AbstractDataBlock next()
    {
        BlockInfo block = currentBlock;
        currentBlock = currentBlock.nextBlock;
        if (currentBlock == null)
            hasNext = false;
        return block.data;
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
    }
    
    
    public void remove()
    {
        currentBlock.prevBlock.nextBlock = currentBlock.nextBlock;
        currentBlock.nextBlock.prevBlock = currentBlock.prevBlock;
        currentBlock = currentBlock.nextBlock;
    }
    
    
    public void insertBlock(AbstractDataBlock dataBlock)
    {
        currentBlock = new BlockInfo(dataBlock, currentBlock, currentBlock.nextBlock);
    }
    
    
    public void addBlock(AbstractDataBlock dataBlock)
    {
        lastBlock = new BlockInfo(dataBlock, lastBlock, null);
        
        if (firstBlock == null)
            firstBlock = lastBlock;
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
