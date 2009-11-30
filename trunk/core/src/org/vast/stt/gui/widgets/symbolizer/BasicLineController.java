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
import org.vast.ows.sld.LineSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Line Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Line controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicLineController extends OptionController // implements SelectionListener
{
	private LineOptionHelper lineOptionHelper;
	
	public BasicLineController(Composite parent, LineSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		
		lineOptionHelper = new LineOptionHelper(symbolizer);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.SPINNER, "Line Width:", new int[] {1, 10}),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Line Color:",	null),
			new OptionParams(OptionControl.ControlType.SPINNER, "Line Opacity:", new int[] {0, 100}),
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Line Smooth:", new Boolean(true))
		};
		optionControls = OptionControl.createControls(parent, params);
		loadFields();
		addSelectionListener(this);
	}
	
	// reset value of all controls to what is currently in symbolizer
	public void loadFields(){
		// line width
		Spinner widthSpinner = (Spinner)optionControls[0].getControl();
		widthSpinner.setSelection((int)lineOptionHelper.getLineWidth());		
		// line color
		optionControls[1].setColorLabelColor(lineOptionHelper.getLineColor());		
		// line opacity
		Spinner opacitySpinner = (Spinner)optionControls[2].getControl();
		opacitySpinner.setSelection((int)(lineOptionHelper.getLineColor().getAlphaValue()*100f));
		// line smooth
		Button smoothCheckbox = (Button)optionControls[3].getControl();
		smoothCheckbox.setSelection(lineOptionHelper.getLineSmooth());
    }
	
	public void widgetDefaultSelected(SelectionEvent e){
	}
	
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		// line width
		if(control == optionControls[0].getControl()) {
			Spinner widthSpinner = (Spinner)control;
			float w = new Float(widthSpinner.getSelection()).floatValue();
			lineOptionHelper.setLineWidth(w);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		}
		
		// line color
		else if (control == optionControls[1].getControl()) {
			ColorDialog colorChooser = 
				new ColorDialog(control.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			lineOptionHelper.setLineColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		}
		
		// line opacity
		else if(control == optionControls[2].getControl()) {
			Spinner widthSpinner = (Spinner)control;
			float opacity = new Float(widthSpinner.getSelection()).floatValue();
			lineOptionHelper.getLineColor().setAlphaValue((float)opacity / 100.0f);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		}
		
		// line smooth
		else if(control == optionControls[3].getControl()) {
			Button smoothCheckbox = (Button)control;
			lineOptionHelper.setLineSmooth(smoothCheckbox.getSelection());
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		}
	}
	
}