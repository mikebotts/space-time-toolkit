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

package org.vast.stt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.*;
import org.vast.stt.apps.STTConfig;
import org.vast.stt.commands.*;
import org.vast.stt.gui.views.SceneView;
import org.vast.stt.project.Project;
import org.vast.stt.project.ProjectReader;


public class ProjectMenu implements IWorkbenchWindowActionDelegate
{
	Runnable openSceneView = new Runnable()
	{
		public void run()
		{
			try
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				page.showView(SceneView.ID, "000", IWorkbenchPage.VIEW_ACTIVATE);
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
			}
		}
	};
	
	
	public ProjectMenu()
	{
	}


	public void run(IAction action)
	{
		String actionId = action.getId();
		String url = null;
		
		if(actionId.endsWith("OpenProject")){
			FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			String path = fileDialog.open();
			if(path != null){
				url = "file:" + path.replace('\\','/');
				ProjectReader reader = new ProjectReader();
				Project project = reader.readProject(url);
				STTConfig.getInstance().setCurrentProject(project);
			}
			
		} else if(actionId.endsWith("OpenTestProject")) {
			url = "file:///D:/Projects/NSSTC/STT3/conf/SoCal.xml";
		}
		final OpenProject command = new OpenProject();
		command.setUrl(url);
		Runnable readProject = new Runnable()
		{
			public void run()
			{
				command.execute();
				PlatformUI.getWorkbench().getDisplay().asyncExec(openSceneView);
			}
		};
		
		Thread thread = new Thread(readProject);
		thread.start();	
	}


	public void selectionChanged(IAction action, ISelection selection)
	{
	}


	public void dispose()
	{
	}


	public void init(IWorkbenchWindow window)
	{
	}
}