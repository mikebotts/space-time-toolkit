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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.gui.widgets.OptionChooser;
import org.sensorML.process.FlatGridGenerator_Process;
import org.sensorML.process.WCS_Process;
import org.sensorML.process.WMS_Process;

/**
 * <p><b>Title:</b><br/>
 * DataProviderOptionChooser
 * </p>
 *
 * <p><b>Description:</b><br/>
 *
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jan 26, 2006
 * @version 1.0
 * 
 * TODO  add chooser/mapping widget when user selects + (add) style
 * TODO  support advanced options      
 */

public class DataProcessOptionChooser extends OptionChooser 
{
    SMLProvider provider;
	
	public DataProcessOptionChooser(Composite parent) {
		super(parent);
	}

	public void buildControls(Object processObj){
		removeOldControls();
		
		if(processObj instanceof WMS_Process) {
			new WMSOptionController(optComp, (WMS_Process)processObj, provider);
		} else if(processObj instanceof WCS_Process){
			new WCSOptionController(optComp, (WCS_Process)processObj, provider);
		} else if(processObj instanceof FlatGridGenerator_Process){
			//;new FlatGridGeneratorController(optComp, (FlatGridGenerator_Process)processObj, provider);
		} else
			System.err.println("OptionChooser:  Process type not supported yet: " + processObj);
		
		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	
	
	public void setProvider(SMLProvider prov){
		this.provider = prov;
	}
}
