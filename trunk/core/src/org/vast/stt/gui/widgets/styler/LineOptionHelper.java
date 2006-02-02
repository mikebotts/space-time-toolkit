package org.vast.stt.gui.widgets.styler;

import org.vast.ows.sld.Color;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;
import org.vast.stt.style.LineStyler;

public class LineOptionHelper {

	LineStyler styler;
	//  Need a way to specify all options/types/args so 
	//  AdvancedOptions can use them also - just repeating
	//  code for now, and not using the labels/optTypes fields - TC
	private String [] labels = {"Line Width:", "Line Color:"};
	private int [] optTypes = { 0, 1}; 

	public LineOptionHelper(LineStyler styler){
		this.styler = styler;
	}
	
	public float getLineWidth(){
		ScalarParameter widthSP = styler.getSymbolizer().getStroke().getWidth();
		if(widthSP == null)
			return 1.0f;
		Object widthCon = widthSP.getConstantValue();
		if(widthCon == null)
			return 1.0f;
		return ((Float)widthCon).floatValue();
	}
	
	public org.vast.ows.sld.Color getLineColor(){
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
	public void setLineWidth(float w){
		Stroke stroke = styler.getSymbolizer().getStroke();
		ScalarParameter width = new ScalarParameter();
		width.setConstantValue(w);
		stroke.setWidth(width);
	}
	
	/**
	 * Convenience method to set line color
	 * @param Color
	 */
	public void setLineColor(Color color) {
		Stroke stroke = styler.getSymbolizer().getStroke();
		ScalarParameter newColor = new ScalarParameter();
		newColor.setConstantValue(color);
		stroke.setColor(newColor);
	}
}
