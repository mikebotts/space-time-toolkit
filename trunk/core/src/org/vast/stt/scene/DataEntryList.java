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

package org.vast.stt.scene;

import java.util.ArrayList;


/**
 * <p><b>Title:</b><br/>
 * Data List
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a list of data items in a scene
 * DataList can be nested recursively, this is why the
 * DataEntry interface is used her
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 17, 2005
 * @version 1.0
 */
public class DataEntryList extends ArrayList<DataEntry> implements DataEntry
{
	static final long serialVersionUID = 0;
	protected String name;
	protected DataItemIterator iterator;
	
	
	public DataEntryList()
	{
		iterator = new DataItemIterator(this);
	}
	
	
	public DataEntryList(int listSize)
	{
		super(listSize);
		iterator = new DataItemIterator(this);
	}
	

	public String getName()
	{
		return name;
	}

	
	public void setName(String name)
	{
		this.name = name;
	}
	
	
	public boolean isEnabled()
	{
		return true;
	}


	public void setEnabled(boolean enabled)
	{
		iterator.reset();
		while (iterator.hasNext())
			iterator.next().setEnabled(enabled);
	}
	
	
	public DataItemIterator getItemIterator()
	{
		iterator.reset();
		return iterator;
	}
}
