package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * <p><b>Title:</b><br/>
 * OptionControl
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Currently supports the following Control types:
 *  	Spinner, Button, Combo
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 23, 2006
 * @version 1.0
 * 
 * TODO  add support for other Stylers (Polygon, Raster) 
 */

public class OptionControl {
	Composite parent;
	Label label;
	Control control;
	
	public OptionControl(Composite parent){
		this.parent = parent;
	}

	// return Label so caller can modify layoutData, if desired
	private Label createLabel(String text){
		label = new Label(parent, 0x0);
		label.setText(text);
		// label.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GREEN));
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.widthHint = 65;
		label.setLayoutData(gd);
		
		return label;
	}
	
	/**
	 * creates a spinner with the specified label, min and max
	 * 
	 * @param labelTxt
	 * @param min -
	 *            note min must be > 0
	 * @param max
	 * @return the created Spinner
	 */
	public Spinner createSpinner(String labelTxt, int min, int max){
		createLabel(labelTxt);
		control = new Spinner(parent, 0x0);
		Spinner spinner = (Spinner)control;
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		GridData gd = new GridData(SWT.RIGHT, SWT.FILL, true,false);
		spinner.setLayoutData(gd);
		return spinner;
	}
	
	public Combo createCombo(String labelTxt, String [] opts){
		createLabel(labelTxt);
		control = new Combo(parent, 0x0);
		Combo combo = (Combo)control;
		combo.setItems(opts);
	
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		combo.setLayoutData(gd);
		return combo;
	}
	
	public Button createButton(String labelTxt, String text) { // sellistener
		createLabel(labelTxt);
		control = new Button(parent, SWT.PUSH);
		Button button = (Button)control;
		button.setText(text);

		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		button.setLayoutData(gd);
		return button;
	}

	public void dispose(){
		label.dispose();
		control.dispose();
		label = null;
		control = null;
	}
	
}

