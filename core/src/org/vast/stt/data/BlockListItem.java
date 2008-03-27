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

import org.vast.data.AbstractDataBlock;

/**
 * <p><b>Title:</b><br/>
 * Block List Item
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO BlockListItem type description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class BlockListItem
{
    //protected BlockInfo info;
    protected AbstractDataBlock data;    
    protected BlockListItem nextItem;
    protected BlockListItem prevItem;
    protected boolean hidden;
    public int blockCount = 0;
    
    
    public BlockListItem(AbstractDataBlock data, BlockListItem prevBlock, BlockListItem nextBlock)
    {
        this.data = data;
        this.prevItem = prevBlock;
        this.nextItem = nextBlock;
        
        if (prevBlock != null)
            prevBlock.nextItem = this;
        
        if (nextBlock != null)
            nextBlock.prevItem = this;
    }
    

    public AbstractDataBlock getData()
    {
        return data;
    }


    public void setData(AbstractDataBlock data)
    {
        this.data = data;
    }


//    public BlockInfo getInfo()
//    {
//        if (info == null)
//            info = new BlockInfo();
//        
//        return info;
//    }
//
//
//    public void setInfo(BlockInfo info)
//    {
//        this.info = info;
//    }
    
    
    public boolean isHidden()
    {
        return hidden;
    }


    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }
}
