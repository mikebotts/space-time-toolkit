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
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.sensorML.process.FlatGridGenerator_Process;

/**
 * <p><b>Title:</b><br/>
 * FlatGridOptionController
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling FlatGridGenerator_Process options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date May 13, 2006
 * @version 1.0
 * 
 */

public class FlatGridOptionController extends OptionController
{
	private SMLProvider provider;
	private FlatGridOptionHelper optionHelper;
	
	public FlatGridOptionController(Composite parent, FlatGridGenerator_Process fgProc, SMLProvider provider){
		this.provider = provider;
		optionHelper = new FlatGridOptionHelper(this, fgProc, provider);
		buildBasicControls(parent);
	}

	//  TODO  possible add request type toggle and server
	public void buildBasicControls(Composite parent) {
		int width = optionHelper.getWidth();
		int length = optionHelper.getLength();
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Width:", "" + width),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Length:", "" + length),	
		};
		optionControls = OptionControl.createControls(parent, params);
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
