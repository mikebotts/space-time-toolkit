package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.LineStyler;

/**
 * Builds basic Line controls for StyleWidget
 * 
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Feb 3, 2006
 * @version 1.0
 */
public class BasicLineController extends OptionController {
	private Composite parent;
	private LineOptionHelper lineOptionHelper;
	
	public BasicLineController(Composite parent, LineStyler styler){
		this.parent = parent;
		this.styler = styler;
		
		lineOptionHelper = new LineOptionHelper(this);
		buildControls();
	}

	/**
	 * build the basic options for lines.  
	 * ASSert - a valid styler with non-null Stroke
	 */
	public void buildControls(){
		optionControls = new OptionControl[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		Spinner widthSpinner = optionControls[0].createSpinner("LineWidth:", 1, 10);
		widthSpinner.setSelection((int)lineOptionHelper.getLineWidth());
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createColorButton("Line Color:", lineOptionHelper.getLineColor());
		
		addSelectionListener(lineOptionHelper);
	}

}