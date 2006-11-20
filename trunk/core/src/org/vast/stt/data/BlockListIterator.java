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

import java.util.Iterator;


/**
 * <p><b>Title:</b><br/>
 * Block List Iterator
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Iterator to loop through BlockList elements.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class BlockListIterator implements Iterator<BlockListItem>
{
    protected BlockList list;
    protected BlockListItem currentItem;
    protected boolean hasNext;
    
    
    public BlockListIterator(BlockList list)
    {
        this.list = list;
        this.reset();
    }
    
    
    public boolean hasNext()
    {
        return hasNext;
    }
    
    
    public BlockListItem next()
    {
        BlockListItem item = currentItem;
        currentItem = currentItem.nextItem;
        if (currentItem == null)
            hasNext = false;
        return item;
    }
    
    
    public void reset()
    {
        if (list.firstItem == null)
            hasNext = false;
        else
            hasNext = true;
        currentItem = list.firstItem;
    }
    
    
    public void remove()
    {
        // TODO remove in BlockListIterator
    }


    public BlockList getList()
    {
        return list;
    }
}
