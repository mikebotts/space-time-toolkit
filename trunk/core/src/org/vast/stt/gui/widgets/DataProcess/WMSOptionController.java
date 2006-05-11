package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
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
	//private WMSProvider provider;
	private SensorMLProvider provider;
	private WMSOptionHelper optionHelper;
	
//	public WMSOptionController(Composite parent, SensorMLProvider provider){
	public WMSOptionController(Composite parent, WMS_Process wmsProc, SensorMLProvider provider){
		this.provider = provider;
		optionHelper = new WMSOptionHelper(this, wmsProc, provider);
		buildBasicControls(parent);
	}

	//  TODO  possible add request type toggle and server
	public void buildBasicControls(Composite parent) {
		// TODO Auto-generated method stub
		optionControls = new OptionControl[7];
		//  Image Width
		optionControls[0] = new OptionControl(parent, 0x0);
		int w = optionHelper.getInputImageWidth();
		optionControls[0].createNumericText("Width:", w + "");

		//  Image Height
		optionControls[1] = new OptionControl(parent,0x0);
		int h = optionHelper.getInputImageWidth();
		optionControls[1].createNumericText("Height:", h + "");

		//  Formats -
		//  WHERE will these come from now?  WMSProcess is completely decoupled from Caps
		String [] formatOpts = new String[]{};
//		if(caps != null)
//			formatOpts = caps.getFormatList().toArray(new String []{});
		optionControls[2] = new OptionControl(parent,0x0);
		optionControls[2].createCombo("Format:", formatOpts);

		//  SRS  
		//
		String [] srsOpts = new String [] {};
//		if(caps != null)
//			srsOpts = caps.getFormatList().toArray(new String []{});
		optionControls[3] = new OptionControl(parent, 0x0);
		optionControls[3].createCombo("SRS:", srsOpts);

		//  Styles  
		//
		String [] styleOpts = new String []{};
//		if(caps != null)
//			styleOpts = caps.getStyleList().toArray(new String []{});
		optionControls[4] = new OptionControl(parent, 0x0);
		optionControls[4].createCombo("Styles:", styleOpts);
		
		// Transparent Checkbox
		//
		optionControls[5] = new OptionControl(parent, 0x0);
		//  Is transparency supported?  No way to tell currently
		//boolean trans = (query != null) ?  query.isTransparent() : true;
		optionControls[5].createCheckbox("Transparency:", false);

		//  Keep Aspect checkbox
		optionControls[6] = new OptionControl(parent, 0x0);
		optionControls[6].createCheckbox("Maintain Aspect:", false);
		
		addSelectionListener(optionHelper);
	}
}
