package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.PolygonSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Polygon Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Polygon controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicPolygonController extends OptionController 
{
	private PolygonOptionHelper polygonOptionHelper;
	
	public BasicPolygonController(Composite parent, PolygonSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		polygonOptionHelper = new PolygonOptionHelper(this);
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

	@Override
	public void loadFields() {
		// TODO Auto-generated method stub
		
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
