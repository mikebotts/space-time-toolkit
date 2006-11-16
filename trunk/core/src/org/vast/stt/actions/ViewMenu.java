
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
import org.vast.stt.commands.FitView;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.world.Projection_ECEF;
import org.vast.stt.project.world.Projection_LLA;
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
        
        if (actionID.endsWith("CloneScene1"))
        {
            ScenePageInput pageInput = (ScenePageInput)window.getActivePage().getInput();
            if (pageInput != null)
            {
                WorldScene currentScene = pageInput.getScene();
                WorldScene newScene = new WorldScene();
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
        else if (actionID.endsWith("CloneScene2"))
        {
            ScenePageInput pageInput = (ScenePageInput)window.getActivePage().getInput();
            if (pageInput != null)
            {
                WorldScene currentScene = pageInput.getScene();
                WorldScene newScene = new WorldScene();
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
        else if (actionID.endsWith("ProjectECEF"))
        {
            ScenePageInput pageInput = (ScenePageInput)window.getActivePage().getInput();
            if (pageInput != null)
            {
                WorldScene currentScene = pageInput.getScene();
                ViewSettings viewSettings = currentScene.getViewSettings();
                viewSettings.setProjection(new Projection_ECEF());
                viewSettings.dispatchEvent(new STTEvent(viewSettings, EventType.SCENE_PROJECTION_CHANGED));
                currentScene.getRenderer().drawScene(currentScene);
                FitView fit = new FitView(currentScene);
                fit.execute();
            }
        }
        else if (actionID.endsWith("ProjectLLA"))
        {
            ScenePageInput pageInput = (ScenePageInput)window.getActivePage().getInput();
            if (pageInput != null)
            {
                WorldScene currentScene = pageInput.getScene();
                ViewSettings viewSettings = currentScene.getViewSettings();
                viewSettings.setProjection(new Projection_LLA());
                viewSettings.dispatchEvent(new STTEvent(viewSettings, EventType.SCENE_PROJECTION_CHANGED));
                currentScene.getRenderer().drawScene(currentScene);
                FitView fit = new FitView(currentScene);
                fit.execute();
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