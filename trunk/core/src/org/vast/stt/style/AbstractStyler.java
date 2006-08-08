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

import java.util.Hashtable;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataIndexer;
import org.vast.data.IndexerTreeBuilder;
import org.vast.stt.data.BlockInfo;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.data.BlockListIterator;
import org.vast.stt.data.DataNode;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataStyler;
import org.vast.stt.project.SpatialExtent;
import org.vast.stt.project.ViewSettings.Projection;


/**
 * <p><b>Title:</b><br/>
 * Abstract Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for all stylers.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public abstract class AbstractStyler implements DataStyler
{
    class ListInfo
    {
        protected BlockListIterator blockIterator;
        protected DataIndexer blockIndexer;
        
        
        public ListInfo(BlockList blockList, DataIndexer dataIndexer)
        {
            this.blockIndexer = dataIndexer;
            this.blockIterator = blockList.getIterator();
        }
    }
    
    
    protected DataItem dataItem;    
    protected DataNode dataNode;
    protected Hashtable<String, IndexerTreeBuilder> treeBuilders;
    protected ListInfo[] dataLists;
    protected Projection projection;
    protected boolean computeExtents;
    protected BlockInfo currentBlockInfo;
    
    
    public abstract void updateDataMappings();
    
    
    public AbstractStyler()
    {
        treeBuilders = new Hashtable<String, IndexerTreeBuilder>();
        dataLists = new ListInfo[0];
    }
    
    
    public DataItem getDataItem()
    {
        return dataItem;
    }
    
        
    public void setDataItem(DataItem dataItem)
	{
		this.dataItem = dataItem;
	}
    
    
    public void setProjection(Projection projection)
    {
        this.projection = projection;
    }
    
    
    public void addPropertyMapper(String componentPath, PropertyMapper newMapper)
    {
        // figure out which list it is
        String listName = componentPath.substring(0, componentPath.indexOf('/'));
        
        // retrieve previously created builder or create a new one
        IndexerTreeBuilder builder = treeBuilders.get(listName);
        if (builder == null)
        {
        	//  dataNode is null here when I am trying to add a new Styler - TC
            BlockList list = dataNode.getList(listName);
            if (list == null) return;
            builder = new IndexerTreeBuilder(list.getBlockStructure());
            treeBuilders.put(listName, builder);
            builder.addVisitor(componentPath, newMapper);
                       
            // resize indexer array
            ListInfo[] oldDataLists = dataLists;
            dataLists = new ListInfo[oldDataLists.length + 1];
            System.arraycopy(oldDataLists, 0, dataLists, 0, oldDataLists.length);
            dataLists[oldDataLists.length] = new ListInfo(list, builder.getRootIndexer());
        }
        else
        {
            builder.addVisitor(componentPath, newMapper);
        }
    }
    
       
    public void clearAllMappers()
    {
        dataLists = new ListInfo[0];
        treeBuilders.clear();
    }
    
    
    /**
     * Reset the block counter used for the block iterator
     */
    public void resetIterators()
    {
        // reset all list iterators
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i];
            info.blockIterator.reset();
        }
    }
    
    
    /**
     * Load data for next block so that it is updated
     * for all array mappers. Also update block property mappers.
     */
    public BlockListItem nextBlock()
    {
        BlockListItem nextItem = null;
        
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i]; 
            DataIndexer nextIndexer = info.blockIndexer;
                        
            if (!info.blockIterator.hasNext())
                return null;
            
            nextItem = info.blockIterator.next();
            AbstractDataBlock nextBlock = nextItem.getData();
            nextIndexer.reset();
            nextIndexer.setData(nextBlock);
            
            // get BlockInfo
            currentBlockInfo = nextItem.getInfo();
            if (currentBlockInfo.getSpatialExtent().isNull())
                computeExtents = true;
            else
                computeExtents = false;
        }           
                        
        return nextItem;
    }
    
    
    /**
     * Skips the specified number of blocks from the lists
     */
    public void skipBlocks(int blockCount)
    {
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i]; 
            
            int count = 0;
            while (info.blockIterator.hasNext() && count < blockCount)
            {
                info.blockIterator.next();
                count++;
            }
        }
    }
    
    
    public boolean hasMoreBlocks()
    {
        for (int i = 0; i < dataLists.length; i++)
        {
            if (!dataLists[i].blockIterator.hasNext())
                return false;
        }
        
        return true;
    }
    
    
    /**
     * Computes and return the bbox of this object in world coordinates
     */
    public SpatialExtent getBoundingBox()
    {
        SpatialExtent bbox = new SpatialExtent();
        
        // get a fresh iterator
        BlockListIterator iterator = dataLists[0].blockIterator.getList().getIterator();
        
        while (iterator.hasNext())
        {
            BlockInfo info = iterator.next().getInfo();
            if (info != null)
                bbox.add(info.getSpatialExtent());
        }
        
        return bbox;
    }
}