package org.vast.stt.project;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.vast.stt.gui.dialogs.DataProviderJob;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataItemIterator;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;

public class OpenPageRunnable implements IRunnableWithProgress
{
	private Project project;

	public OpenPageRunnable(Project project)
	{
		this.project = project;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException 
	{
		monitor.beginTask("Loading scene" , IProgressMonitor.UNKNOWN);
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
					while (it.hasNext() && !monitor.isCanceled())
					{
						DataItem item = it.next();
						monitor.subTask("Loading... " + item.getName());
						DataProvider provider = item.getDataProvider();
						if (!processedProviders.contains(provider))
						{
							if (provider.getSpatialExtent().getUpdater() == null)
								new DataProviderJob(provider);
							processedProviders.add(provider);
						}
						//  UPDATE PROG MON here- technically don't need this sleep call, but it enables DataItem 
						//  names to print as they are loaded, and looks neat.  It techically should add only a tiny 
						//  amount of time to the project load to leve it (For 100 items, it could add 1 second).
						//  	TC
						Thread.sleep(10);
					}
					//                        
					if(monitor.isCanceled()) {
						//  What state will this leave the sceneTree in?  We MAY need to clean it up
						//  Actually, testing with debugger, it is taken care of somewhere down the line
						//  but would not hurt to do it here, also - TC
//						if(scene != null)
//							scene.cleanup();
						throw new InterruptedException("Scene Loading Cancelled");
					}
					// create scene page input and open new page (must spawn in UI thread...)
					CreateNewScenePage scenePage = new CreateNewScenePage(scene);
					PlatformUI.getWorkbench().getDisplay().asyncExec(scenePage);
				}
			}
		}
		finally {
			monitor.done();
		}
	}

	private class CreateNewScenePage implements Runnable  
	{
		Scene scene;
		
		CreateNewScenePage(Scene scene){
			this.scene = scene;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ScenePageInput pageInput = new ScenePageInput(scene);
			IWorkbenchWindow oldWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			try {
				oldWindow.openPage("STT.Perspective", pageInput);
			} catch (WorkbenchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// close empty page
			if (oldWindow.getActivePage().getInput() == null)
				oldWindow.close();
		}
		
	}
	
}	

