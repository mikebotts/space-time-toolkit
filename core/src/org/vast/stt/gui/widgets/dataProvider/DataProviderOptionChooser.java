package org.vast.stt.gui.widgets.dataProvider;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.data.WMSProvider;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.process.WCS_Process;
import org.vast.stt.process.WMS_Process;

/**
 * <p><b>Title:</b><br/>
 * DataProviderOptionChooser
 * </p>
 *
 * <p><b>Description:</b><br/>
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 26, 2006
 * @version 1.0
 * 
 * TODO  add chooser/mapping widget when user selects + (add) style
 * TODO  support advanced options      
 */

public class DataProviderOptionChooser extends OptionChooser {
	
	public DataProviderOptionChooser(Composite parent) {
		super(parent);
	}

	public void buildControls(Object providerObj){
		removeOldControls();
		
		//  TODO distinguish between different types of SensorMLProviders
		//  TODO add support for SWEProvider...
		if(providerObj instanceof SensorMLProvider) {
			DataProcess process = ((SensorMLProvider)providerObj).getProcess();
			if(process instanceof ProcessChain){
				//  Not sure how to handle ProcessChain from GUI standpt.
				//  1)  Show each process GUI as its own tab 
				//  2)  Combine all options from all processes into single tab
				//  Probably go with (1)
				//  Hack for now to test WMS Options
				List procList = ((ProcessChain)process).getProcessList();
				for(int i=0; i<procList.size(); i++){
	    			DataProcess nextProcess = (DataProcess)procList.get(i);
	    			if(nextProcess instanceof WMS_Process) {
	    				process = nextProcess;
	    				break;
	    			}
				}
			}
			if(process instanceof WMS_Process) {
				WMSOptionController wmsOpts = 
					//new WMSOptionController(optComp, (SensorMLProvider)providerObj);
					new WMSOptionController(optComp, (WMS_Process)process);
			} else if(process instanceof WCS_Process){
				//  WCSOptionController...
			} else
				System.err.println("OptionChooser:  Process type not supported yet: " + process);
		} else
			System.err.println("OptionChooser:  Provider type not supported yet: " + providerObj);
		
		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	
	
}
