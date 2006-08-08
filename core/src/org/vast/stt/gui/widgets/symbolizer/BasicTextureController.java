package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Texture Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Texture controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicTextureController extends OptionController
{
	private RasterOptionHelper rasterOptionHelper;
	private GridOptionHelper gridOptionHelper;
	
	public BasicTextureController(Composite parent, TextureSymbolizer symbolizer){
		this.symbolizer = symbolizer;

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
