package org.vast.stt.gui.widgets.DataProcess;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.process.FlatGridGenerator_Process;

/**
 * <p><b>Title:</b><br/>
 * FlatGridOptionController
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling FlatGridGenerator_Process options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 13, 2006
 * @version 1.0
 * 
 */

public class FlatGridOptionController extends OptionController
{
	private SensorMLProvider provider;
	private FlatGridOptionHelper optionHelper;
	
	public FlatGridOptionController(Composite parent, FlatGridGenerator_Process fgProc, SensorMLProvider provider){
		this.provider = provider;
		optionHelper = new FlatGridOptionHelper(this, fgProc, provider);
		buildBasicControls(parent);
	}

	//  TODO  possible add request type toggle and server
	public void buildBasicControls(Composite parent) {
		int width = optionHelper.getWidth();
		int length = optionHelper.getLength();
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Width:", "" + width),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Length:", "" + length),	
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
