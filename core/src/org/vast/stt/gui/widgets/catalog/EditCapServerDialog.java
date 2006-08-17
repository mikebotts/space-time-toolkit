/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.gui.widgets.catalog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p><b>Title:</b>
 *  EditCapServerDialog
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Dialog for editting Cap Servers for this user
 * </p>
 *
 * <p>Copyright (c) 2006</p>
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

