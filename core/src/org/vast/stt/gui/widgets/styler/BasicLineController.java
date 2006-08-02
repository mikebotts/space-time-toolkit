package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Line Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Line controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicLineController extends OptionController
{
	private LineOptionHelper lineOptionHelper;
	
	public BasicLineController(Composite parent, LineSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		
		lineOptionHelper = new LineOptionHelper(this);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.SPINNER, "Line Width:", new int[] {1, 10}),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Line Color:",
					lineOptionHelper.getLineColor())
		};
		optionControls = OptionControl.createControls(parent, params);
		Spinner widthSpinner = (Spinner)optionControls[0].getControl();
		widthSpinner.setSelection((int)lineOptionHelper.getLineWidth());
		addSelectionListener(lineOptionHelper);
	}
}