package org.vast.stt.gui.widgets.dataProvider;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.process.WMS_Process;

public class WMSOptionController extends OptionController
	implements KeyListener 
{
	//private WMSProvider provider;
	private SensorMLProvider provider;
	private WMSOptionHelper optionHelper;
	
//	public WMSOptionController(Composite parent, SensorMLProvider provider){
	public WMSOptionController(Composite parent, WMS_Process wmsProc){
		//this.provider = provider;
		optionHelper = new WMSOptionHelper(this, wmsProc);
		buildBasicControls(parent);
	}

	public void buildBasicControls(Composite parent) {
		// TODO Auto-generated method stub
		optionControls = new OptionControl[7];
		//  Image Width
		optionControls[0] = new OptionControl(parent, 0x0);
		int w = optionHelper.getInputImageWidth();
		Text widthText = optionControls[0].createText("Width:", w + "");
		widthText.addKeyListener(this);

		//  Image Height
		optionControls[1] = new OptionControl(parent,0x0);
		int h = optionHelper.getInputImageWidth();
		Text heightText = optionControls[1].createText("Height:", h + "");
		heightText.addKeyListener(this);

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
	
	public void keyPressed(KeyEvent e) {
		//  if e.control == numericText
		e.doit = (e.keyCode >=48 && e.keyCode <= 57);
	}

	public void keyReleased(KeyEvent e) {
	}
}
