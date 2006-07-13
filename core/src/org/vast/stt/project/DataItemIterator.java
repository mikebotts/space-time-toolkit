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

package org.vast.stt.project;

import java.util.Iterator;


/**
 * <p><b>Title:</b><br/>
 * Data Item Iterator
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Iterate through a tree list of DataItem/DataList.
 * This will return data items one by one descending
 * each branch of the tree sequentially.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 30, 2005
 * @version 1.0
 */
public class DataItemIterator implements Iterator<DataItem>
{
	protected DataItemIterator currentChild;
	protected int currentIndex;
	protected DataFolder dataList;


	public DataItemIterator(DataFolder dataList)
	{
		this.dataList = dataList;
	}


	public boolean hasNext()
	{
		int listSize = dataList.size();

		if (currentIndex < listSize - 1)
			return true;

		if (currentIndex == listSize)
			return false;

		if (currentIndex == listSize - 1)
			if (currentChild != null)
				return currentChild.hasNext();

		return true;
	}


	public DataItem next()
	{
		DataEntry nextEntry = dataList.get(currentIndex);
		DataItem nextItem = null;

		if (nextEntry instanceof DataFolder)
		{
			if (currentChild == null)
				currentChild = ((DataFolder) nextEntry).getItemIterator();

			if (currentChild.hasNext())
			{
				nextItem = currentChild.next();
			}
			else
			{
				currentIndex++;
				currentChild = null;
				nextItem = next();
			}
		}
		else
		{
			nextItem = (DataItem) nextEntry;
			currentIndex++;
		}

		return nextItem;
	}


	public void reset()
	{
		currentIndex = 0;
		currentChild = null;
	}


	public void remove()
	{
	}
}
