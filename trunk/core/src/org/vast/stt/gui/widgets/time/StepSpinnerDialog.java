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
 * <p>Copyright (c) 2006</p>
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
