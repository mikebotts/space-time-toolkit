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
import org.vast.stt.data.DataNode;
import org.vast.stt.project.DataProvider;
import org.vast.stt.project.DataStyler;
import org.vast.stt.util.SpatialExtent;


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
    protected String name;
    protected boolean enabled;
    protected DataProvider dataProvider;
    protected SpatialExtent bbox;
    protected DataNode dataNode;
    protected Hashtable<String, IndexerTreeBuilder> treeBuilders;
    protected ListInfo[] dataLists;
        
    
    public abstract void updateBoundingBox();
    
    
    public AbstractStyler()
    {
        treeBuilders = new Hashtable<String, IndexerTreeBuilder>();
        dataLists = new ListInfo[0];
    }
    
    
    public DataNode getDataNode()
    {
        return this.dataNode;
    }
    
        
    public void setDataProvider(DataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}


	public boolean isEnabled()
	{
		return enabled;
	}


	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;		
	}
    
    
    public SpatialExtent getBoundingBox()
    {
        return bbox;
    }


    public double[] getCenterPoint()
    {
        double[] centerPoint = new double[3];
        
        centerPoint[0] = (bbox.getMaxX() - bbox.getMinX()) / 2;
        centerPoint[1] = (bbox.getMaxY() - bbox.getMinY()) / 2;
        centerPoint[2] = (bbox.getMaxZ() - bbox.getMinZ()) / 2;
        
        return centerPoint;
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
    public void reset()
    {
        // reset all list iterators
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i];
            info.blockList.reset();
        }
    }
    
    
    /**
     * Load data for next block so that it is updated
     * for all array mappers. Also update block property mappers.
     */
    public boolean nextBlock()
    {
        for (int i = 0; i < dataLists.length; i++)
        {
            ListInfo info = dataLists[i]; 
            DataIndexer nextIndexer = info.blockIndexer;
                        
            if (!info.blockList.hasNext)
                return false;
            
            AbstractDataBlock nextBlock = info.blockList.next().data;
            
            // TODO implement block filtering here
            
            nextIndexer.reset();
            nextIndexer.setData(nextBlock);
        }           
                        
        return true;
    }
}


class ListInfo
{
    protected BlockList blockList;
    protected DataIndexer blockIndexer;
    
    
    public ListInfo(BlockList blockList, DataIndexer dataIndexer)
    {
        this.blockIndexer = dataIndexer;
        this.blockList = blockList;
    }
}