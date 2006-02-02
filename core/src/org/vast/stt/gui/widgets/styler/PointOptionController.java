package org.vast.stt.gui.widgets.styler;

import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.Graphic;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.GraphicSource;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.style.PointStyler;

public class PointOptionController implements SelectionListener 
	//extends OptionController
{
	private PointStyler styler;
	private Spinner sizeSpinner;
	private Button colorButton;
	private OptionControl [] optionControl;
	
	public PointOptionController(PointStyler styler){
		this.styler = styler;
	}
	
	public void buildAdvancedControls(Composite parent){
		
	}
	
	public void buildBasicControls(Composite parent) {
		// TODO Auto-generated method stub
		optionControl = new OptionControl[2];
		optionControl[0] = new OptionControl(parent);
		sizeSpinner = optionControl[0].createSpinner("Point Size:", 1,10);
		sizeSpinner.setSelection((int)getPointSize());
		
		optionControl[1] = new OptionControl(parent);
		colorButton = optionControl[1].createColorButton("Point Color:", getPointColor());
		colorButton.addSelectionListener(this);
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		//  uncomment when Stylers are working
		if(control == sizeSpinner) {
			float size = (float)sizeSpinner.getSelection();
			setPointSize(size);
			styler.updateDataMappings();
		} else if(control == colorButton) {
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			setPointColor(rgb);
			styler.updateDataMappings();
		}
	}

	private void setPointSize(float f){
		Graphic graphic = styler.getSymbolizer().getGraphic();
		ScalarParameter size = new ScalarParameter();
		size.setConstantValue(f);
		graphic.setSize(size);
	}

	/**
	 * Convenience method to set line color
	 * @param swtRgb
	 */
	private void setPointColor(RGB swtRgb){
		Color color = new Color(swtRgb.red, swtRgb.green, swtRgb.blue, 255);
		optionControl[1].setColorLabelColor(color);
		Graphic graphic = styler.getSymbolizer().getGraphic();
		List graphicSourceList = graphic.getGlyphs();
		if(graphicSourceList == null)  return;
		GraphicSource graphicSource = (GraphicSource)graphicSourceList.get(0);
		if(graphicSource instanceof GraphicMark) {
			GraphicMark gm = (GraphicMark)graphicSource;
			ScalarParameter newColor = new ScalarParameter();
			newColor.setConstantValue(color);
			gm.getFill().setColor(newColor);
		}
	}
	
	public float getPointSize(){
		Graphic graphic = styler.getSymbolizer().getGraphic();
		ScalarParameter size = graphic.getSize();
		if(size == null)
			return 1.0f;
		Object val = size.getConstantValue();
		if(val == null)
			return 1.0f;
		return ((Float)val).floatValue();
	}
	
	public org.vast.ows.sld.Color getPointColor(){
		Graphic graphic = styler.getSymbolizer().getGraphic();
		List graphicSourceList = graphic.getGlyphs();
		//if(graphicSourceList == null)  return;
		GraphicSource graphicSource = (GraphicSource)graphicSourceList.get(0);
		if(graphicSource instanceof GraphicMark) {
			GraphicMark gm = (GraphicMark)graphicSource;
			ScalarParameter color = gm.getFill().getColor();
			if(color == null)
				return new Color(1.0f, 0.0f, 0.0f, 1.0f);
			Object val = color.getConstantValue();
			if(val == null)
				return new Color(1.0f, 0.0f, 0.0f, 1.0f);
			return (Color)val;
		}
		return new Color(1.0f, 0.0f, 0.0f, 1.0f);
	}
}
