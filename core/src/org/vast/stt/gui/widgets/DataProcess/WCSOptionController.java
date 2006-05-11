package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.process.WCS_Process;
import org.vast.stt.process.WMS_Process;

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
		// TODO Auto-generated method stub
		optionControls = new OptionControl[6];

		//  ARE width and length settable params?
		//		//  Width
//		optionControls[0] = new OptionControl(parent, 0x0);
//		int w = optionHelper.getWidth();
//		Text widthText = optionControls[0].createText("Width:", w + "");
//		widthText.addKeyListener(this);
//
//		//  Length
//		optionControls[1] = new OptionControl(parent,0x0);
//		int l = optionHelper.getLength();
//		Text lengthText = optionControls[1].createText("Length:", l + "");
//		lengthText.addKeyListener(this);

		//  Formats -
		//  WHERE will these come from now?  WCSProcess is completely decoupled from Caps
		String [] formatOpts = new String[]{"SWE Common", "Geotiff", "Fix Me!"};
//		if(caps != null)
//			formatOpts = caps.getFormatList().toArray(new String []{});
		optionControls[0] = new OptionControl(parent,0x0);
		Combo formatCombo = optionControls[0].createCombo("Format:", formatOpts);
		formatCombo.setTextLimit(25);

		//  SRS  
		String [] srsOpts = new String [] {};
//		if(caps != null)
//			srsOpts = caps.getFormatList().toArray(new String []{});
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createCombo("SRS:", srsOpts);

		//  Version  
		//  TODO  get possible versions from somewhere
		String [] version = new String [] {};
//		if(caps != null)
//			srsOpts = caps.getFormatList().toArray(new String []{});
		optionControls[2] = new OptionControl(parent, 0x0);
		optionControls[2].createCombo("Version:", srsOpts);
		
		//  skipX
		optionControls[3] = new OptionControl(parent,0x0);
		int sx = optionHelper.getSkipX();
		optionControls[3].createNumericText("Skip X:", sx + "");
		
		//  skipY
		optionControls[4] = new OptionControl(parent,0x0);
		int sy = optionHelper.getSkipY();
		optionControls[4].createNumericText("Skip Y:", sy + "");

//		skipZ
		optionControls[5] = new OptionControl(parent,0x0);
		int sz = optionHelper.getSkipZ();
		optionControls[5].createNumericText("Skip Z:", sz + "");
	}
}
