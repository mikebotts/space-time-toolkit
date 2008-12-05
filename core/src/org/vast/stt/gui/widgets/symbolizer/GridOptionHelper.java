/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.symbolizer;

import org.vast.ows.sld.Color;
import org.vast.ows.sld.Fill;
import org.vast.ows.sld.GridBorderSymbolizer;
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
		width.setConstantValue(w);
	}
	
	public float getGridMeshWidth(){
		Stroke stroke = ((GridMeshSymbolizer)symbolizer).getStroke();
		ScalarParameter width = stroke.getWidth();
		//  Was getting NPE here, so I add a width if it's null here.
		//  IMO, Scalar Params should never be null here.  Otherwise, 
		//  I will have to check them all wherever I access them.  TC
		if(width == null) {
			width = new ScalarParameter();
			width.setConstant(true);
			width.setConstantValue(new Float(1.0));
			stroke.setWidth(width);
			return 1.0f;
		}
		
		if(width.isConstant())
			return ((Float)width.getConstantValue()).floatValue();
		else 
			return 1.0f;  //Not sure what to return here
	}
	
	public Color getGridMeshColor(){
		Stroke stroke = ((GridMeshSymbolizer)symbolizer).getStroke();
		return stroke.getColor();
	}
	
	/**
	 * Convenience method to set line color
	 * @param swtRgb
	 */
	public void setGridFillColor(org.vast.ows.sld.Color sldColor){
		Fill fill = ((GridFillSymbolizer)symbolizer).getFill();
		fill.setColor(sldColor);
	}
	
	public Color getGridFillColor(){
		Fill fill = ((GridFillSymbolizer)symbolizer).getFill();
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
			//System.err.println("At least one FillColor channel is mapped.  Do what now?");
			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
		}
		
		return fillColor;
	}
	
	public float getGridFillOpacity(){
		Fill fill = ((GridFillSymbolizer)symbolizer).getFill();
		if(fill == null) {
			System.err.println("GridOptionHelper.getGridFillOpacity:\n\tFill is NULL.  Do what now?");
			return 1.0f;		
		}
		
		ScalarParameter opSp = fill.getOpacity();
		
		if(opSp==null || !opSp.isConstant()) {
			System.err.println("GridOptionHelper.getGridFillOpacity:\n\tOpacity is either NULL or mapped- Not supported yet");
			return 1.0f;		
		}
		
		return ((Float)opSp.getConstantValue()).floatValue();
	}
	
	public void setGridFillOpacity(float f){
		Fill fill = ((GridFillSymbolizer)symbolizer).getFill();
		ScalarParameter opSp = new ScalarParameter();
		opSp.setConstant(true);
		opSp.setConstantValue(f);
		fill.setOpacity(opSp);
		//  Not sure why we have to set opacity AND alpha to the same val here, but that's 
		//  thwe way it is done in SLDReader
		fill.getColor().setAlpha(opSp);
	}
		
	public float getGridBorderWidth(){
		Stroke stroke = ((GridBorderSymbolizer)symbolizer).getStroke();
		ScalarParameter width = stroke.getWidth();

		if(width.isConstant())
			return ((Float)width.getConstantValue()).floatValue();
		else 
			return 1.0f;  //Not sure what to return here
	}
	public void setGridBorderWidth(float w){
		Stroke stroke = ((GridBorderSymbolizer)symbolizer).getStroke();
		ScalarParameter width = stroke.getWidth();
		width.setConstantValue(w);
	}
	
	public Color getGridBorderColor(){
		Stroke stroke = ((GridBorderSymbolizer)symbolizer).getStroke();
		return stroke.getColor();
	}
	
	public void setGridBorderColor(org.vast.ows.sld.Color sldColor){
		Stroke stroke = ((GridBorderSymbolizer)symbolizer).getStroke();
		stroke.setColor(sldColor);
	}
		
	
	public int getGridWidth(){
//		ScalarParameter widthSP = symbolizer.getDimensions().getWidth();
//		//  for SRTM width,length,depth are all properties taken from coverageData/width...
//		//  how to get these values?
//		if(widthSP == null)
//			return -1;
//		if(widthSP.isConstant())
//			return ((Integer)widthSP.getConstantValue()).intValue();
//		return -1; // what to do here?
        return -1;
	}
	
	public int getGridLength(){
//		ScalarParameter lengthSP = symbolizer.getDimensions().getLength();
//		if(lengthSP == null)
//			return -1;
//		if(lengthSP.isConstant())
//			return ((Integer)lengthSP.getConstantValue()).intValue();
//		return -1; // what to do here?
        return -1;
	}
	
	public int getGridDepth(){
//		ScalarParameter depthSP = symbolizer.getDimensions().getDepth();
//		if(depthSP == null)
//			return -1;
//		if(depthSP.isConstant())
//			return ((Integer)depthSP.getConstantValue()).intValue();
//		return -1; // what to do here?
        return -1;
	}
	
}
