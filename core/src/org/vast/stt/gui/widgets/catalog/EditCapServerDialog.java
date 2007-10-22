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

package org.vast.stt.gui.widgets.catalog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * <p><b>Title:</b>
 *  EditCapServerDialog
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Dialog for editting Cap Servers for this user
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class EditCapServerDialog extends Dialog
{
	private CapServerTree capServerTree;
	private final static int EDIT = 0, ADD = 1, REMOVE = 2, CLOSE = 3;
	CapServers capServers;
	
	public EditCapServerDialog(Shell parent, CapServers capServers){
		super(parent);
		this.capServers = capServers;
		this.setShellStyle(SWT.APPLICATION_MODAL|SWT.SHELL_TRIM);
		this.open();
	}
	
	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Edit Capabilities Servers");
        shell.setMinimumSize(280,200);
    }

	protected Control createDialogArea(Composite parent) {
		//Composite dialogArea = (Composite)super.createDialogArea(parent);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		capServerTree = new CapServerTree(parent, capServers);
		capServerTree.getControl().setLayoutData(gd);
		
		return capServerTree.getControl();
	}

	protected void createButtonsForButtonBar(Composite parent){
	    createButton(parent, ADD, "Add", true);
	    createButton(parent, REMOVE, "Remove", true);
	    createButton(parent, EDIT, "Edit", true);
	    createButton(parent, CLOSE, "Close", true);
	}
	
	protected void buttonPressed(int id){
		switch(id){
		case ADD:
			System.err.println("Add ");
			break;
		case REMOVE:
			System.err.println("Remove ");
			break;
		case EDIT:
			System.err.println("Edit ");
			break;
		case CLOSE:
			super.close();
			break;
		}
		
	}
}

