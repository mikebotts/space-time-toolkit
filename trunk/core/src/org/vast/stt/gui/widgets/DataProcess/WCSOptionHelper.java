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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.sensorML.process.WCS_Process;
import org.vast.cdm.common.DataComponent;
import org.vast.data.DataGroup;
import org.vast.stt.data.DataException;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.gui.widgets.OptionControl;

/**
 * <p><b>Title:</b><br/>
 * WCSOptionHelper
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for changing state of a WCS_Process object       
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date May 11, 2006
 * @version 1.0
 */

public class WCSOptionHelper  implements SelectionListener {
	WMSOptionController optionController;  // mainly needed to get controls handle later
    SMLProvider provider;
	WCS_Process wcsProcess;
	OptionControl [] controls;
	private DataComponent paramData;
	DataGroup wcsParams;
	
	public WCSOptionHelper(WCSOptionController oc, WCS_Process wcsProc, SMLProvider prov){
		wcsProcess = wcsProc;
		this.provider = prov;
        //  will grabbing this handle work?
		paramData = wcsProcess.getParameterList();
		wcsParams =(DataGroup)paramData.getComponent("wcsOptions");
	}
	
//	public int getWidth() {
//		
//		DataValue v = output.getComponent("width").getData();
//        int w = wcsParams.getComponent("width").getData().getIntValue();
//        return w;
//	}
//	
//	public int getLength(){
//        int l = wcsParams.getComponent("length").getData().getIntValue();
//        return l;
//	}
//	
	public String getVersion(){
        String version = wcsParams.getComponent("version").getData().getStringValue();
		return version;
	}
	
	public String getSRS(){
		//  TODO uncomment when SRS supported by process
        //String version = wcsParams.getComponent("srs").getData().getStringValue();
		return "EPSG:4326";
	}
	
	public String getFormat(){
        String format = wcsParams.getComponent("format").getData().getStringValue();
        return format;
	}
	
	public int getSkipX(){
        DataComponent component = wcsParams.getComponent("skipX");
        if(component == null)
        	return -1;  //  should make text disabled also
        int sx = component.getData().getIntValue();
        return sx;
	}
	
	public int getSkipY(){
        DataComponent component = wcsParams.getComponent("skipY");
        if(component == null)
        	return -1;  //  should make text disabled also
        int sy = component.getData().getIntValue();
        return sy;
	}
		
	public int getSkipZ(){
        DataComponent component = wcsParams.getComponent("skipZ");
        if(component == null)
        	return -1;  //  should make text disabled also
        int sz = component.getData().getIntValue();
        return sz;
	}
		
	public void setLength(int l){
        wcsParams.getComponent("length").getData().setIntValue(l);
	}        
	
	public void setWidth(int w){
        wcsParams.getComponent("width").getData().setIntValue(w);
	}
	
	public void setSRS(String SRS){
        wcsParams.getComponent("SRS").getData().setStringValue(SRS);
	}
	
	public void setFormat(String format){
        wcsParams.getComponent("format").getData().setStringValue(format);
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControl = optionController.getControls();

		if(control == optionControl[0].getControl()) {  // width 
			String ws = ((Text)control).getText();
			int w = Integer.parseInt(ws);
			setWidth(w);
		} else if (control == optionControl[1].getControl()) { //height
			String hs = ((Text)control).getText();
			int l = Integer.parseInt(hs);
			setLength(l);
		} else if (control == optionControl[2].getControl()) { //format
			String format = ((Combo)control).getText();
			setFormat(format);
		} else if (control == optionControl[3].getControl()) { //SRS
			String srs = ((Combo)control).getText();
			setSRS(srs);
		}
		try {
			//  TODO  add update button...
			provider.updateData();
		} catch (DataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
