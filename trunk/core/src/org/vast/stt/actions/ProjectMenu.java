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
import org.vast.stt.commands.*;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.Project;
import org.vast.stt.project.STTDisplay;
import org.vast.stt.project.scene.Scene;


public class ProjectMenu implements IWorkbenchWindowActionDelegate
{
    class OpenPageRunnable implements Runnable
    {
        private Project project;
        
        public OpenPageRunnable(Project project)
        {
            this.project = project;
        }
        
        public void run()
        {
            try
            {
                ArrayList<STTDisplay> displayList = project.getDisplayList();
                for (int i=0; i<displayList.size(); i++)
                {
                    STTDisplay nextDisplay = displayList.get(i);
                    
                    if (nextDisplay instanceof Scene)
                    {
                        Scene scene = (Scene)nextDisplay;
                        ScenePageInput pageInput = new ScenePageInput(scene);
                        IWorkbenchWindow oldWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        oldWindow.openPage("STT.Perspective", pageInput);
                        if (oldWindow.getActivePage().getInput() == null)
                            oldWindow.close();
                    }
                }
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
            // TODO close project
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
                Runnable openPage = new OpenPageRunnable(command.getProject());
                PlatformUI.getWorkbench().getDisplay().asyncExec(openPage);
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