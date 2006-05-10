package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.process.WMS_Process;

public class WMSOptionHelper  implements SelectionListener {
	WMSOptionController optionController;  // mainly needed to get controls handle later
	SensorMLProvider provider;
	WMS_Process wmsProcess;
	OptionControl [] controls;
	
	//  Do we really need SensorMLProvider here?  Maybe for maxTime and SpatialExtent.
	//public WMSOptionHelper(WMSOptionController oc, SensorMLProvider provider){
	public WMSOptionHelper(WMSOptionController oc, WMS_Process wmsProc){
		//this.provider = provider; 
		wmsProcess = wmsProc;
	}
	
	//  Should I get these out of the Process directly, or out of Query object from the Process
	public int getInputImageWidth(){
		return -1;
	}
	
	public int getInputImageHeight(){
		return -1;
	}
	
	public String getSRS(){
		return null;
	}
	
	public String getFormat(){
		return null;
	}
	
	public boolean getTransparency(){
		return false;
	}
	
	public boolean getMaintainAspect(){
		return true;
	}
	
	public void setInputImageHeight(int h){
		
	}
	
	public void setInputImageWidth(int w){
		//  Also, should these be set in the Process directly or the query object?
	}
	
	public void setSRS(String SRS){
		
	}
	
	public void setFormat(String format){
		
	}
	
	public void setTransparency(boolean b){
		
	}

	public void setMaintainAspect(boolean b){
		
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControl = optionController.getControls();

		if(control == optionControl[0].getControl()) {  // width 
		} else if (control == optionControl[1].getControl()) { //height
		} else if (control == optionControl[1].getControl()) { //format
		} else if (control == optionControl[1].getControl()) { //SRS
		} else if (control == optionControl[1].getControl()) { //styles
		} else if (control == optionControl[1].getControl()) { //transparency
		}
	}
	
}
