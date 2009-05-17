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
 * <p>Copyright (c) 2007</p>
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

	protected ArrayList<String> extensionList;
	protected ArrayList<Service> serviceList;  //  Not being used in current projects, all in resources?
	protected ArrayList<DataProcess> processList;
	protected ArrayList<STTDisplay> displayList;  //  So far, contains only one STTDispaly object 
	protected ResourceList resourceList; // loads resources okay, but currently after reading proj, this structure does 
										 // not reflect the loaded resources (nesting/recursion problem?)
	

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


	public ArrayList<String> getExtensionList()
	{
		return extensionList;
	}


	public void setExtensionList(ArrayList<String> extensionList)
	{
		this.extensionList = extensionList;
	}
}
