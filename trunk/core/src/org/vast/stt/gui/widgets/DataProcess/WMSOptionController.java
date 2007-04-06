package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.stt.provider.sml.SMLProvider;
import org.vast.stt.data.DataException;
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
	WMS_Process wmsProcess;
	private WMSProcessOptions wmsOptions;
	
	public WMSOptionController(Composite parent, WMS_Process wmsProc, SMLProvider provider){
		this.provider = provider;
		this.wmsProcess = wmsProc;
		wmsOptions = new WMSProcessOptions(wmsProc);
		buildBasicControls(parent);
	}
	
	public void buildBasicControls(Composite parent){
		WMSLayerCapabilities caps = wmsProcess.getCapabilities(); 
		String [] formatOpts = caps.getFormatList().toArray(new String[]{});
		String [] srsOpts = caps.getSrsList().toArray(new String[]{});
		String [] styleOpts = caps.getStyleList().toArray(new String[]{});
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Width:", ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Height:", ""),	
			new OptionParams(OptionControl.ControlType.COMBO, "Format:", formatOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "SRS:", srsOpts),	
			new OptionParams(OptionControl.ControlType.COMBO, "Style:", styleOpts),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Transparency:", false),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Maintain Aspect:", false)	
		};
		optionControls = OptionControl.createControls(parent, params);
		loadFields();
		addSelectionListener(this);
	}

	@Override
	//  load the initial state of the options based on what's currently in the provider
	public void loadFields() {
		Text wText = (Text)optionControls[0].getControl();
		wText.setText(wmsOptions.getInputImageWidth() + "");
		Text hText = (Text)optionControls[1].getControl();
		hText.setText(wmsOptions.getInputImageHeight() + "");
		Combo formatCombo = (Combo)optionControls[2].getControl();
		String format = wmsOptions.getFormat();
		//formatCombo.select(0); //  need lookup function (remarkably, Combo has no way to select based on String)
		//  OR
		formatCombo.setText(format);
		Combo srsCombo = (Combo)optionControls[3].getControl();
		String srs = wmsOptions.getSRS();
		srsCombo.setText(srs);  
		Combo styleCombo = (Combo)optionControls[4].getControl();
		//String style = wmsOptions.getStyle();
		//styleCombo.setText(style);
		//styleCombo.select(0);
		Button transBtn = (Button)optionControls[5].getControl();
		transBtn.setSelection(wmsOptions.getTransparency());
		Button aspectBtn = (Button)optionControls[6].getControl();
		aspectBtn.setSelection(wmsOptions.getMaintainAspect());
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		if(control == optionControls[0].getControl()) {  // width 
			String ws = ((Text)control).getText();
			int w = Integer.parseInt(ws);
			wmsOptions.setInputImageWidth(w);
		} else if (control == optionControls[1].getControl()) { //height
			String hs = ((Text)control).getText();
			int h = Integer.parseInt(hs);
			wmsOptions.setInputImageHeight(h);
		} else if (control == optionControls[2].getControl()) { //format
			String format = ((Combo)control).getText();
			wmsOptions.setFormat(format);
		} else if (control == optionControls[3].getControl()) { //SRS
			String srs = ((Combo)control).getText();
			wmsOptions.setSRS(srs);
		} else if (control == optionControls[4].getControl()) { //styles
			String style = ((Combo)control).getText();
			//  TODO  add Style supprt
			//setStyle(style);
		} else if (control == optionControls[5].getControl()) { //transparency
			boolean tr = ((Button)control).getSelection();
			wmsOptions.setTransparency(tr);
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
