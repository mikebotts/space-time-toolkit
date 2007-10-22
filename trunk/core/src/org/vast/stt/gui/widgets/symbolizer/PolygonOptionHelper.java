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
