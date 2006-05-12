package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.style.LabelStyler;

public class BasicLabelController extends OptionController  
{
	private LabelOptionHelper labelOptionHelper;
	
	public BasicLabelController(Composite parent, LabelStyler styler){
		this.styler = styler;

		labelOptionHelper = new LabelOptionHelper(this);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){
		// TODO populate widgets with current Font's values
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.TEXT, "Label Text:", "Dummy"),	
			new OptionParams(OptionControl.ControlType.BUTTON, "Font:", "..."),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Font Color:",
					labelOptionHelper.getLabelColor())	
			//  rotation
			//  anchor x,y
			//  displacement x,y
		};
		optionControls = OptionControl.createControls(parent, params);
		addSelectionListener(labelOptionHelper);
	}
}
