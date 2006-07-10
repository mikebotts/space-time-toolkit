package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.vast.ows.sld.Dimensions;
import org.vast.ows.sld.RasterSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.project.DataStyler;
import org.vast.stt.style.RasterStyler;
import org.vast.stt.style.TextureMappingStyler;

public class RasterOptionHelper implements SelectionListener 
{
	OptionController optionController;
	RasterSymbolizer symbolizer;
	DataStyler styler;
	
	public RasterOptionHelper(OptionController loc){
		optionController = loc;
		//  styler must not change for this to work
		DataStyler styler = optionController.getStyler();
		//  a bit of hackery so I can reuse RasterOptionHelper for Textures
		//  rethink this logic...
		if(styler instanceof RasterStyler)
			symbolizer = (RasterSymbolizer)styler.getSymbolizer();
		else if (styler instanceof TextureMappingStyler) {
			TextureSymbolizer symTmp= (TextureSymbolizer)styler.getSymbolizer();
			symbolizer = symTmp.getImagery();
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
			styler.updateDataMappings();
		} else if(control == optionControls[1].getControl()) {
			styler.updateDataMappings();
		}
	}

}
