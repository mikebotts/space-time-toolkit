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

package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;

public class BasicGridBorderController extends OptionController{
	private GridOptionHelper gridOptionHelper;
	
	public BasicGridBorderController(Composite parent, GridSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		
		gridOptionHelper = new GridOptionHelper(symbolizer);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.SPINNER, "Border Width:", new int[] {1, 10}),	
			//new OptionParams(OptionControl.ControlType.TEXT, "Som text:",	"blah"),
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Border Color:",	null),
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Border Opacity:", "1.0")	
		};
		optionControls = OptionControl.createControls(parent, params);
		loadFields();
		addSelectionListener(this);
	}
	
	// reset value of all controls to what is currently in symbolizer
	public void loadFields(){
//	  TODO check for mapping
		Spinner widthSpinner = (Spinner)optionControls[0].getControl();
		widthSpinner.setSelection((int)gridOptionHelper.getGridBorderWidth());
		optionControls[1].setColorLabelColor(gridOptionHelper.getGridBorderColor());
		//  TODO support mesh opacity
	}
	
	public void widgetDefaultSelected(SelectionEvent e){
	}
	
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		if(control == optionControls[0].getControl()) {
			Spinner widthSpinner = (Spinner)control;
			float w = new Float(widthSpinner.getSelection()).floatValue();
			gridOptionHelper.setGridBorderWidth(w);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		} else if (control == optionControls[1].getControl()) {
			ColorDialog colorChooser = 
				new ColorDialog(control.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			// TODO:  add alpha support
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			gridOptionHelper.setGridBorderColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		} else {
			//TODO:  support mesh opacity
		}
	}

}
