package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;

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
	Composite optRow;
	Color colorLabelColor;
	Label colorLabel;
	
	public OptionControl(Composite parent){
		this.parent = parent;
		optRow = new Composite(parent, 0x0);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER,true, true);
		gd.minimumWidth = 140;
        gd.minimumHeight = 30;
		optRow.setLayoutData(gd);
	}

	// return Label so caller can modify layoutData, if desired
	private Label createLabel(String text){
		Label label = new Label(optRow, 0x0);
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
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		optRow.setLayout(layout);
		createLabel(labelTxt);
		Spinner spinner = new Spinner(optRow, 0x0);
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		GridData gd = new GridData(SWT.RIGHT, SWT.FILL, true,false);
		spinner.setLayoutData(gd);
		return spinner;
	}
	
	public Combo createCombo(String labelTxt, String [] opts){
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		optRow.setLayout(layout);
		createLabel(labelTxt);
		Combo combo = new Combo(optRow, 0x0);
		combo.setItems(opts);
	
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		combo.setLayoutData(gd);
		return combo;
	}
	
	public Button createButton(String labelTxt, String text) { // sellistener
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		optRow.setLayout(layout);
		createLabel(labelTxt);
		Button button = new Button(optRow, SWT.PUSH);
		button.setText(text);

		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		button.setLayoutData(gd);
		return button;
	}

	public Button createColorButton(String labelTxt, org.vast.ows.sld.Color sldColor) { 
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		optRow.setLayout(layout);
		createLabel(labelTxt);
		//  Add color label
		colorLabel = new Label(optRow, 0x0);
		colorLabel.setText("        ");
		colorLabelColor = new Color(PlatformUI.getWorkbench().getDisplay(), 
									(int)(sldColor.getRed()*255), 
									(int)(sldColor.getGreen()*255), 
									(int)(sldColor.getBlue()*255));
		colorLabel.setBackground(colorLabelColor);
		Button button = new Button(optRow, SWT.PUSH);
		button.setText("...");

		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		button.setLayoutData(gd);
		return button;
	}

	public void setColorLabelColor(org.vast.ows.sld.Color sldColor){
		if(colorLabelColor != null) 
			colorLabelColor.dispose();
		colorLabelColor = new Color(PlatformUI.getWorkbench().getDisplay(), 
				(int)(sldColor.getRed()*255), 
				(int)(sldColor.getGreen()*255), 
				(int)(sldColor.getBlue()*255));
		colorLabel.setBackground(colorLabelColor);		
	}
	
	public void dispose(){
		optRow.dispose();
		optRow = null;
		if(colorLabelColor != null) {
			colorLabelColor.dispose();
			colorLabelColor = null;
		}
	}
	
}

