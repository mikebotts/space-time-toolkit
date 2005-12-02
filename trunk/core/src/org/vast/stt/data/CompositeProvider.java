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
import java.util.List;

import org.vast.stt.project.Resource;
import org.vast.stt.util.SpatialExtent;
import org.vast.stt.util.TimeExtent;


/**
 * <p><b>Title:</b><br/>
 * Composite Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This abstract Composite Provider class is the skeleton
 * for all composite providers. Composite Providers are
 * getting data from several child Data Providers and are
 * usually responsible for synchronization and merging. 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public abstract class CompositeProvider implements DataProvider
{
	protected List<DataProvider> childProviders;
	
	
	public abstract DataNode getDataNode();
	public abstract void setDataNode(DataNode dataNode);
	

	public CompositeProvider()
	{
		childProviders = new ArrayList<DataProvider>(2);
	}
	
	
	public void updateData() throws DataException
	{
		for (int i=0; i<childProviders.size(); i++)
			childProviders.get(i).updateData();
	}


	public boolean isUpdating()
	{
		for (int i=0; i<childProviders.size(); i++)
			if (childProviders.get(i).isUpdating())
				return true;
		
		return false;
	}


	public void cancelUpdate()
	{
		for (int i=0; i<childProviders.size(); i++)
			childProviders.get(i).cancelUpdate();
	}


	public void clearData()
	{
		for (int i=0; i<childProviders.size(); i++)
			childProviders.get(i).clearData();
	}


	public SpatialExtent getSpatialExtent()
	{
		return childProviders.get(0).getSpatialExtent();
	}


	public void setSpatialExtent(SpatialExtent spatialExtent)
	{
		for (int i=0; i<childProviders.size(); i++)
			childProviders.get(i).setSpatialExtent(spatialExtent);
	}


	public TimeExtent getTimeExtent()
	{
		return childProviders.get(0).getTimeExtent();
	}


	public void setTimeExtent(TimeExtent timeExtent)
	{
		for (int i=0; i<childProviders.size(); i++)
			childProviders.get(i).setTimeExtent(timeExtent);
	}
	
	
	public Resource getResource()
	{
		return null;
	}


	public void setResource(Resource resource)
	{
	}
}
