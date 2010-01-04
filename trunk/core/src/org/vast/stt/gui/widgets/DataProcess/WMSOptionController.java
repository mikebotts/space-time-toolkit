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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.sensorML.process.WMS_Process;
import org.vast.stt.provider.sml.SMLProvider;

/**
 * <p><b>Title:</b><br/>
 * WMSOptionController
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling WMS_Process options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date May 10, 2006
 * @version 1.0
 */

public class WMSOptionController extends OptionController implements ModifyListener
{
	private SMLProvider provider;
	WMS_Process wmsProcess;
	private WMSProcessOptions wmsOptions;
	
	public WMSOptionController(Composite parent, WMS_Process wmsProc, SMLProvider provider){
		this.provider = provider;
		this.wmsProcess = wmsProc;
		wmsOptions = new WMSProcessOptions(wmsProc);
		buildBasicControls(parent);
	}
	
	public void buildBasicControls(Composite parent){
		WMSLayerCapabilities caps = wmsProcess.getCapabilities(); 
		String [] formatOpts = caps.getFormatList().toArray(new String[]{});
		String [] srsOpts = caps.getSrsList().toArray(new String[]{});
		String [] styleOpts = caps.getStyleList().toArray(new String[]{});
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Width:", ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Height:", ""),	
			new OptionParams(OptionControl.ControlType.COMBO, "Format:", formatOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "SRS:", srsOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "Style:", styleOpts),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Transparency:", false),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Maintain Aspect:", false)	
		};
		optionControls = OptionControl.createControls(parent, params);
		loadFields();
		addSelectionListener(this);
		//  Need also to register key listeners on width and height text fields, since they
		//  don't generate selection events.
		Text wText = (Text)optionControls[0].getControl();
		wText.addModifyListener(this);
	}

	@Override
	//  load the initial state of the options based on what's currently in the provider
	public void loadFields() {
		Text wText = (Text)optionControls[0].getControl();
		wText.setText(wmsOptions.getInputImageWidth() + "");
		Text hText = (Text)optionControls[1].getControl();
		hText.setText(wmsOptions.getInputImageHeight() + "");
		Combo formatCombo = (Combo)optionControls[2].getControl();
		String format = wmsOptions.getFormat();
		//formatCombo.select(0); //  need lookup function (remarkably, Combo has no way to select based on String)
		//  OR
		formatCombo.setText(format);
		Combo srsCombo = (Combo)optionControls[3].getControl();
		String srs = wmsOptions.getSRS();
		srsCombo.setText(srs);  
		Combo styleCombo = (Combo)optionControls[4].getControl();
		//String style = wmsOptions.getStyle();
		//styleCombo.setText(style);
		//styleCombo.select(0);
		Button transBtn = (Button)optionControls[5].getControl();
		transBtn.setSelection(wmsOptions.getTransparency());
		Button aspectBtn = (Button)optionControls[6].getControl();
		aspectBtn.setSelection(wmsOptions.getMaintainAspect());
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void modifyText(ModifyEvent e) {
		Text text = (Text)e.getSource();
		String newStr = text.getText();
		int newVal = Integer.parseInt(newStr);
		if(text == optionControls[0].getControl()) { // width
			wmsOptions.setInputImageWidth(newVal);
		} else {  // height
			wmsOptions.setInputImageHeight(newVal);
		}
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		if (control == optionControls[2].getControl()) { //format
			String format = ((Combo)control).getText();
			wmsOptions.setFormat(format);
		} else if (control == optionControls[3].getControl()) { //SRS
			String srs = ((Combo)control).getText();
			wmsOptions.setSRS(srs);
		} else if (control == optionControls[4].getControl()) { //styles
			String style = ((Combo)control).getText();
			//  TODO  add Style supprt
			//setStyle(style);
		} else if (control == optionControls[5].getControl()) { //transparency
			boolean tr = ((Button)control).getSelection();
			wmsOptions.setTransparency(tr);
		}
	}
}
