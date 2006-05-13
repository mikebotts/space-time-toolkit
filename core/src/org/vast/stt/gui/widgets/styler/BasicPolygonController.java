package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.style.PolygonStyler;

public class BasicPolygonController extends OptionController 
{
	private PolygonOptionHelper polygonOptionHelper;
	
	public BasicPolygonController(Composite parent, PolygonStyler styler){
		this.styler = styler;

		//polygonOptionHelper = new PolygonOptionHelper(this);
		buildControls(parent);
	}

	public void buildControls(Composite parent){
		
		OptionParams[] params = 
		{
		    new OptionParams(OptionControl.ControlType.CHECKBOX, "Show Bounds",
		    		polygonOptionHelper.getShowBounds()),
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Bound Color", 
					polygonOptionHelper.getBoundColor()),
			new OptionParams(OptionControl.ControlType.CHECKBOX, "Fill Polygon", 
					polygonOptionHelper.getFillPolygon()),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Fill Color", 
					polygonOptionHelper.getFillColor())
		};
		
		optionControls = OptionControl.createControls(parent, params);
		addSelectionListener(polygonOptionHelper);
	}
}
