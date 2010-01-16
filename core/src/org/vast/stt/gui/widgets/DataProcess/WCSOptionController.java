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

package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.sensorML.process.WCS_Process;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;

/**
 * <p><b>Title:</b><br/>
 * WCSOptionController
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling WCS_Process options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date May 11, 2006
 * @version 1.0
 * 
 * TODO  Are width/length/depth params the user can modify?
 * TODO  Get combo options from caps for SRS, Format, and Version
 */

public class WCSOptionController extends OptionController
{
	private SMLProvider provider;
	private WCSOptionHelper optionHelper;
	
	public WCSOptionController(Composite parent, WCS_Process wcsProc, SMLProvider provider){
		this.provider = provider;
		optionHelper = new WCSOptionHelper(this, wcsProc, provider);
		buildBasicControls(parent);
	}

	//  TODO  possible add request type toggle and server
	public void buildBasicControls(Composite parent) {
		String [] formatOpts = new String[]{"SWE Common", "Geotiff", "Fix Me!"};
		String [] srsOpts = new String [] {};
		String [] versionOpts = new String [] {};
		int sx = optionHelper.getSkipX();
		int sy = optionHelper.getSkipY();
		int sz = optionHelper.getSkipZ();
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.COMBO, "Format:", formatOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "SRS:", srsOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "Version:", versionOpts),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Skip X:", "" + sx),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Skip Y:", "" + sy),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Skip Z:", "" + sz)	
		};
		optionControls = OptionControl.createControls(parent, params);
		//  Increase default size of Combo for formats
		((Combo)optionControls[0].getControl()).setTextLimit(25);
		addSelectionListener(optionHelper);
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
