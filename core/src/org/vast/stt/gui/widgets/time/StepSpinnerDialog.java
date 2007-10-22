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

package org.vast.stt.gui.widgets.time;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * <p><b>Title:</b><br/>
 * StepSpinnerDialog
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date May 18, 2006
 * @version 1.0
 */

public class StepSpinnerDialog extends Dialog {
	TimeSpinner stepSpinner;
	static final int DEFAULT_ID = -678;
	static final double DEFAULT_TIME_STEP = 60.0;
	private double timeStep = DEFAULT_TIME_STEP;
	
	public StepSpinnerDialog(Shell parent, double val){
		super(parent);
//		this.timeStep = 1e11;
		this.timeStep = val;
		this.open();
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, 0x0);
		//  made it 2 columns because I coudn't get the damned spinner to center otherwise...
		comp.setLayout(new GridLayout(2, true));
		//  step Spinner
		stepSpinner = new TimeSpinner(comp, "Step Time");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 2;
		stepSpinner.setLayoutData(gridData);
		
		stepSpinner.setValue(timeStep);
		stepSpinner.resetCaret();
		
		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		createButton(parent, DEFAULT_ID, "Default", false);
	}
	
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Adjust Step Time");
    }

    protected void buttonPressed(int buttonId) {
    	if(buttonId == IDialogConstants.OK_ID){
    		timeStep = stepSpinner.getValue();
    	} else if(buttonId == DEFAULT_ID){
    		timeStep = DEFAULT_TIME_STEP;
    		stepSpinner.setValue(timeStep);
    	}
    	super.buttonPressed(buttonId);
    }    
    
	public double getTimeStep() {
		return timeStep;
	}
}
