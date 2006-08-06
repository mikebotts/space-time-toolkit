
package org.vast.stt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.Scene;
import org.vast.stt.project.ViewSettings;


/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class ViewMenu implements IWorkbenchWindowActionDelegate
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
		if (action.getId().equals("STT.CloneScene1"))
        {
            ScenePageInput pageInput = (ScenePageInput)window.getActivePage().getInput();
            if (pageInput != null)
            {
                Scene currentScene = pageInput.getScene();
                Scene newScene = new Scene();
                newScene.setViewSettings(currentScene.getViewSettings());
                newScene.setTimeSettings(currentScene.getTimeSettings());
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
        else
            if (action.getId().equals("STT.CloneScene2"))
            {
                ScenePageInput pageInput = (ScenePageInput)window.getActivePage().getInput();
                if (pageInput != null)
                {
                    Scene currentScene = pageInput.getScene();
                    Scene newScene = new Scene();
                    newScene.setViewSettings(new ViewSettings());
                    newScene.setTimeSettings(currentScene.getTimeSettings());
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
	}
}