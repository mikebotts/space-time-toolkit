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
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

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
 * <p>Copyright (c) 2007</p>
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