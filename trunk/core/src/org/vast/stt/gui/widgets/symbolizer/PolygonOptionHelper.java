package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.Fill;
import org.vast.ows.sld.PolygonSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;


public class PolygonOptionHelper implements SelectionListener
{
	OptionController optionController;
	PolygonSymbolizer symbolizer;
    
	
	public PolygonOptionHelper(OptionController loc){
		optionController = loc;
        symbolizer = (PolygonSymbolizer)optionController.getSymbolizer();
	}
	
	public Color getFillColor(){
		Fill fill = symbolizer.getFill();
		if(fill == null) {
			System.err.println("Fill is NULL.  Do what now?");
			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
			//return null;
		}
		Color fillColor = fill.getColor();
		ScalarParameter redSP = fillColor.getRed();
		ScalarParameter greenSP = fillColor.getGreen();
		ScalarParameter blueSP = fillColor.getBlue();
		ScalarParameter alphaSP = fillColor.getAlpha();
		
		if(!redSP.isConstant() || !greenSP.isConstant() || !blueSP.isConstant() || !alphaSP.isConstant()) {
			System.err.println("At least one FillColor channel is mapped.  Do what now?");
			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
		}
		
		return fillColor;
	}
		
	/**
	 * Convenience method to set line color
	 * @param swtRgb
	 */
	public void setFillColor(org.vast.ows.sld.Color sldColor){
	}
	
	//  TODO  is StrokeColor used for the "bounds" of the polygon
	public Color getBoundColor(){
		Stroke stroke = symbolizer.getStroke();
		if(stroke == null) {
			System.err.println("Stroke is NULL.  Do what now?");
			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
		}
		Color strokeColor = stroke.getColor();
		ScalarParameter redSP = strokeColor.getRed();
		ScalarParameter greenSP = strokeColor.getGreen();
		ScalarParameter blueSP = strokeColor.getBlue();
		ScalarParameter alphaSP = strokeColor.getAlpha();
		
		if(!redSP.isConstant() || !greenSP.isConstant() || !blueSP.isConstant() || !alphaSP.isConstant()) {
			System.err.println("At least one StrokeColor channel is mapped.  Do what now?");
			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
		}
		
		return strokeColor;
	}
		
	public void setBoundColor(org.vast.ows.sld.Color sldColor){
	}

	public boolean getFillPolygon(){
		// TODO
		return true;
	}
	
	public void setFillPolygon(boolean b){
//	  TODO (how is this actually set?)
	}
	
	public boolean getShowBounds(){
		//
		return true;
	}
	
	public void setShowBounds(boolean b){
		//  TODO (how is this actually set?)
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControls = optionController.getControls();

		//  TODO finish this
	}

}
