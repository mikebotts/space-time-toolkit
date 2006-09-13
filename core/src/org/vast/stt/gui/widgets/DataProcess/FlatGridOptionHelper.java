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
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.process.FlatGridGenerator_Process;
import org.vast.stt.process.WCS_Process;
import org.vast.stt.process.WMS_Process;

/**
 * <p><b>Title:</b><br/>
 * FlatGridOptionHelper
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 13, 2006
 * @version 1.0
 */

public class FlatGridOptionHelper  implements SelectionListener {
	FlatGridOptionController optionController;  // mainly needed to get controls handle later
    SMLProvider provider;
	FlatGridGenerator_Process flatGridProcess;
	OptionControl [] controls;
	private DataComponent paramData;
	DataGroup flatGridParams;
	
	public FlatGridOptionHelper(FlatGridOptionController oc, FlatGridGenerator_Process fgProc,
            SMLProvider prov){
		this.flatGridProcess = fgProc;
		this.provider = prov;
        //  will grabbing this handle work?
		paramData = flatGridProcess.getParameterList();
		flatGridParams =(DataGroup)paramData.getComponent("wcsOptions");
	}
	
	public int getWidth() {
		int w = paramData.getComponent("gridWidth").getData().getIntValue();
        return w;
	}
	
	public int getLength(){
        int l = paramData.getComponent("gridLength").getData().getIntValue();
        return l;
	}
	
	private void setWidth(int w){
		
	}
	
	private void setLength(int l){
		
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControl = optionController.getControls();

		//  TODO...

		try {
			//  TODO  add update button...
			provider.updateData();
		} catch (DataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
