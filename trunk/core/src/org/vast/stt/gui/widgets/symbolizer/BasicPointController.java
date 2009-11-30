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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Point Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Point controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicPointController extends OptionController  
{
	private PointOptionHelper pointOptionHelper;
	
	public BasicPointController(Composite parent, PointSymbolizer symbolizer){
		this.symbolizer = symbolizer;

		pointOptionHelper = new PointOptionHelper(this);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){
		//  get current size, color from styler
		Color c = pointOptionHelper.getPointColor();
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.SPINNER, "Point Size:", new int[] {1, 10}),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Point Color:", c)
		};
		optionControls = OptionControl.createControls(parent, params);
		Spinner sizeSpinner = (Spinner)optionControls[0].getControl();
		sizeSpinner.setSelection((int)pointOptionHelper.getPointSize());
		addSelectionListener(this);
	}

	// reset value of all controls to what is currently in symbolizer
	public void loadFields(){
	}
	
	public void widgetDefaultSelected(SelectionEvent e){
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		if(control == optionControls[0].getControl()) {
			Spinner sizeSpinner = (Spinner)control;
			float size = (float)sizeSpinner.getSelection();
			pointOptionHelper.setPointSize(size);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		} else if(control == optionControls[1].getControl()) {
			Button colorButton = (Button)control;
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			pointOptionHelper.setPointColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		}
	}

}
