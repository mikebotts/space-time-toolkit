package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.Fill;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.GridStyler;
import org.vast.stt.style.TextureMappingStyler;

public class GridOptionHelper implements SelectionListener {

	OptionController optionController;
	DataStyler styler;
	GridSymbolizer symbolizer;
	
	public GridOptionHelper(OptionController loc){
		optionController = loc;
		//  styler must not change for this to work
		styler = optionController.getStyler();
		//  a bit of hackery so I can reuse GridOptionHelper for Textures
		//  rethink this logic...
		if(styler instanceof GridStyler)
			symbolizer = (GridSymbolizer)styler.getSymbolizer();
		else if (styler instanceof TextureMappingStyler) {
			TextureSymbolizer symTmp= (TextureSymbolizer)styler.getSymbolizer();
			symbolizer = symTmp.getGrid();
		}
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
		
		if(redSP.isMapped() || greenSP.isMapped() || blueSP.isMapped() || alphaSP.isMapped()) {
			System.err.println("At least one FillColor channel is mapped.  Do what now?");
			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
		}
		
		return fillColor;
	}
		
	public int getGridWidth(){
		ScalarParameter widthSP = symbolizer.getDimensions().getWidth();
		//  for SRTM width,length,depth are all properties taken from coverageData/width...
		//  how to get these values?
		if(widthSP == null)
			return -1;
		if(widthSP.isConstant())
			return ((Integer)widthSP.getConstantValue()).intValue();
		return -1; // what to do here?
	}
	
	public int getGridLength(){
		ScalarParameter lengthSP = symbolizer.getDimensions().getLength();
		if(lengthSP == null)
			return -1;
		if(lengthSP.isConstant())
			return ((Integer)lengthSP.getConstantValue()).intValue();
		return -1; // what to do here?
	}
	
	public int getGridDepth(){
		ScalarParameter depthSP = symbolizer.getDimensions().getDepth();
		if(depthSP == null)
			return -1;
		if(depthSP.isConstant())
			return ((Integer)depthSP.getConstantValue()).intValue();
		return -1; // what to do here?
	}
	
	/**
	 * Convenience method to set line color
	 * @param swtRgb
	 */
	private void setFillColor(org.vast.ows.sld.Color sldColor){
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControls = optionController.getControls();

//		if(control == optionControls[0].getControl()) {
//			Spinner sizeSpinner = (Spinner)control;
//			float size = (float)sizeSpinner.getSelection();
//			setPointSize(size);
//			styler.updateDataMappings();
//		} else if(control == optionControls[1].getControl()) {
//			Button colorButton = (Button)control;
//			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
//			RGB rgb = colorChooser.open();
//			if(rgb == null)
//				return;
//			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
//			optionControls[1].setColorLabelColor(sldColor); 
//			setPointColor(sldColor);
//			
//			styler.updateDataMappings();
//		}
	}

}
