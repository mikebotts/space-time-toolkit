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
import org.vast.ows.sld.Dimensions;
import org.vast.ows.sld.RasterSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
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
	}
	
	public float getWidth(){
//		Dimensions dims = symbolizer.getRasterDimensions();
//		ScalarParameter widthSP = dims.getWidth();
//
//		if(widthSP.isConstant()) {
//			return ((Float)widthSP.getConstantValue()).floatValue();
//		} else
//			return -1;
        return -1;
	}
	
	public float getHeight(){
//		Dimensions dims = symbolizer.getRasterDimensions();
//		ScalarParameter lengthSP = dims.getLength();
//
//		if(lengthSP.isConstant()) {
//			return ((Float)lengthSP.getConstantValue()).floatValue();
//		} else
//			return -1;
        return -1;
	}
		
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControls = optionController.getControls();

		if(control == optionControls[0].getControl()) {
            optionController.getDataItem().dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		} else if(control == optionControls[1].getControl()) {
            optionController.getDataItem().dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		}
	}

}
