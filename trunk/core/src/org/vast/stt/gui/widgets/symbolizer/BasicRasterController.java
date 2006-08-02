package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.RasterSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Raster Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Raster controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicRasterController extends OptionController
{
	private RasterOptionHelper rasterOptionHelper;
	
	public BasicRasterController(Composite parent, RasterSymbolizer symbolizer){
		this.symbolizer = symbolizer;

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
