package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.process.FlatGridGenerator_Process;
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

public class DataProcessOptionChooser extends OptionChooser {
	
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
