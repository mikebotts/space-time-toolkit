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

import java.util.*;


/**
 * <p><b>Title:</b><br/>
 * Data Set Group
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Group of Resrouces (can be use to group resources in folders)
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class ResourceList extends ArrayList<Resource> implements Resource
{
	static final long serialVersionUID = 0;
	protected String description;
	protected String name;

	
	public ResourceList()
	{
	}
	
	
	public ResourceList(int listSize)
	{
		super(listSize);
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
}
