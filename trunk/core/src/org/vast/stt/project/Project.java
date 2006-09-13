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
import org.vast.util.*;
import org.vast.process.*;


/**
 * <p><b>Title:</b><br/>
 * Space Time Toolkit Project
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current project state, providing access
 * to all project variables.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class Project
{
	protected String path;
	protected String name;
	protected String description;
	protected String author;
	protected DateTime date;

	protected ResourceList resourceList;
	protected ArrayList<Service> serviceList;	
	protected ArrayList<DataProcess> processList;
	protected ArrayList<STTDisplay> displayList;


    public Project()
    {
        displayList = new ArrayList<STTDisplay>();
    }
    
    
	public String getAuthor()
	{
		return author;
	}


	public void setAuthor(String author)
	{
		this.author = author;
	}


	public ResourceList getResourceList()
	{
		return resourceList;
	}


	public void setResourceList(ResourceList resourceList)
	{
		this.resourceList = resourceList;
	}


	public DateTime getDate()
	{
		return date;
	}


	public void setDate(DateTime date)
	{
		this.date = date;
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


	public String getPath()
	{
		return path;
	}


	public void setPath(String path)
	{
		this.path = path;
	}


	public ArrayList<DataProcess> getProcessList()
	{
		return processList;
	}


	public void setProcessList(ArrayList<DataProcess> processList)
	{
		this.processList = processList;
	}


	public ArrayList<STTDisplay> getDisplayList()
	{
		return displayList;
	}


	public void setDisplayList(ArrayList<STTDisplay> displayList)
	{
		this.displayList = displayList;
	}


	public ArrayList<Service> getServiceList()
	{
		return serviceList;
	}


	public void setServiceList(ArrayList<Service> serviceList)
	{
		this.serviceList = serviceList;
	}
}
