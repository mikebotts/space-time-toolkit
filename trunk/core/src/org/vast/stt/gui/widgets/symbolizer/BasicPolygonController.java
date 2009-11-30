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
import org.vast.ows.sld.Color;
import org.vast.ows.sld.PolygonSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Polygon Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Polygon controls for StyleWidget
 * 
 * NOTE:  Polygon symbolizer only supports Fill Color - to add bounds, the user
 * must use a separate Line Symbolizer currently.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicPolygonController extends OptionController 
{
	private PolygonOptionHelper polygonOptionHelper;
	
	
	public BasicPolygonController(Composite parent, PolygonSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		polygonOptionHelper = new PolygonOptionHelper(this);
		buildControls(parent);
	}

	public void buildControls(Composite parent){
		
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Fill Color", 
					polygonOptionHelper.getFillColor())
		};
		
		optionControls = OptionControl.createControls(parent, params);
		loadFields();
		addSelectionListener(this);	
	}

	@Override
	public void loadFields() {
		//  fillColor
		optionControls[0].setColorLabelColor(polygonOptionHelper.getFillColor());
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		//  TODO support showBounds and fillPoly
		if(control == optionControls[0].getControl()) {  //  fillColor
			ColorDialog colorChooser = 
				new ColorDialog(control.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			// TODO:  add alpha support
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[0].setColorLabelColor(sldColor); 
			polygonOptionHelper.setFillColor(sldColor);
			dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		}
	}
	
}
