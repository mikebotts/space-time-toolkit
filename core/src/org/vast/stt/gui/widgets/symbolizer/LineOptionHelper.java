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
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.MappingFunction;
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
	
	public boolean getWidthConstant(){
		ScalarParameter widthSP = symbolizer.getStroke().getWidth();
		return widthSP.isConstant();
	}
	
	public void setWidthConstant(boolean b){
		ScalarParameter widthSP = symbolizer.getStroke().getWidth();
		widthSP.setConstant(b);
	}
	
	public String getWidthProperty(){
		ScalarParameter widthSP = symbolizer.getStroke().getWidth();
		return widthSP.getPropertyName();
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
		
	public MappingFunction getWidthMappingFunction(){
		ScalarParameter widthSP = symbolizer.getStroke().getWidth();
		return widthSP.getMappingFunction();
	}
	
	public org.vast.ows.sld.Color getLineColor(){
		Color colorSP = symbolizer.getStroke().getColor();
		if(colorSP == null)
			return new Color(1.0f, 0.0f, 0.0f, 1.0f);
		return colorSP;
	}
	
	public boolean getLineSmooth(){
		return symbolizer.getStroke().getSmooth();
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
	public void setLineColor(org.vast.ows.sld.Color color) {
		Stroke stroke = symbolizer.getStroke();
		stroke.setColor(color);
	}
	
	public void setLineSmooth(boolean smooth) {
		symbolizer.getStroke().setSmooth(smooth);
	}
}
