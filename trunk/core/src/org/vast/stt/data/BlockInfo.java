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

import org.vast.data.AbstractDataBlock;

/**
 * <p><b>Title:</b><br/>
 * Block Info
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO BlockInfo type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class BlockInfo
{
    public AbstractDataBlock data;    
    public BlockInfo nextBlock;
    public BlockInfo prevBlock;
    
    public double minX, maxX;
    public double minY, maxY;
    public double minZ, maxZ;
    public double minT, maxT;
    public double meanStep;
    
    public boolean data_updated;
    public boolean info_updated;
    
    // public BlockInfo[] subBlocks; // use if needed
    
    
    public BlockInfo(AbstractDataBlock data, BlockInfo prevBlock, BlockInfo nextBlock)
    {
        this.data = data;
        this.prevBlock = prevBlock;
        this.nextBlock = nextBlock;
        
        if (prevBlock != null)
            prevBlock.nextBlock = this;
        
        if (nextBlock != null)
            nextBlock.prevBlock = this;
    }
}
