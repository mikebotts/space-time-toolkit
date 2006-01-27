package org.vast.stt.gui.widgets.styler;


import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;
import org.vast.stt.style.LineStyler;

public class LineOptionController 
	implements OptionController, ModifyListener, VerifyListener, KeyListener {

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
		widthSpinner.setSelection((int)getLineWidth());
		widthSpinner.addSelectionListener(this);
			
		optionControl[1] = new OptionControl(parent);
		colorButton = optionControl[1].createColorButton("Line Color:", getLineColor());
		colorButton.addSelectionListener(this);
		
//		optionControl[1] = new OptionControl(parent);
//		Text widthText = optionControl[1].createText("Some Text", "");
//		widthText.addModifyListener(this);
//		widthText.addVerifyListener(this);
//		widthText.addKeyListener(this);
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

	private float getLineWidth(){
		ScalarParameter widthSP = styler.getSymbolizer().getStroke().getWidth();
		if(widthSP == null)
			return 1.0f;
		Object widthCon = widthSP.getConstantValue();
		if(widthCon == null)
			return 1.0f;
		return ((Float)widthCon).floatValue();
	}
	
	private org.vast.ows.sld.Color getLineColor(){
		ScalarParameter colorSP = styler.getSymbolizer().getStroke().getColor();
		if(colorSP == null)
			return new Color(1.0f, 0.0f, 0.0f, 1.0f);
		Object colorCon = colorSP.getConstantValue();
		if(colorCon == null)
			return new Color(1.0f, 0.0f, 0.0f, 1.0f);
		return (Color)colorCon;
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

	public void modifyText(ModifyEvent e) {
		// TODO Auto-generated method stub
		System.err.println(e);
	}

	public void verifyText(VerifyEvent e) {
		// TODO Auto-generated method stub
		System.err.println(e);
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if((e.keyCode<48 || e.keyCode > 57)) { // && e.keyCode!=0) {
			//System.err.println("NOT A NUMBER");
			e.doit = false;
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	
}
