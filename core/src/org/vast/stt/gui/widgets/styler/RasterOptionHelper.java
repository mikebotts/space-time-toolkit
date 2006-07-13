package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.vast.ows.sld.Dimensions;
import org.vast.ows.sld.RasterSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;


public class RasterOptionHelper implements SelectionListener 
{
	OptionController optionController;
	RasterSymbolizer symbolizer;

	
	public RasterOptionHelper(OptionController loc){
		optionController = loc;
		//  styler must not change for this to work
		Symbolizer sym = optionController.getSymbolizer();
		//  a bit of hackery so I can reuse RasterOptionHelper for Textures
		//  rethink this logic...
		if(sym instanceof RasterSymbolizer)
			symbolizer = (RasterSymbolizer)sym;
		else if (sym instanceof TextureSymbolizer) {
            symbolizer = ((TextureSymbolizer)sym).getImagery();
		}
	}
	
	public int getWidth(){
		Dimensions dims = symbolizer.getDimensions();
		ScalarParameter widthSP = dims.getWidth();

		if(widthSP.isConstant()) {
			return ((Integer)widthSP.getConstantValue()).intValue();
		} else
			return -1;
	}
	
	public int getHeight(){
		Dimensions dims = symbolizer.getDimensions();
		ScalarParameter lengthSP = dims.getLength();

		if(lengthSP.isConstant()) {
			return ((Integer)lengthSP.getConstantValue()).intValue();
		} else
			return -1;
	}
		
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControls = optionController.getControls();

		if(control == optionControls[0].getControl()) {
            optionController.getDataItem().dispatchEvent(new STTEvent(this, EventType.ITEM_STYLE_CHANGED));
		} else if(control == optionControls[1].getControl()) {
            optionController.getDataItem().dispatchEvent(new STTEvent(this, EventType.ITEM_STYLE_CHANGED));
		}
	}

}
