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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.vast.stt.gui.dialogs.DataProviderJob;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.Project;
import org.vast.stt.project.ProjectReader;
import org.vast.stt.project.STTDisplay;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataItemIterator;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;


public class OpenProject implements Command
{
	private String url = null;
    private Project project = null;
    private boolean isUpdating = false;
    private boolean sceneLoading = false;
    
    private class LoadProjectRunnable implements  IRunnableWithProgress
	{
		ProjectReader reader;

		public LoadProjectRunnable(ProjectReader pr)
		{
			reader = pr;
		}
		
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			String msg = "Attempting to open project: " + url + "...";

			try 
			{
				monitor.beginTask(msg, IProgressMonitor.UNKNOWN);
				monitor.subTask("ReadingProject...");
				reader.readProject(url);
				while(isUpdating){
					try {
						Thread.sleep(40);
						if (monitor.isCanceled()) {
							isUpdating =  false;
						}
						if(reader.getProject() != null)
							isUpdating = false;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						System.err.println("LPR.run.IntrptdEx");
						e.printStackTrace();
					} finally {
						isUpdating = false;
					}
				}
				if(monitor.isCanceled()) {
					reader.cancelRead();
					throw new InterruptedException("Project Loading Cancelled");
				}
				monitor.subTask("Loading SceneTree...");
				Runnable openPage = new OpenPageRunnable(reader.getProject());
				PlatformUI.getWorkbench().getDisplay().asyncExec(openPage);
				sceneLoading = true;
				while(sceneLoading){
					try {
						Thread.sleep(40);
						//  Allowing OpenPageRunnable to cancel currently will cause some problems
						//  Needs work, just diable cancel for now.
						if (monitor.isCanceled()) {
							sceneLoading = false;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} finally {
				if(monitor.isCanceled()) {
					Project proj = reader.getProject();
					proj = null;
					throw new InterruptedException("Project Loading Cancelled");
				}				monitor.done();
				sceneLoading = false;
			}		  
		}
					
	}
		
	public void execute()
	{
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = Display.getDefault().getActiveShell();
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
		pmd.setCancelable(true);
		ProjectReader reader = new ProjectReader();
		LoadProjectRunnable lpr = new LoadProjectRunnable(reader);
		try {
			pmd.run(true,true, lpr);
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
	        MessageDialog.openInformation(shell, "Information", "User cancelled Project Loading");
	        e1.printStackTrace();
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

    
    public Project getProject()
    {
        return project;
    }
    
    public boolean isUpdating()
	{
    	return isUpdating;
	}
    
    public class OpenPageRunnable implements Runnable
    {
        private Project project;
        private boolean cancelled = false;
        
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
                    
                    // open a new page  a WorldScene
                    if (nextDisplay instanceof WorldScene)
                    {
                        WorldScene scene = (WorldScene)nextDisplay;
                        
                        // keep list of providers we did so we don't do them twice
                        List<DataProvider> processedProviders = new ArrayList<DataProvider>(20);
                        
                        // add job progress listener to all providers
                        DataItemIterator it = ((Scene)nextDisplay).getDataTree().getItemIterator();
                        while (it.hasNext())
                        {
                        	DataItem item = it.next();
                            DataProvider provider = item.getDataProvider();
                            if (!processedProviders.contains(provider))
                            {
                                if (provider.getSpatialExtent().getUpdater() == null)
                                    new DataProviderJob(provider);
                                processedProviders.add(provider);
                            }
                        }
//                        
                        // create scene page input and open new page
                        ScenePageInput pageInput = new ScenePageInput(scene);
                        IWorkbenchWindow oldWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        oldWindow.openPage("STT.Perspective", pageInput);
                        
                        // close empty page
                        if (oldWindow.getActivePage().getInput() == null)
                            oldWindow.close();
                    }
                }
            }
            catch (WorkbenchException e)
            {
                e.printStackTrace();
            }
            finally {
            	sceneLoading = false;
            }
        }
    };
    
   
    
}
