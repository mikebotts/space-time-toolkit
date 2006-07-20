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
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;


public class PointOptionHelper implements SelectionListener
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
		
	private void setPointSize(float f){
		Graphic graphic = symbolizer.getGraphic();
		ScalarParameter size = new ScalarParameter();
		size.setConstantValue(f);
		graphic.setSize(size);
	}

	/**
	 * Convenience method to set point color
	 * @param swtRgb
	 */
	private void setPointColor(org.vast.ows.sld.Color sldColor){
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

    
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControls = optionController.getControls();

		if(control == optionControls[0].getControl()) {
			Spinner sizeSpinner = (Spinner)control;
			float size = (float)sizeSpinner.getSelection();
			setPointSize(size);
            optionController.getDataItem().dispatchEvent(new STTEvent(this, EventType.ITEM_STYLE_CHANGED));
		} else if(control == optionControls[1].getControl()) {
			Button colorButton = (Button)control;
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			setPointColor(sldColor);
            optionController.getDataItem().dispatchEvent(new STTEvent(this, EventType.ITEM_STYLE_CHANGED));
		}
	}

}
