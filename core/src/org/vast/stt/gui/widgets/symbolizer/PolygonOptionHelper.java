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
import org.vast.ows.sld.PolygonSymbolizer;
import org.vast.stt.gui.widgets.OptionController;


public class PolygonOptionHelper 
{
	OptionController optionController;
	PolygonSymbolizer symbolizer;
	
	public PolygonOptionHelper(OptionController loc){
		optionController = loc;
        symbolizer = (PolygonSymbolizer)optionController.getSymbolizer();
	}
	
	public Color getFillColor(){
		return symbolizer.getFill().getColor();
	}
		
	/**
	 * Convenience method to set line color
	 * @param swtRgb
	 */
	public void setFillColor(org.vast.ows.sld.Color sldColor){
		Fill fill = symbolizer.getFill();
		fill.setColor(sldColor);
	}
}
