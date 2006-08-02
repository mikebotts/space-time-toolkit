package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Grid Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Grid controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicGridController extends OptionController 
{
	private GridOptionHelper gridOptionHelper;
	
	public BasicGridController(Composite parent, GridSymbolizer symbolizer){
		this.symbolizer = symbolizer;

		gridOptionHelper = new GridOptionHelper(this);
		buildControls(parent);
	}

	public void buildControls(Composite parent){
		int width = gridOptionHelper.getGridWidth();
		int length = gridOptionHelper.getGridLength();
		int depth = gridOptionHelper.getGridDepth();
		
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Fill Grid", new Boolean(true)),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Fill Color", 
					gridOptionHelper.getFillColor()),	
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Show Wiremesh", new Boolean(false)),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Mesh Color", 
					gridOptionHelper.getFillColor()),
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Width:", width + ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Length:", length + ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Depth:", depth + "")
		};
		
		optionControls = OptionControl.createControls(parent, params);
		// disable controls that don't work yet
		for(int i=2; i<7; i++)
			optionControls[i].setEnabled(false);
		addSelectionListener(gridOptionHelper);
	}
}
