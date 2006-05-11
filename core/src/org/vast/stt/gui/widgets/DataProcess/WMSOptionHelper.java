package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.DataGroup;
import org.vast.stt.data.DataException;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.process.WMS_Process;

/**
 * <p><b>Title:</b><br/>
 * WMSOptionHelper
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for changing state of a WMS_Process object       
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 10, 2006
 * @version 1.0
 */

public class WMSOptionHelper  implements SelectionListener {
	WMSOptionController optionController;  // mainly needed to get controls handle later
	SensorMLProvider provider;
	WMS_Process wmsProcess;
	OptionControl [] controls;
	private DataComponent paramData;
	DataGroup wmsParams;
	
	public WMSOptionHelper(WMSOptionController oc, WMS_Process wmsProc, SensorMLProvider prov){
		wmsProcess = wmsProc;
		this.provider = prov;
        //  will grabbing this handle work?
		paramData = wmsProcess.getParameterList();
		wmsParams =(DataGroup)paramData.getComponent("wmsOptions");
	}
	
	public int getInputImageWidth() {
        int w = wmsParams.getComponent("imageWidth").getData().getIntValue();
        return w;
	}
	
	public int getInputImageHeight(){
        int h = wmsParams.getComponent("imageHeight").getData().getIntValue();
        return h;
	}
	
	public String getVersion(){
        String version = wmsParams.getComponent("version").getData().getStringValue();
		return version;
	}
	
	public String getSRS(){
		//  TODO uncomment when SRS supported by process
        //String version = wmsParams.getComponent("srs").getData().getStringValue();
		return "EPSG:4326";
	}
	
	public String getFormat(){
        String format = wmsParams.getComponent("format").getData().getStringValue();
        return format;
	}
	
	public boolean getTransparency(){
        boolean transparent = wmsParams.getComponent("imageTransparency").getData().getBooleanValue();
        return transparent;
	}
	
	public boolean getMaintainAspect(){
		return wmsProcess.getPreserveAspectRatio();
	}
	
	public void setInputImageHeight(int h){
        wmsParams.getComponent("imageWidth").getData().setIntValue(h);
	}        
	
	public void setInputImageWidth(int w){
        wmsParams.getComponent("imageWidth").getData().setIntValue(w);
	}
	
	public void setSRS(String SRS){
        wmsParams.getComponent("SRS").getData().setStringValue(SRS);
	}
	
	public void setFormat(String format){
        wmsParams.getComponent("format").getData().setStringValue(format);
	}
	
	public void setTransparency(boolean b){
        wmsParams.getComponent("imageTransparency").getData().setBooleanValue(b);
	}

	public void setMaintainAspect(boolean b){
		wmsProcess.setPreserveAspectRatio(b);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControl = optionController.getControls();

		if(control == optionControl[0].getControl()) {  // width 
			String ws = ((Text)control).getText();
			int w = Integer.parseInt(ws);
			setInputImageWidth(w);
		} else if (control == optionControl[1].getControl()) { //height
			String hs = ((Text)control).getText();
			int h = Integer.parseInt(hs);
			setInputImageHeight(h);
		} else if (control == optionControl[2].getControl()) { //format
			String format = ((Combo)control).getText();
			setFormat(format);
		} else if (control == optionControl[3].getControl()) { //SRS
			String srs = ((Combo)control).getText();
			setSRS(srs);
		} else if (control == optionControl[4].getControl()) { //styles
			String style = ((Combo)control).getText();
			//  TODO  add Style supprt
			//setStyle(style);
		} else if (control == optionControl[5].getControl()) { //transparency
			boolean tr = ((Button)control).getSelection();
			setTransparency(tr);
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
