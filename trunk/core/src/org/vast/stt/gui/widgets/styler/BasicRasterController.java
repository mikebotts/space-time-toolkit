package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.style.RasterStyler;

/**
 * 
 * @author tcook
 * @since: 3/16/05
 */

public class BasicRasterController extends OptionController
{
	private RasterOptionHelper rasterOptionHelper;
	
	public BasicRasterController(Composite parent, RasterStyler styler){
		this.styler = styler;

		rasterOptionHelper = new RasterOptionHelper(this);
		buildControls(parent);
	}

	public void buildControls(Composite parent){
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Width:", 
					rasterOptionHelper.getWidth() + ""),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Height:",
					rasterOptionHelper.getHeight() + "")
		};
		optionControls = OptionControl.createControls(parent, params);
		addSelectionListener(rasterOptionHelper);
	}
}
