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

package org.vast.stt.provider.google;

import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.QuadTreeVisitor;


/**
 * <p><b>Title:</b>
 * Virtual Earth Tile Number
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Generates a virtual earth tile number by visiting the QuadTree
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Sep 30, 2006
 * @version 1.0
 */
public class GoogleMapTileNumber implements QuadTreeVisitor
{
    protected StringBuffer buf;
    
    
    public GoogleMapTileNumber()
    {
        buf = new StringBuffer();
    }
    
    
    public void visit(QuadTreeItem item)
    {
        // go further only if there is a parent
        QuadTreeItem parent = item.getParent();
        if (parent == null)
            return;
        
        switch (item.getQuadrant())
        {
            case 0:
                buf.insert(0, 't');
                break;
                
            case 1:
                buf.insert(0, 's');
                break;
                
            case 2:
                buf.insert(0, 'r');
                break;
                
            case 3:
                buf.insert(0, 'q');
                break;
        }

        parent.accept(this);
    }

    
    public String getTileNumber()
    {
        return buf.toString();
    }
}
