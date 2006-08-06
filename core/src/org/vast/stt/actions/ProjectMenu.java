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

import java.util.ArrayList;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.*;
import org.vast.stt.apps.STTConfig;
import org.vast.stt.commands.*;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.Scene;


public class ProjectMenu implements IWorkbenchWindowActionDelegate
{
    Runnable refreshWorkPage = new Runnable()
    {
        public void run()
        {
            try
            {
                ArrayList<Scene> sceneList = STTConfig.getInstance().getCurrentProject().getSceneList();
                for (int i=0; i<sceneList.size(); i++)
                {
                    Scene newScene = sceneList.get(i);
                    ScenePageInput pageInput = new ScenePageInput(newScene);
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage("STT.Perspective", pageInput);
                }
                                
                
                // IWorkbenchWindow window = PlatformUI.getWorkbench().openWorkbenchWindow("STT.Perspective", null);
                //IWorkbenchPage page = window.getActivePage();
//                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//                IViewReference view = page.findViewReference(WorldView.ID, "000");
//                if (view != null)
//                {
//                    WorldView sceneView = (WorldView)view.getView(false);
//                    sceneView.reset();
//                    
//                    SceneTreeView sceneTree = (SceneTreeView)page.findView(SceneTreeView.ID);
//                    if (sceneTree != null)
//                        sceneTree.refresh();
//                }
//                else
//                    page.showView(WorldView.ID, "000", IWorkbenchPage.VIEW_ACTIVATE);
            }
            catch (WorkbenchException e)
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

        
        // close project 
        if (actionId.endsWith("CloseProject"))
        {
            STTConfig.getInstance().setCurrentProject(null);
            refreshWorkPage.run();
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
                PlatformUI.getWorkbench().getDisplay().asyncExec(refreshWorkPage);
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