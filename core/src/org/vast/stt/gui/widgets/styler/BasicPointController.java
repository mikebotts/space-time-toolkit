package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.style.PointStyler;

public class BasicPointController extends OptionController  
{
	private PointOptionHelper pointOptionHelper;
	
	public BasicPointController(Composite parent, PointStyler styler){
		this.styler = styler;

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
