package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.TextSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Label Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Label controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicLabelController extends OptionController  
{
	private LabelOptionHelper labelOptionHelper;
	
	public BasicLabelController(Composite parent, TextSymbolizer symbolizer){
		this.symbolizer = symbolizer;

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
