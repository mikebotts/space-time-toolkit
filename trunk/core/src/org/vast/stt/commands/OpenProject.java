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

package org.vast.stt.commands;

import org.vast.stt.project.Project;
import org.vast.stt.project.ProjectReader;


public class OpenProject implements Command
{
	private String url = null;
    private Project project = null;
    
	
	public void execute()
	{
		ProjectReader reader = new ProjectReader();
		project = reader.readProject(url);
        System.gc();
	}
    

	public void unexecute()
	{
	}


	public boolean isUndoAvailable()
	{
		return false;
	}


	public String getUrl()
	{
		return url;
	}


	public void setUrl(String url)
	{
		this.url = url;
	}

    
    public Project getProject()
    {
        return project;
    }
}
