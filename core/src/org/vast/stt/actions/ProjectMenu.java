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
import org.vast.stt.gui.views.SceneTreeView;
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
                IViewReference view = page.findViewReference(SceneView.ID, "000");
                if (view != null)
                {
                    SceneView sceneView = (SceneView)view.getView(false);
                    sceneView.reset();
                    
                    SceneTreeView sceneTree = (SceneTreeView)page.findView(SceneTreeView.ID);
                    if (sceneTree != null)
                        sceneTree.refresh();
                }
                else
                    page.showView(SceneView.ID, "000", IWorkbenchPage.VIEW_ACTIVATE);
            }
            catch (PartInitException e)
            {
                e.printStackTrace();
            }
        }
    };
    
    
    Runnable clearViews = new Runnable()
    {
        public void run()
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IViewReference view = page.findViewReference(SceneView.ID, "000");
            if (view != null)
            {
                SceneView sceneView = (SceneView)view.getView(false);
                sceneView.clear();
            }
            
            SceneTreeView sceneTree = (SceneTreeView)page.findView(SceneTreeView.ID);
            if (sceneTree != null)
                sceneTree.clear();
        }
    };


    public ProjectMenu()
    {
    }


    public void run(IAction action)
    {
        String actionId = action.getId();
        String url = null;

        
        // close project 
        if (actionId.endsWith("CloseProject"))
        {
            STTConfig.getInstance().setCurrentProject(null);
            clearViews.run();
            System.gc();
            return;
        }        
        
        // open file chooser window
        if (actionId.endsWith("OpenProject"))
        {
            FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
            String path = fileDialog.open();
            if (path != null)
                url = "file:///" + path.replace('\\', '/');
            else return;
        }
        else if (actionId.endsWith("OpenTestProject"))
        {
            url = "file:///D:/Projects/NSSTC/STT3/projects/SoCal.xml";
        }

        // launch OpenProject command in separate thread
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