package org.vast.stt.gui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * <p><b>Title:</b><br/>
 * OptionControl
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Currently supports the following Control types:
 *  	Spinner, Button, Combo, ColorButton, Text, Checkbox
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 23, 2006
 * @version 1.0
 */ 

public class OptionControl {
	Composite parent;
	Composite optRow;
	Color colorLabelColor;
	Label colorLabel;
	Display display = PlatformUI.getWorkbench().getDisplay();
	Control control;
	
	public OptionControl(Composite parent){
		this.parent = parent;
		optRow = new Composite(parent, 0x0);
		optRow.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		GridData gd = new GridData(SWT.FILL, SWT.CENTER,true, false);
		gd.minimumWidth = 120;
		gd.minimumHeight = 25;
		//gd.heightHint = 25;		
		optRow.setLayoutData(gd);
	}

	// return Label so caller can modify layoutData, if desired
	private Label createLabel(String text){
		Label label = new Label(optRow, 0x0);
		label.setText(text);
		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.widthHint = 60;
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
		layout.marginHeight = 0;
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
		layout.marginHeight = 0;
		optRow.setLayout(layout);
		createLabel(labelTxt);
		Combo combo = new Combo(optRow, SWT.READ_ONLY);
		combo.setItems(opts);
	
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		gd.widthHint = 40;
		combo.setLayoutData(gd);
		return combo;
	}
	
	public Button createButton(String labelTxt, String text) { // sellistener
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		optRow.setLayout(layout);
		createLabel(labelTxt);
		Button button = new Button(optRow, SWT.PUSH);
		button.setText(text);

		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		button.setLayoutData(gd);
		return button;
	}
	
	public Button createCheckbox(String labelTxt, boolean enabled){
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		optRow.setLayout(layout);
		createLabel(labelTxt);
		Button button = new Button(optRow, SWT.CHECK);

		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		button.setLayoutData(gd);
		return button;
	}
	
	public Text createText(String labelStr, String defaultText){
		GridLayout layout = new GridLayout(2, false);
		optRow.setLayout(layout);
		layout.marginHeight = 0;
		createLabel(labelStr);
		Text text = new Text(optRow, SWT.RIGHT);
		text.setText(defaultText);
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false,false);
		gd.widthHint = 45;
		text.setLayoutData(gd);
		//  Set limit to 7.  Callers can override this setting by using the return Text object
		text.setTextLimit(7);
		//  make bg gray to distinguish it from bg of parent
		text.setBackground(new Color(display, 210, 210, 210));
		return text;
	}

	public Button createColorButton(String labelTxt, org.vast.ows.sld.Color sldColor) { 
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 0;
		optRow.setLayout(layout);
		createLabel(labelTxt);
		//  Add color label
		colorLabel = new Label(optRow, 0x0);
		colorLabel.setText("       ");
		colorLabelColor = new Color(display, 
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
		colorLabelColor = new Color(display, 
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

