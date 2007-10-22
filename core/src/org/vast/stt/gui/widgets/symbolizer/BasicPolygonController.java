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
import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.PolygonSymbolizer;
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
		    new OptionParams(OptionControl.ControlType.CHECKBOX, "Show Bounds",
		    		polygonOptionHelper.getShowBounds()),
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Bound Color", 
					polygonOptionHelper.getBoundColor()),
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Fill Polygon", 
					polygonOptionHelper.getFillPolygon()),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Fill Color", 
					polygonOptionHelper.getFillColor())
		};
		
		optionControls = OptionControl.createControls(parent, params);
		addSelectionListener(polygonOptionHelper);
	}

	@Override
	public void loadFields() {
		// TODO Auto-generated method stub
		
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
