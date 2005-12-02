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

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.gui.views.SceneView;


public class OpenSceneView implements Command
{
	public void execute()
	{
		// create runnable to be able
		Runnable refreshView = new Runnable()
		{
			public void run()
			{
				try
				{
					// TODO also send events to update all views
					
					// TODO use an event instead of code below
					// Scene View should close itself when receiving the event
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					page.showView(SceneView.ID, "000", IWorkbenchPage.VIEW_ACTIVATE);
				}
				catch (PartInitException e)
				{
					e.printStackTrace();
				}
			}
		};
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(refreshView);		
	}

	public boolean isUndoAvailable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void unexecute()
	{
		// TODO Auto-generated method stub
		
	}
}
