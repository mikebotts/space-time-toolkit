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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 30, 2005
 * @version 1.0
 */
public class DataItemIterator implements Iterator<DataItem>
{
	protected DataItemIterator childIterator;
	protected int currentIndex;
	protected DataFolder dataFolder;


	public DataItemIterator(DataFolder dataFolder)
	{
		this.dataFolder = dataFolder;
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


	public DataItem next()
	{
		DataEntry nextEntry = dataFolder.get(currentIndex);
        DataItem nextItem = null;

		if (nextEntry instanceof DataFolder)
		{
			if (childIterator == null)
				childIterator = ((DataFolder) nextEntry).getItemIterator();

			if (childIterator.hasNext())
			{
                nextItem = childIterator.next();
			}
			else
			{
				currentIndex++;
				childIterator = null;
                nextItem = next();
			}
		}
		else if (nextEntry instanceof DataItem)
		{
            nextItem = (DataItem) nextEntry;
			currentIndex++;
		}
        else
        {
            currentIndex++;
            if (this.hasNext())
                nextItem = next();
        }

		return nextItem;
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
