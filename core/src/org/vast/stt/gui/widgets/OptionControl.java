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

package org.vast.stt.gui.widgets;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
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
 *  	Spinner, Button, Combo, ColorButton, Text, NumericText, Checkbox
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jan 23, 2006
 * @version 1.0
 */ 

public class OptionControl extends Composite implements KeyListener
{
	Color colorLabelColor;
	Label colorLabel;
	Label label;
	Display display = PlatformUI.getWorkbench().getDisplay();
	Control activeControl;
	public static enum ControlType 
		{ BUTTON, COLOR_BUTTON, SPINNER, COMBO, TEXT, NUMERIC_TEXT, CHECKBOX}; 
	ControlType controlType;
	final Color WHITE = display.getSystemColor(SWT.COLOR_WHITE);
	final Color GRAY = display.getSystemColor(SWT.COLOR_GRAY);

	public OptionControl(Composite parent, int styleBits){
		super(parent, styleBits);
		GridLayout layout = new GridLayout();
		this.setLayout(layout);
		
		setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = 100;
		//gd.minimumWidth = 120;
		gd.minimumHeight = 25;
		//gd.heightHint = 25;		
		setLayoutData(gd);
	}

	public static OptionControl createControl(Composite parent, OptionParams param){
		OptionControl control = new OptionControl(parent, 0x0);
		ControlType type = param.getType();
		String label = param.getLabel();
		Object data = param.getData();
		
		try {
			switch(type){
			case BUTTON:
				control.createButton(label, (String)data);
				break;
			case CHECKBOX:
				boolean enabled = ((Boolean)data).booleanValue();
				control.createCheckbox(label, enabled);
				break;
			case COLOR_BUTTON:
				control.createColorButton(label, (org.vast.ows.sld.Color)data);
				break;
			case TEXT:
				control.createText(label, (String)data);
				break;
			case NUMERIC_TEXT:
				control.createNumericText(label, (String)data);
				break;
			case SPINNER:
				int [] minMax = (int [])data;
				control.createSpinner(label, minMax[0], minMax[1]);
				break;
			case COMBO:
				control.createCombo(label, (String[])data);
				break;
			default:
				System.err.println("OptionControl.addSelListnr():  ControlType unrecognized");
			}
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return control;
	}
	
	public static OptionControl[] createControls(Composite parent, OptionParams[] params){
		int numParams = params.length;
		OptionControl [] controls = new OptionControl[numParams];
		for(int i=0; i<numParams; i++)
			controls[i] = OptionControl.createControl(parent, params[i]);
		
		return controls;
	}
	
	// return Label so caller can modify layoutData, if desired
	private Label createLabel(String text){
		label = new Label(this, 0x0);
		label.setText(text);
		label.setBackground(WHITE);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.minimumWidth = 60;
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
		this.setLayout(layout);
		createLabel(labelTxt);
		activeControl = new Spinner(this, 0x0);
		Spinner spinner = (Spinner)activeControl;
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		GridData gd = new GridData(SWT.RIGHT, SWT.FILL, true,false);
		spinner.setLayoutData(gd);
		controlType = ControlType.SPINNER;
		return spinner;
	}
	
	public Combo createCombo(String labelTxt, String [] opts){
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		this.setLayout(layout);
		createLabel(labelTxt);
		activeControl = new Combo(this, SWT.READ_ONLY);
		Combo combo = (Combo)activeControl;
		combo.setItems(opts);
	
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		gd.widthHint = 60;
		combo.setLayoutData(gd);
		controlType = ControlType.COMBO;
		return combo;
	}
	
	public Button createButton(String labelTxt, String text) { 
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		this.setLayout(layout);
		createLabel(labelTxt);
		activeControl = new Button(this, SWT.PUSH);
		Button button = (Button)activeControl;
		button.setText(text);

		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		button.setLayoutData(gd);
		controlType = ControlType.BUTTON;
		return button;
	}
	
	public Button createCheckbox(String labelTxt, boolean enabled){
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		this.setLayout(layout);
		createLabel(labelTxt);
		activeControl = new Button(this, SWT.CHECK);

		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		Button button = (Button)activeControl;
		button.setLayoutData(gd);
		controlType = ControlType.BUTTON;
		return button;
	}

	//  Experimenting with Jface Editors (it's not showing up, for some reason)
	public TextCellEditor createTextCellEditor(String labelStr, String defText){
		TextCellEditor tce = new TextCellEditor(this, SWT.RIGHT);
		tce.setValue(defText);
		Text text = (Text)tce.getControl();
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false,false);
		gd.widthHint = 45;
		text.setLayoutData(gd);
		//  Set limit to 7.  Callers can override this setting by using the return Text object
		text.setTextLimit(7);
		//  make bg gray to distinguish it from bg of parent
		text.setBackground(new Color(display, 210, 210, 210));
		controlType = ControlType.TEXT;
		return tce;
	}
	
	public Text createText(String labelStr, String defaultText){
		GridLayout layout = new GridLayout(2, false);
		this.setLayout(layout);
		layout.marginHeight = 0;
		createLabel(labelStr);
		activeControl = new Text(this, SWT.RIGHT);
		Text text = (Text)activeControl;
		text.setText(defaultText);
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false,false);
		gd.widthHint = 45;
		text.setLayoutData(gd);
		//  Set limit to 7.  Callers can override this setting by using the return Text object
		text.setTextLimit(7);
		//  make bg gray to distinguish it from bg of parent
		text.setBackground(new Color(display, 210, 210, 210));
		controlType = ControlType.TEXT;
		return text;
	}

	public Text createNumericText(String labelStr, String defaultText){
		Text numText = createText(labelStr, defaultText);
		numText.addKeyListener(this);
		controlType = ControlType.NUMERIC_TEXT;
		return numText;
	}
	
	public Button createColorButton(String labelTxt, org.vast.ows.sld.Color sldColor) { 
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 0;
		this.setLayout(layout);
		createLabel(labelTxt);
		//  Add color label
		colorLabel = new Label(this, 0x0);
		colorLabel.setText("       ");
		if(sldColor!=null)
			setColorLabelColor(sldColor);
		activeControl = new Button(this, SWT.PUSH);
		Button button = (Button)activeControl;
		button.setText("...");
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
		button.setLayoutData(gd);
		controlType = ControlType.COLOR_BUTTON;
		return button;
	}

	public void setColorLabelColor(org.vast.ows.sld.Color sldColor){
		if(colorLabelColor != null) 
			colorLabelColor.dispose();
		if(sldColor.isConstant())
			colorLabelColor = new Color(display, 
					(int)(sldColor.getRedValue()*255), 
					(int)(sldColor.getGreenValue()*255), 
					(int)(sldColor.getBlueValue()*255));
		else  //  at least one of rgba channels is mapped
			colorLabelColor = new Color(display, 100,100,100);
		colorLabel.setBackground(colorLabelColor);		
	}
	
	public Control getControl(){
		return activeControl;
	}
	
	public void setEnabled(boolean b){
		if(b) {
			this.setBackground(WHITE);
			this.label.setBackground(WHITE);
			this.getControl().setEnabled(true);
		} else {
			this.setBackground(GRAY);
			this.label.setBackground(GRAY);
			this.getControl().setEnabled(false);
		}
	}
	
	public void addSelectionListener(SelectionListener sl){
		switch(controlType){
		case BUTTON:
		case CHECKBOX:
		case COLOR_BUTTON:
			((Button)activeControl).addSelectionListener(sl);
			break;
		case TEXT:
		case NUMERIC_TEXT:
			((Text)activeControl).addSelectionListener(sl);
			break;
		case SPINNER:
			((Spinner)activeControl).addSelectionListener(sl);
			break;
		case COMBO:
			((Combo)activeControl).addSelectionListener(sl);
			break;
		default:
			System.err.println("OptionControl.addSelListnr():  ControlType unrecognized");
		}
	}
	
	public void removeSelectionListener(SelectionListener sl){
		switch(controlType){
		case BUTTON:
		case CHECKBOX:
		case COLOR_BUTTON:
			((Button)activeControl).removeSelectionListener(sl);
			break;
		case NUMERIC_TEXT:
		case TEXT:
			((Text)activeControl).removeSelectionListener(sl);
			break;
		case SPINNER:
			((Spinner)activeControl).removeSelectionListener(sl);
			break;
		case COMBO:
			((Combo)activeControl).removeSelectionListener(sl);
			break;
		default:
			System.err.println("OptionControl.addSelListnr():  ControlType unrecognized");
		}
	}
	
	//  sets this optControl's selection based on the arg
	public void setSelection(OptionControl optControl){
		Control control = optControl.getControl();
		switch(controlType){
		case BUTTON:
		case CHECKBOX:
			//((Button)activeControl).removeSelectionListener(sl);
			break;
		case COLOR_BUTTON:
			colorLabelColor = optControl.colorLabelColor;
			colorLabel.setBackground(colorLabelColor);
			break;
		case TEXT:
		case NUMERIC_TEXT:
			//((Text)activeControl).removeSelectionListener(sl);
			break;
		case SPINNER:
			((Spinner)activeControl).setSelection( ((Spinner)control).getSelection());
			break;
		case COMBO:
			//((Combo)activeControl).removeSelectionListener(sl);
			break;
		default:
			System.err.println("OptionControl.addSelListnr():  ControlType unrecognized");
		}
	}

	public void keyPressed(KeyEvent e) {
		//  accept only numbers and decimal point
		e.doit = ( (e.keyCode >=48 && e.keyCode <= 57) || e.keyCode == 46 );
		//	and bkspace/delete/ENTER/LFT/RT ARROW/
		e.doit = e.doit || e.keyCode == 8 || e.keyCode == 127 || e.keyCode == 13 ||
				 e.keyCode == 16777219 || e.keyCode == 16777220;
	}

	public void keyReleased(KeyEvent e) {
	}
	
	public void dispose(){
		if(colorLabelColor != null) {
			colorLabelColor.dispose();
			colorLabelColor = null;
		}
		super.dispose();
	}
	
}

