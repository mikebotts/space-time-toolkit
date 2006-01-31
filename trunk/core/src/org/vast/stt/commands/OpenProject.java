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

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.apps.STTConfig;
import org.vast.stt.project.Project;
import org.vast.stt.project.ProjectReader;


public class OpenProject implements Command
{
	private String url = null;
	
	public void execute()
	{
		ProjectReader reader = new ProjectReader();
		Project project = reader.readProject(url);
		STTConfig.getInstance().setCurrentProject(project);
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
}
