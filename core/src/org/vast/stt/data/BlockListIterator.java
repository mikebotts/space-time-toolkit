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
 * <p>Copyright (c) 2007</p>
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
