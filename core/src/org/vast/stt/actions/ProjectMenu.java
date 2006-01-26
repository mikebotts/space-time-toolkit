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
import org.eclipse.ui.*;
import org.vast.stt.commands.*;
import org.vast.stt.gui.views.SceneView;


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
		Runnable readProject = new Runnable()
		{
			public void run()
			{
				OpenProject command = new OpenProject();
				command.setUrl("file:///D:/Projects/NSSTC/STT3/conf/SoCal.xml");
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