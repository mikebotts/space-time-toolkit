/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit Cache Engine".
  
 The Initial Developer of the Original Code is Sensia Software LLC.
 Portions created by the Initial Developer are Copyright (C) 2008
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.cache;

import org.vast.data.AbstractDataBlock;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.BlockListIterator;


/**
 * <p><b>Title:</b>
 * BlockListBDB
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO BlockListBDB type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Jan 24, 2009
 */
public class BlockListBDB extends BlockList
{

    @Override
    public void add(BlockListItem newItem)
    {
        // TODO Auto-generated method stub
        super.add(newItem);
    }


    @Override
    public BlockListItem addBlock(AbstractDataBlock dataBlock)
    {
        // TODO Auto-generated method stub
        return super.addBlock(dataBlock);
    }


    @Override
    public void clear()
    {
        // TODO Auto-generated method stub
        super.clear();
    }


    @Override
    public boolean contains(BlockListItem item)
    {
        // TODO Auto-generated method stub
        return super.contains(item);
    }


    @Override
    public BlockList copy()
    {
        // TODO Auto-generated method stub
        return super.copy();
    }


    @Override
    public AbstractDataBlock get(int index)
    {
        // TODO Auto-generated method stub
        return super.get(index);
    }


    @Override
    public BlockListItem getCurrentItem()
    {
        // TODO Auto-generated method stub
        return super.getCurrentItem();
    }


    @Override
    public BlockListItem getFirstItem()
    {
        // TODO Auto-generated method stub
        return super.getFirstItem();
    }


    @Override
    public BlockListIterator getIterator()
    {
        // TODO Auto-generated method stub
        return super.getIterator();
    }


    @Override
    public BlockListItem getLastItem()
    {
        // TODO Auto-generated method stub
        return super.getLastItem();
    }


    @Override
    public int getSize()
    {
        // TODO Auto-generated method stub
        return super.getSize();
    }


    @Override
    public void remove(BlockListItem item)
    {
        // TODO Auto-generated method stub
        super.remove(item);
    }

}
