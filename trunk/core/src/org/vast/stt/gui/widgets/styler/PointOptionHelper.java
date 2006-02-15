package org.vast.stt.gui.widgets.styler;

import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.Graphic;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.GraphicSource;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.PointStyler;

public class PointOptionHelper implements SelectionListener {

	OptionController optionController;
	PointStyler styler;

	public PointOptionHelper(OptionController loc){
		optionController = loc;
		//  styler must not change for this to work
		styler = (PointStyler)optionController.getStyler();
	}
	
	public ScalarParameter getPointColorScalar(){
		Graphic graphic = styler.getSymbolizer().getGraphic();
		List graphicSourceList = graphic.getGlyphs();
		//if(graphicSourceList == null)  return;
		GraphicSource graphicSource = (GraphicSource)graphicSourceList.get(0);
		if(graphicSource instanceof GraphicMark) {
			GraphicMark gm = (GraphicMark)graphicSource;
			ScalarParameter color = gm.getFill().getColor();
			return color;
		}
		return null;
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
	private void setPointColor(org.vast.ows.sld.Color sldColor){
		Graphic graphic = styler.getSymbolizer().getGraphic();
		List graphicSourceList = graphic.getGlyphs();
		if(graphicSourceList == null)  return;
		GraphicSource graphicSource = (GraphicSource)graphicSourceList.get(0);
		if(graphicSource instanceof GraphicMark) {
			GraphicMark gm = (GraphicMark)graphicSource;
			ScalarParameter newColor = new ScalarParameter();
			newColor.setConstantValue(sldColor);
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
		ScalarParameter color = getPointColorScalar();
		if(color == null)
			return new Color(1.0f, 0.0f, 0.0f, 1.0f);
		Object val = color.getConstantValue();
		if(val == null)
			return new Color(1.0f, 0.0f, 0.0f, 1.0f);
		return (Color)val;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControls = optionController.getControls();

		if(control == optionControls[0].getControl()) {
			Spinner sizeSpinner = (Spinner)control;
			float size = (float)sizeSpinner.getSelection();
			setPointSize(size);
			styler.updateDataMappings();
		} else if(control == optionControls[1].getControl()) {
			Button colorButton = (Button)control;
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			setPointColor(sldColor);
			
			styler.updateDataMappings();
		}
	}

}
