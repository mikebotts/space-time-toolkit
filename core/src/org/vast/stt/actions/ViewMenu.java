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

package org.vast.stt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.world.Projection_ECEF;
import org.vast.stt.project.world.Projection_LLA;
import org.vast.stt.project.world.Projection_Mercator;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.project.world.ViewSettings;


/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class ViewMenu implements IWorkbenchWindowActionDelegate, IPartListener
{
	private IWorkbenchWindow window;
	

	/**
	 * The constructor.
	 */
	public ViewMenu()
	{
	}


	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action)
	{
		String actionID = action.getId();
        ScenePageInput pageInput = (ScenePageInput)window.getActivePage().getInput();
        Scene currentScene = pageInput.getScene();
        
        if (actionID.endsWith("CloneScene1"))
        {
            
            if (pageInput != null)
            {
                WorldScene newScene = new WorldScene();
                newScene.setViewSettings(((WorldScene)currentScene).getViewSettings());
                newScene.setTimeExtent(((WorldScene)currentScene).getTimeExtent());
                newScene.setDataTree(currentScene.getDataTree());
                newScene.setName(currentScene.getName());
                
                pageInput = new ScenePageInput(newScene);
                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage("STT.Perspective", pageInput);
                }
                catch (WorkbenchException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (actionID.endsWith("CloneScene2"))
        {
            if (pageInput != null)
            {
                WorldScene newScene = new WorldScene();
                newScene.setViewSettings(new ViewSettings());
                newScene.setTimeExtent(((WorldScene)currentScene).getTimeExtent());
                newScene.setDataTree(currentScene.getDataTree());
                newScene.setName(currentScene.getName());
                
                pageInput = new ScenePageInput(newScene);
                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage("STT.Perspective", pageInput);
                }
                catch (WorkbenchException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (action.isChecked() && actionID.endsWith("ProjectECEF"))
        {
            if (pageInput != null)
            {
                ViewSettings viewSettings = ((WorldScene)currentScene).getViewSettings();
                viewSettings.setProjection(new Projection_ECEF());
                viewSettings.dispatchEvent(new STTEvent(viewSettings, EventType.SCENE_PROJECTION_CHANGED), false);
            }
        }
        else if (action.isChecked() && actionID.endsWith("ProjectLLA"))
        {
            if (pageInput != null)
            {
                ViewSettings viewSettings = ((WorldScene)currentScene).getViewSettings();
                viewSettings.setProjection(new Projection_LLA());
                viewSettings.dispatchEvent(new STTEvent(viewSettings, EventType.SCENE_PROJECTION_CHANGED), false);
            }
        }
        else if (action.isChecked() && actionID.endsWith("ProjectMERC"))
        {
            if (pageInput != null)
            {
                ViewSettings viewSettings = ((WorldScene)currentScene).getViewSettings();
                viewSettings.setProjection(new Projection_Mercator());
                viewSettings.dispatchEvent(new STTEvent(viewSettings, EventType.SCENE_PROJECTION_CHANGED), false);
            }
        }
        else if (actionID.startsWith("STT.Show"))
        {
            action.setChecked(true);
            try
            {
                String viewID = actionID.substring(8);
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();                
                page.showView("STT." + viewID);
            }
            catch (PartInitException e)
            {
                e.printStackTrace();
            }
        }
	}


	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}


	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose()
	{
	}


	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
        window.getActivePage().addPartListener(this);
	}


    public void partActivated(IWorkbenchPart part)
    {
        // TODO Auto-generated method stub
        
    }


    public void partBroughtToTop(IWorkbenchPart part)
    {
        // TODO Auto-generated method stub
        
    }


    public void partClosed(IWorkbenchPart part)
    {
        //window.getWorkbench().      
    }


    public void partDeactivated(IWorkbenchPart part)
    {
        // TODO Auto-generated method stub
        
    }


    public void partOpened(IWorkbenchPart part)
    {
        // TODO Auto-generated method stub
        
    }
}