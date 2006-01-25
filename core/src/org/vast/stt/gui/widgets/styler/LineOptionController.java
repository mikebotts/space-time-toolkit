package org.vast.stt.gui.widgets.styler;


import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;
import org.vast.stt.style.LineStyler;

public class LineOptionController implements OptionController {

	private Composite parent;
	private LineStyler styler;
	private OptionControl[] optionControl;
	private Spinner widthSpinner;
	private Button colorButton;
	
	public LineOptionController(Composite parent, LineStyler styler){
		this.parent = parent;
		this.styler = styler;
		buildBasicOptions();
	}

	public void buildAdvancedOptions() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * build the basic options for lines.  
	 * ASSert - a valid styler with non-null Stroke
	 */
	public void buildBasicOptions() {
		// TODO Auto-generated method stub
		optionControl = new OptionControl[7];
		//optionControl = new OptionControl[2];
		optionControl[0] = new OptionControl(parent);
		widthSpinner = optionControl[0].createSpinner("LineWidth:", 1, 10);
		ScalarParameter widthSP = styler.getSymbolizer().getStroke().getWidth();
		if(widthSP != null) {
			int width = ((Float)(widthSP.getConstantValue())).intValue(); //
			widthSpinner.setSelection(width);
		}
		widthSpinner.addSelectionListener(this);
			
		optionControl[1] = new OptionControl(parent);
		ScalarParameter colorSP = styler.getSymbolizer().getStroke().getColor();
		Color sldColor;
		if(colorSP != null) 
			sldColor = (Color)colorSP.getConstantValue();
		else 
			sldColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);
			
		colorButton = optionControl[1].createColorButton("Line Color:", sldColor);
		colorButton.addSelectionListener(this);

		//  add a bunch of controls to test scrolling
//		optionControl[2] = new OptionControl(parent);
//		optionControl[2].createCombo("Line Test:", new String [] { "aaa", "bbb", "ccc" } );
//		optionControl[3] = new OptionControl(parent);
//		optionControl[3].createSpinner("LineWidth:", 1, 10);
//		optionControl[4] = new OptionControl(parent);
//		optionControl[4].createSpinner("LineWidth:", 1, 10);
//		optionControl[5] = new OptionControl(parent);
//		optionControl[5].createSpinner("LineWidth:", 1, 10);
//		optionControl[6] = new OptionControl(parent);
//		optionControl[6].createSpinner("LineWidth:", 1, 10);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		if(control == widthSpinner) {
			float w = new Float(widthSpinner.getSelection()).floatValue();
			setLineWidth(w);
			styler.updateDataMappings();
		} else if (control == colorButton){
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			setLineColor(rgb);
			styler.updateDataMappings();
		}
	}

	/**
	 * Convenience method to set line width
	 * @param w - width
	 */
	private void setLineWidth(float w){
		Stroke stroke = styler.getSymbolizer().getStroke();
		ScalarParameter width = new ScalarParameter();
		width.setConstantValue(w);
		stroke.setWidth(width);
	}
	
	/**
	 * Convenience method to set line color
	 * @param swtRgb
	 */
	private void setLineColor(RGB swtRgb){
		Color color = new Color(swtRgb.red, swtRgb.green, swtRgb.blue, 255);
		optionControl[1].setColorLabelColor(color);
		Stroke stroke = styler.getSymbolizer().getStroke();
		ScalarParameter newColor = new ScalarParameter();
		newColor.setConstantValue(color);
		stroke.setColor(newColor);
	}
}
