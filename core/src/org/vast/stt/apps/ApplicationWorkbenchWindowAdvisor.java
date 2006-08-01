
package org.vast.stt.apps;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.vast.stt.commands.OpenProject;
import org.vast.stt.gui.views.ExceptionPopup;
import org.vast.stt.gui.views.WorldView;


public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
	}


	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{
		return new ActionBarAdvisor(configurer);
	}


	public void preWindowOpen()
	{
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setTitle("Space Time Toolkit");
		
		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
	}
	
	public void postWindowOpen(){
		//  HACK to preload test proj
		String	url = "file:///C:/tcook/sttEclipse/stt3/conf/AL.xml";
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
		//thread.start();
	}

	Runnable openSceneView = new Runnable()
	{
		public void run()
		{
			try
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				page.showView(WorldView.ID, "000", IWorkbenchPage.VIEW_ACTIVATE);
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
			}
		}
	};
}
