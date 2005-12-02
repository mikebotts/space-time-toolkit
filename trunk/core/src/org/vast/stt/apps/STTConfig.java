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

package org.vast.stt.apps;

import org.vast.stt.event.STTEventManager;
import org.vast.stt.project.Project;


public class STTConfig
{
	private static STTConfig currentConfig;
	private Project currentProject;
	private STTEventManager eventManager;
	
	
	// singleton constructor
	private STTConfig()
	{
		currentProject = new Project();
		eventManager = new STTEventManager();
	}
	
	
	public static STTConfig getInstance()
	{
		if (currentConfig == null)
			currentConfig = new STTConfig();

		return currentConfig;
	}


	public Project getCurrentProject()
	{
		return currentProject;
	}


	public void setCurrentProject(Project currentProject)
	{
		this.currentProject = currentProject;
	}


	public STTEventManager getEventManager()
	{
		return eventManager;
	}
}
