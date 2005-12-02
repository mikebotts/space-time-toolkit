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
import org.vast.stt.project.Resource;
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
	protected Resource resource;
	protected boolean updating = false;
	protected boolean canceled = false;
	protected InputStream dataStream;
	protected DataNode cachedData;
	protected TimeExtent timeExtent;
	protected SpatialExtent spatialExtent;
	
	
	public abstract void updateData() throws DataException;
	
	
	public DataNode getDataNode()
	{
		try
		{
			if (cachedData == null)
				updateData();
		}
		catch (DataException e)
		{
			e.printStackTrace();
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
			cachedData.removeAllComponents();
			//System.gc();
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
	
	
	public Resource getResource()
	{
		return resource;
	}


	public void setResource(Resource resource)
	{
		this.resource = resource;
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
		this.spatialExtent = spatialExtent;		
	}


	public TimeExtent getTimeExtent()
	{
		return timeExtent;
	}


	public void setTimeExtent(TimeExtent timeExtent)
	{
		this.timeExtent = timeExtent;		
	}
}
