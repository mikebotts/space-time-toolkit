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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.commands.OpenProject;


public class ProjectMenu implements IWorkbenchWindowActionDelegate
{
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
            if(path == null)
            	return;
            url = "file:///" + path.replace('\\', '/');

            final OpenProject openProjectCmd = new OpenProject();
            openProjectCmd.setUrl(url);
            openProjectCmd.execute();
            return;
        }
        
        if(actionId.endsWith("SaveProject")) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information", 
					"Save Project not available");
			return;
        }

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