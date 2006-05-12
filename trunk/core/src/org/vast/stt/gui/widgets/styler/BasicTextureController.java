package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.style.TextureMappingStyler;

/**
 * 
 * @author tcook
 * @since: 3/16/05
 */

public class BasicTextureController extends OptionController
{
	private RasterOptionHelper rasterOptionHelper;
	private GridOptionHelper gridOptionHelper;
	
	public BasicTextureController(Composite parent, TextureMappingStyler styler){
		this.styler = styler;

		rasterOptionHelper = new RasterOptionHelper(this);
		gridOptionHelper = new GridOptionHelper(this);
		buildControls(parent);
	}

	public void buildControls(Composite parent){
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Width:", 
					rasterOptionHelper.getWidth() + ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Height:",
					rasterOptionHelper.getHeight() + ""),
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Width:",
					gridOptionHelper.getGridWidth() + ""),
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Length:",
					gridOptionHelper.getGridLength() + ""),
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Grid Depth:",
					gridOptionHelper.getGridDepth() + ""),
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Show Wiremesh:", false),
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Medh Color:", 
					gridOptionHelper.getFillColor())
		};
		optionControls = OptionControl.createControls(parent, params);
		//  Current GridOptionHelper won't work for this- rethink
		addSelectionListener(gridOptionHelper);
		addSelectionListener(rasterOptionHelper);
	}
}
