package org.vast.stt.gui.widgets.dataProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.data.DataProvider;
import org.vast.stt.data.WMSProvider;
import org.vast.stt.gui.widgets.OptionChooser;

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
		
//		if(providerObj  instanceOf WMSProvider) {
				//WMSOptionController wmsOpts = new WMSOptionController(optComp, (WMSProvider)providerObj);
				WMSOptionController wmsOpts = new WMSOptionController(optComp, null);
//		} else if (styler instanceof LineStyler) {
//			//if(lineOpts == null)
//				LineOptionController lineOpts = new LineOptionController(optComp, (LineStyler)styler); 
//		} else
//			System.err.println("Styler not supported yet: " + styler);
		
		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	
	
}
