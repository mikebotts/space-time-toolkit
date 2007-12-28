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
    Alexandre Robin <alexandre.robin@spotimage.fr>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.apps;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.views.IViewDescriptor;


/**
 * <p><b>Title:</b><br/>
 * ApplicationActionBarAdvisor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin <alexandre.robin@spotimage.fr>
 * @date 27 déc. 07
 * @version 1.0
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
	private IAction[] showViewActions;

	private class ShowViewAction extends Action
	{
		
		public ShowViewAction(String id, String label)
		{
			super(label);
			this.setId(id);
		}
		
		
		@Override
		public void run()
		{
			setChecked(true);
            try
            {
                String viewID = getId().substring(8);
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();                
                page.showView("STT." + viewID);
            }
            catch (PartInitException e)
            {
                e.printStackTrace();
            }
		}
	}


	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
	{
		super(configurer);
	}


	protected void makeActions(final IWorkbenchWindow window)
	{
		IViewDescriptor[] allViews = PlatformUI.getWorkbench().getViewRegistry().getViews();
		showViewActions = new IAction[allViews.length];

		for (int i = 0; i < allViews.length; i++)
		{
			String actionLabel = "Show " + allViews[i].getLabel() + " View";
			String actionId = "STT.Show" + allViews[i].getId().substring(4);
			IAction action = new ShowViewAction(actionId, actionLabel);
			showViewActions[i] = action;
			register(action);
		}
	}


	protected void fillMenuBar(IMenuManager menuBar)
	{
		MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
		
		for (int i = 0; i < showViewActions.length; i++)
		{
			// hack to avoid showing eclipse welcome view which does not work !!
			if (!showViewActions[i].getText().contains("Welcome"))
				windowMenu.add(showViewActions[i]);
		}
		
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(windowMenu);
	}
}
