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

package org.vast.stt.provider.tiling;

import java.util.ArrayList;
import org.vast.stt.data.DataException;
import org.vast.stt.data.DataNode;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Quad Tree Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Provider using a SensorML process model or chain to produce data.
 * A chain can itself include data sources and further processing steps.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Feb 28, 2006
 * @version 1.0
 */
public class TimeTilingProvider extends AbstractProvider
{
    protected DataProvider subProvider;
    protected QuadTree quadTree;
    
    
    public TimeTilingProvider(DataProvider subProvider)
	{
        quadTree = new QuadTree();
        this.subProvider = subProvider;        
        subProvider.getSpatialExtent().removeAllListeners();
        subProvider.getTimeExtent().removeAllListeners();        
        this.setSpatialExtent(subProvider.getSpatialExtent());
        this.setTimeExtent(subProvider.getTimeExtent());
        subProvider.setSpatialExtent(new STTSpatialExtent());
	}
	
    
    @Override
    public void init() throws DataException
    {
        subProvider.init();
        dataNode = subProvider.getDataNode();        
    }
    
    
    @Override
    public void updateData() throws DataException
    {
        // init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        ArrayList<QuadTreeItem> matchingItems = new ArrayList<QuadTreeItem>(30);


        for (int i=0; i<matchingItems.size(); i++)
        {
            QuadTreeItem nextItem = matchingItems.get(i);
            
            if (canceled)
                return;
            
            if (nextItem.getData() == null)
            {
                subProvider.getSpatialExtent().setMinX(nextItem.getMinX());
                subProvider.getSpatialExtent().setMinY(nextItem.getMinY());
                subProvider.getSpatialExtent().setMaxX(nextItem.getMaxX());
                subProvider.getSpatialExtent().setMaxY(nextItem.getMaxY());
                subProvider.updateData();
                //System.out.println(nextItem);
                dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED), true);
            }
        }
    }
    
    
    @Override
    public void clearData()
    {
//        error = false;
//        
//        if (dataNode != null)
//        {
//            dataNode.clearAll();
//            dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CLEARED));
//        }       
    }
    
    
    @Override
    public DataNode getDataNode()
    {
        return super.getDataNode();
    }
    
    
    public boolean isSpatialSubsetSupported()
	{
        return subProvider.isSpatialSubsetSupported();
	}


	public boolean isTimeSubsetSupported()
	{
        return subProvider.isTimeSubsetSupported();
	}
}
