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

import java.util.ArrayList;
import org.vast.stt.data.tiling.QuadTree;
import org.vast.stt.data.tiling.QuadTreeItem;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.DataProvider;
import org.vast.stt.project.SpatialExtent;


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
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Feb 28, 2006
 * @version 1.0
 */
public class QuadTreeProvider extends AbstractProvider
{
    protected DataProvider subProvider;
    protected QuadTree quadTree;
    
    
    public QuadTreeProvider(DataProvider subProvider)
	{
        quadTree = new QuadTree();
        this.subProvider = subProvider;
        this.autoUpdate = true;
        
        subProvider.getSpatialExtent().removeAllListeners();
        subProvider.getTimeExtent().removeAllListeners();        
        this.setSpatialExtent(subProvider.getSpatialExtent());
        this.setTimeExtent(subProvider.getTimeExtent());
        subProvider.setSpatialExtent(new SpatialExtent());
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
        ArrayList<QuadTreeItem> unusedItems = new ArrayList<QuadTreeItem>(30);
        quadTree.findItems(matchingItems, unusedItems, spatialExtent, 100, 1);

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
                dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
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
        new MyBboxUpdater(spatialExtent);
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
