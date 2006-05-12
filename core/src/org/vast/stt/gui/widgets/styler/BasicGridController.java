package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.style.GridStyler;

public class BasicGridController extends OptionController 
{
	private GridOptionHelper gridOptionHelper;
	
	public BasicGridController(Composite parent, GridStyler styler){
		this.styler = styler;

		gridOptionHelper = new GridOptionHelper(this);
		buildControls(parent);
	}

	public void buildControls(Composite parent){
		int width = gridOptionHelper.getGridWidth();
		int length = gridOptionHelper.getGridLength();
		int depth = gridOptionHelper.getGridDepth();
		
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Width:", width + ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Length:", length + ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Depth:", depth + ""),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Fill Grid", new Boolean(true)),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Fill Color", 
					gridOptionHelper.getFillColor()),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Show Wiremesh", new Boolean(false)),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Mesh Color", 
					gridOptionHelper.getFillColor())
		};
		
		optionControls = OptionControl.createControls(parent, params);
		addSelectionListener(gridOptionHelper);
	}
}
