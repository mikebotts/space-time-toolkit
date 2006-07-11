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

import java.io.IOException;
import java.io.InputStream;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataProvider;
import org.vast.stt.util.SpatialExtent;
import org.vast.stt.util.TimeExtent;


/**
 * <p><b>Title:</b><br/>
 * Abstract Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for data providers getting data from an 
 * input stream and using a cached DataNode.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public abstract class AbstractProvider implements DataProvider
{
    protected String name;
    protected String description;
    protected boolean updating = false;
	protected boolean canceled = false;
    protected boolean forceUpdate = true;
    protected DataItem dataItem;
	protected InputStream dataStream;
	protected DataNode cachedData;
	protected TimeExtent timeExtent = new TimeExtent();
	protected TimeExtent maxTimeExtent = new TimeExtent();
	protected SpatialExtent spatialExtent = new SpatialExtent();
	protected SpatialExtent maxSpatialExtent = new SpatialExtent();
    
    
	public abstract void updateData() throws DataException;
	
	
	public DataNode getDataNode()
	{
        if (!updating && forceUpdate)
        {
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    try
                    {
                        updating = true;
                        updateData();
                        updating = false;
                    }
                    catch (DataException e)
                    {
                        e.printStackTrace();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
            forceUpdate = false;
        }
		
		return cachedData;
	}
	
	
	public void setDataNode(DataNode dataNode)
	{
		cachedData = dataNode;		
	}
	
	
	public void clearData()
	{
		if (cachedData != null)
		{
			//TODO cachedData.removeAllComponents();
			//System.gc();
            forceUpdate = true;
		}		
	}
	
	
	public void cancelUpdate()
	{
		canceled = true;
		
		try
		{
			if (dataStream != null)
				dataStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
	
	
	public boolean isUpdating()
	{
		return updating;
	}


	public SpatialExtent getSpatialExtent()
	{
		return spatialExtent;
	}
	
	
	public void setSpatialExtent(SpatialExtent spatialExtent)
	{
		this.forceUpdate = true;
        this.spatialExtent = spatialExtent;		
	}


	public TimeExtent getTimeExtent()
	{
		return timeExtent;
	}


	public void setTimeExtent(TimeExtent timeExtent)
	{
        this.forceUpdate = true;
        this.timeExtent = timeExtent;		
	}


	public SpatialExtent getMaxSpatialExtent()
	{
		return maxSpatialExtent;
	}


	public void setMaxSpatialExtent(SpatialExtent maxSpatialExtent)
	{
		this.maxSpatialExtent = maxSpatialExtent;
	}


	public TimeExtent getMaxTimeExtent()
	{
		return maxTimeExtent;
	}


	public void setMaxTimeExtent(TimeExtent maxTimeExtent)
	{
		this.maxTimeExtent = maxTimeExtent;
	}


    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public DataItem getDataItem()
    {
        return dataItem;
    }


    public void setDataItem(DataItem dataItem)
    {
        this.dataItem = dataItem;
    }
}
