package org.vast.stt.gui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.vast.util.DefaultExceptionHandler;
import org.vast.util.ExceptionSystem;


/**
 * <p><b>Title:</b>
 * Exception Popup
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Pops up message boxes when exceptions are generated.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 1, 2006
 * @version 1.0
 */
public class ExceptionPopup extends DefaultExceptionHandler
{
    class MsgBoxRunnable implements Runnable
    {
        protected String message;
        
        
        public MsgBoxRunnable(String message)
        {
            this.message = message;
        }        
        
        
        public void run()
        {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            msg.setText("Error");
            msg.setMessage(message);
            msg.open();
        }
    };
    
    
    protected MsgBoxRunnable msgBoxExec;
    
    
    public ExceptionPopup()
    {
        ExceptionSystem.setDisplayHandler(this);
        ExceptionSystem.debug = false;
    }
    

    @ Override
    public void handleException(Throwable e, boolean debug)
    {
        Display display = PlatformUI.getWorkbench().getDisplay();
        String text = "";
        text = getErrorMessage(e, "\n", debug);
        msgBoxExec = new MsgBoxRunnable(text);
        display.asyncExec(msgBoxExec);
    }
}