package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.process.WMS_Process;

/**
 * <p><b>Title:</b><br/>
 * WMSOptionController
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling WMS_Process options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 10, 2006
 * @version 1.0
 */

public class WMSOptionController extends OptionController
{
	private SMLProvider provider;
	private WMSOptionHelper optionHelper;
	
	public WMSOptionController(Composite parent, WMS_Process wmsProc, SMLProvider provider){
		this.provider = provider;
		optionHelper = new WMSOptionHelper(this, wmsProc, provider);
		buildBasicControls(parent);
	}
	
	public void buildBasicControls(Composite parent){
		int w = optionHelper.getInputImageWidth();
		int h = optionHelper.getInputImageHeight();
		String [] formatOpts = new String[]{};
//		if(caps != null)
//			formatOpts = caps.getFormatList().toArray(new String []{});
		String [] srsOpts = new String [] {};
//		if(caps != null)
//			srsOpts = caps.getFormatList().toArray(new String []{});
		String [] styleOpts = new String []{};
//		if(caps != null)
//			styleOpts = caps.getStyleList().toArray(new String []{});
		//  boolean trans = (query != null) ?  query.isTransparent() : true;
		//  boolean keepAspect = ...;
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Width:", w + ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Height:", h + ""),	
			new OptionParams(OptionControl.ControlType.COMBO, "Format:", formatOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "SRS:", srsOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "Style:", styleOpts),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Transparency:", false),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Maintain Aspect:", false)	
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
