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

package org.vast.stt.commands;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vast.stt.project.LoadProjectRunnable;
import org.vast.stt.project.OpenPageRunnable;
import org.vast.stt.project.Project;
import org.vast.stt.project.ProjectReader;


public class OpenProject implements Command
{
	private String url = null;
	private boolean isUpdating = false;
	
	public void execute()
	{
		Shell shell = Display.getDefault().getActiveShell();
		
		//  Two Progress Monitor Dialogs now used...

		//  ...First loads ProjectReader.project object from Project File
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
		pmd.setCancelable(true);
		ProjectReader reader = new ProjectReader();
		LoadProjectRunnable lpr = new LoadProjectRunnable(reader, url);
		try {
			pmd.run(true,true, lpr);
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			MessageDialog.openInformation(shell, "Information", "User cancelled Project Loading");
			return;
		}
		Project project = reader.getProject();
		if(project == null) {
			MessageDialog.openError(shell, "Error", "Unknown error reading project");
			return;
		}
		
		//  ...Second unloads project object into scene tree (populate tree, load enabled data items, etc)
		pmd = new ProgressMonitorDialog(shell);
		pmd.setCancelable(true);
		OpenPageRunnable opr = new OpenPageRunnable(project);
		try {
			pmd.run(true,true, opr);
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			MessageDialog.openInformation(shell, "Information", "User cancelled Project Loading");
			return;
		}
		System.gc();  //  Check me...
	}

	public void unexecute()
	{
	}


	public boolean isUndoAvailable()
	{
		return false;
	}


	public String getUrl()
	{
		return url;
	}


	public void setUrl(String url)
	{
		this.url = url;
	}

	public boolean isUpdating()
	{
		return isUpdating;
	}
}
