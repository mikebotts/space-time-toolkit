package org.vast.stt.gui.widgets.symbolizer;

import org.vast.ows.sld.Color;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;


public class LineOptionHelper //implements SelectionListener
{
	LineSymbolizer symbolizer;
	//  Need a way to specify all options/types/args so 
	//  AdvancedOptions can use them also - just repeating
	//  code for now, and not using the labels/optTypes fields - TC
	//private String [] labels = {"Line Width:", "Line Color:"};
	//private int [] optTypes = { 0, 1}; 

	public LineOptionHelper(LineSymbolizer sym){
		symbolizer = sym;
	}
	
	public float getLineWidth(){
		ScalarParameter widthSP = symbolizer.getStroke().getWidth();
		if(widthSP == null)
			return 1.0f;
		Object widthCon = widthSP.getConstantValue();
		if(widthCon == null)
			return 1.0f;
		return ((Float)widthCon).floatValue();
	}
	
	public org.vast.ows.sld.Color getLineColor(){
		Color colorSP = symbolizer.getStroke().getColor();
		if(colorSP == null)
			return new Color(1.0f, 0.0f, 0.0f, 1.0f);
		return colorSP;
	}
	
	/**
	 * Convenience method to set line width
	 * @param w - width
	 */
	public void setLineWidth(float w){
		Stroke stroke = symbolizer.getStroke();
		ScalarParameter width = new ScalarParameter();
		width.setConstantValue(w);
		stroke.setWidth(width);
	}
	
	/**
	 * Convenience method to set line color
	 * @param Color
	 */
	public void setLineColor(Color color) {
		Stroke stroke = symbolizer.getStroke();
		stroke.setColor(color);
	}
}
