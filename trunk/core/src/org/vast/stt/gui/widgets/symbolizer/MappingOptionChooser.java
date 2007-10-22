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

import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.MappingFunction;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionParams;

/**
 * @author Tony Cook
 *
 */
public class MappingOptionChooser extends OptionChooser
{
	OptionControl [] optionControls;
	MappingFunction func;
	
	public MappingOptionChooser(Composite parent){
		super(parent);
		buildControls(null);
	}
	
	public void buildControls(Object mappingFn){
		OptionParams[] params =	{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Gain:", "1.2345"),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Offset:",	"-12")
		};
		
		optionControls = OptionControl.createControls(optComp, params);
	}
	
	public void setMappingFunction(MappingFunction func){
		
	}
}

