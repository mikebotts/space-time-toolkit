package org.vast.stt.gui.widgets.symbolizer;

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
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;


public class PointOptionHelper 
{
 	OptionController optionController;
	PointSymbolizer symbolizer;
    

	public PointOptionHelper(OptionController loc){
		optionController = loc;
        symbolizer = (PointSymbolizer)optionController.getSymbolizer();
	}
	
	public Color getPointColor(){
		Graphic graphic = symbolizer.getGraphic();
		List graphicSourceList = graphic.getGlyphs();
		//if(graphicSourceList == null)  return;
		GraphicSource graphicSource = (GraphicSource)graphicSourceList.get(0);
		if(graphicSource instanceof GraphicMark) {
			GraphicMark gm = (GraphicMark)graphicSource;
			Color color = gm.getFill().getColor();
            if (color == null)
                return new Color(1.0f, 0.0f, 0.0f, 1.0f);
			return color;
		}
		return new Color(1.0f, 0.0f, 0.0f, 1.0f);
	}
		
	public void setPointSize(float f){
		Graphic graphic = symbolizer.getGraphic();
		ScalarParameter size = new ScalarParameter();
		size.setConstantValue(f);
		graphic.setSize(size);
	}

	/**
	 * Convenience method to set point color
	 * @param swtRgb
	 */
	public void setPointColor(org.vast.ows.sld.Color sldColor){
		Graphic graphic = symbolizer.getGraphic();
		List graphicSourceList = graphic.getGlyphs();
		if(graphicSourceList == null)  return;
		GraphicSource graphicSource = (GraphicSource)graphicSourceList.get(0);
		if(graphicSource instanceof GraphicMark) {
			GraphicMark gm = (GraphicMark)graphicSource;
			gm.getFill().setColor(sldColor);
		}
	}
	
	public float getPointSize(){
		Graphic graphic = symbolizer.getGraphic();
		ScalarParameter size = graphic.getSize();
		if(size == null)
			return 1.0f;
		Object val = size.getConstantValue();
		if(val == null)
			return 1.0f;
		return ((Float)val).floatValue();
	}

    
}
