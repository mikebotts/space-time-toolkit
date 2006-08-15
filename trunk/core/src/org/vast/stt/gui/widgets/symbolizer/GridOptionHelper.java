package org.vast.stt.gui.widgets.symbolizer;

import org.vast.ows.sld.Color;
import org.vast.ows.sld.Fill;
import org.vast.ows.sld.GridFillSymbolizer;
import org.vast.ows.sld.GridMeshSymbolizer;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;


public class GridOptionHelper 
{
	GridSymbolizer symbolizer;
    
	public GridOptionHelper(GridSymbolizer sym){
        symbolizer = sym;
	}
	
	public void setGridMeshColor(Color sldColor){
		Stroke stroke = ((GridMeshSymbolizer)symbolizer).getStroke();
		stroke.setColor(sldColor);
	}
	
	public void setGridMeshWidth(float w){
		Stroke stroke = ((GridMeshSymbolizer)symbolizer).getStroke();
		ScalarParameter width = stroke.getWidth();
		//ScalarParameter width = new ScalarParameter();
		width.setConstantValue(w);
		//stroke.setWidth(width);		
	}
	
	public Color getFillColor(){
//		Fill fill = symbolizer.getFill();
//		if(fill == null) {
//			System.err.println("Fill is NULL.  Do what now?");
//			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
//			//return null;
//		}
//		Color fillColor = fill.getColor();
//		ScalarParameter redSP = fillColor.getRed();
//		ScalarParameter greenSP = fillColor.getGreen();
//		ScalarParameter blueSP = fillColor.getBlue();
//		ScalarParameter alphaSP = fillColor.getAlpha();
//		
//		if(!redSP.isConstant() || !greenSP.isConstant() || !blueSP.isConstant() || !alphaSP.isConstant()) {
//			System.err.println("At least one FillColor channel is mapped.  Do what now?");
//			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
//		}
//		
//		return fillColor;
		return null;
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
	public void setGridFillColor(org.vast.ows.sld.Color sldColor){
		Fill fill = ((GridFillSymbolizer)symbolizer).getFill();
		fill.setColor(sldColor);
	}
	
	private void setFillGrid(boolean b){
//		if(!b) {
//			Fill f = symbolizer.getFill();
//			symbolizer.setFill(null);
//		}
	}
}
