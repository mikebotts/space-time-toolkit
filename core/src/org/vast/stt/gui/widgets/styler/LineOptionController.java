package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.style.LineStyler;

public class LineOptionController implements SelectionListener
	//implements OptionController
{ 
	private OptionControl[] basicControl;
	LineOptionHelper optionHelper;
	private LineStyler styler;
	private Spinner widthSpinner;
	private Button colorButton;
	
	public LineOptionController(){
	}

	public void setStyler(LineStyler styler){
		//  Move this to constructor 
		this.styler = styler;
		optionHelper = new LineOptionHelper(styler);
	}
	
	/**
	 * build the basic options for lines.  
	 * ASSert - a valid styler with non-null Stroke
	 */
	public void buildControls(Composite parent){
		basicControl = new OptionControl[2];
		basicControl[0] = new OptionControl(parent);
		widthSpinner = basicControl[0].createSpinner("LineWidth:", 1, 10);
		widthSpinner.setSelection((int)optionHelper.getLineWidth());
		widthSpinner.addSelectionListener(this);
		
		basicControl[1] = new OptionControl(parent);
		colorButton = 
			basicControl[1].createColorButton("Line Color:", optionHelper.getLineColor());
		colorButton.addSelectionListener(this);
	}


	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		if(control == widthSpinner) { 
			float w = new Float(widthSpinner.getSelection()).floatValue();
			optionHelper.setLineWidth(w);
			styler.updateDataMappings();
		} else if (control == colorButton){
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			setLineColor(rgb);
			styler.updateDataMappings();
		}
	}
	
	public void setLineColor(RGB swtRgb){
		Color color = new Color(swtRgb.red, swtRgb.green, swtRgb.blue, 255);
		basicControl[1].setColorLabelColor(color);
		optionHelper.setLineColor(color);
	}
}
