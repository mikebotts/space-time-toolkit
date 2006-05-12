package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.process.WCS_Process;

/**
 * <p><b>Title:</b><br/>
 * WCSOptionController
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling WCS_Process options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 11, 2006
 * @version 1.0
 * 
 * TODO  Are width/length/depth params the user can modify?
 * TODO  Get combo options from caps for SRS, Format, and Version
 */

public class WCSOptionController extends OptionController
{
	private SensorMLProvider provider;
	private WCSOptionHelper optionHelper;
	
	public WCSOptionController(Composite parent, WCS_Process wcsProc, SensorMLProvider provider){
		this.provider = provider;
		optionHelper = new WCSOptionHelper(this, wcsProc, provider);
		buildBasicControls(parent);
	}

	//  TODO  possible add request type toggle and server
	public void buildBasicControls(Composite parent) {
		String [] formatOpts = new String[]{"SWE Common", "Geotiff", "Fix Me!"};
		String [] srsOpts = new String [] {};
		String [] versionOpts = new String [] {};
		int sx = optionHelper.getSkipX();
		int sy = optionHelper.getSkipY();
		int sz = optionHelper.getSkipZ();
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.COMBO, "Format:", formatOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "SRS:", srsOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "Version:", versionOpts),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Skip X:", "" + sx),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Skip Y:", "" + sy),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Skip Z:", "" + sz)	
		};
		optionControls = OptionControl.createControls(parent, params);
		//  Increase default size of Combo for formats
		((Combo)optionControls[0].getControl()).setTextLimit(25);
		addSelectionListener(optionHelper);
	}
}
