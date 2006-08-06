package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.Fill;
import org.vast.ows.sld.GridFillSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;


public class GridOptionHelper implements SelectionListener
{
	OptionController optionController;
	GridFillSymbolizer symbolizer;
    
	public GridOptionHelper(OptionController loc){
		optionController = loc;
		//  styler must not change for this to work
        Symbolizer sym = optionController.getSymbolizer();
		//  a bit of hackery so I can reuse GridOptionHelper for Textures
		//  rethink this logic...
        symbolizer = (GridFillSymbolizer)sym;
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
		symbolizer.getFill().setColor(sldColor);
	}
	
	private void setFillGrid(boolean b){
		if(!b) {
			Fill f = symbolizer.getFill();
			symbolizer.setFill(null);
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) { // fill, fillCol, showMesh, meshCol
		Control control = (Control)e.getSource();
		OptionControl[] optionControls = optionController.getControls();

		if(control == optionControls[0].getControl()) {  //  toggle fill
			boolean ckState = ((Button)control).getSelection();
			setFillGrid(ckState);
            optionController.getDataItem().dispatchEvent(new STTEvent(this, EventType.ITEM_SYMBOLIZER_CHANGED));
		} else if(control == optionControls[1].getControl()) {  //  fillColor
			Button colorButton = (Button)control;
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			setFillColor(sldColor);
            optionController.getDataItem().dispatchEvent(new STTEvent(this, EventType.ITEM_SYMBOLIZER_CHANGED));
		} else if(control == optionControls[2].getControl()) {
			//  setShowMesh
		} else if(control == optionControls[3].getControl()) {
			//  setMeshColor
		}
	}

}
