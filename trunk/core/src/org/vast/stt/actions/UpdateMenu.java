
package org.vast.stt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.update.ui.UpdateManagerUI;


public class UpdateMenu implements IWorkbenchWindowActionDelegate, IPartListener
{
	private IWorkbenchWindow window;
	

	/**
	 * The constructor.
	 */
	public UpdateMenu()
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
        
        if (actionID.endsWith("STT.Update"))
        {
            BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {
                public void run()
                {
                    UpdateManagerUI.openInstaller(window.getShell());
                }
            });
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