package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Point Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Point controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicPointController extends OptionController  
{
	private PointOptionHelper pointOptionHelper;
	
	public BasicPointController(Composite parent, PointSymbolizer symbolizer){
		this.symbolizer = symbolizer;

		pointOptionHelper = new PointOptionHelper(this);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){
		//  get current size, color from styler
		Color c = pointOptionHelper.getPointColor();
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.SPINNER, "Point Size:", new int[] {1, 10}),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Point Color:", c)
		};
		optionControls = OptionControl.createControls(parent, params);
		Spinner sizeSpinner = (Spinner)optionControls[0].getControl();
		sizeSpinner.setSelection((int)pointOptionHelper.getPointSize());
		addSelectionListener(pointOptionHelper);
	}
}
