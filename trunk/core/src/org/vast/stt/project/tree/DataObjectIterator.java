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

package org.vast.stt.project.tree;

import java.util.Iterator;


/**
 * <p><b>Title:</b><br/>
 * Data Item Iterator
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Iterate through a tree list of DataItem/DataFolder.
 * This will return data items one by one descending
 * each branch of the tree sequentially.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 30, 2005
 * @version 1.0
 */
public class DataObjectIterator implements Iterator<DataObject>
{
	protected DataObjectIterator childIterator;
	protected int currentIndex;
	protected DataFolder dataFolder;


	public DataObjectIterator(DataFolder dataList)
	{
		this.dataFolder = dataList;
	}


	public boolean hasNext()
	{
		int listSize = dataFolder.size();

		if (currentIndex < listSize - 1)
			return true;

		if (currentIndex == listSize)
			return false;

		if (currentIndex == listSize - 1)
			if (childIterator != null)
				return childIterator.hasNext();

		return true;
	}


	public DataObject next()
	{
		DataEntry nextEntry = dataFolder.get(currentIndex);
        DataObject nextObject = null;

		if (nextEntry instanceof DataFolder)
		{
			if (childIterator == null)
				childIterator = ((DataFolder) nextEntry).getItemIterator();

			if (childIterator.hasNext())
			{
                nextObject = childIterator.next();
			}
			else
			{
				currentIndex++;
				childIterator = null;
                nextObject = next();
			}
		}
		else
		{
            nextObject = (DataObject) nextEntry;
			currentIndex++;
		}

		return nextObject;
	}


	public void reset()
	{
		currentIndex = 0;
		childIterator = null;
	}


	public void remove()
	{
	}
}
