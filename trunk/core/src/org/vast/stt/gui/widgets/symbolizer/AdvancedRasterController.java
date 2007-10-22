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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.PolygonSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b>
 * Advanced Raster Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds advanced Raster controls for Advanced Dialog
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class AdvancedRasterController extends AdvancedOptionController 
{
	private Composite parent;
	//private RasterOptionHelper rasterOptionHelper;
	
	public AdvancedRasterController(Composite parent, DataItem item, PolygonSymbolizer symbolizer){
		super(item);
		this.parent = parent;
		this.symbolizer = symbolizer;
		
		//rasterOptionHelper = new RasterOptionHelper(this);
		buildControls();
	}
	
	public void buildControls(){
	/*	optionControls = new OptionControl[2];
		mapFromCombo = new Combo[2];
		mappingBtn = new Button[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		optionControls[0].createText("Width:", "500");
		//advWidthSpinner.setSelection((int)rasterOptionHelper.getPointSize());
		//  add other controls
		addMappingControls(parent, 0);
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createText("Height:", "500");

		//addMappingControls(parent, 1);
		//  disable color mapFromCombo (consider just not showing this)
		mapFromCombo[1].setEnabled(false);
		//addSelectionListener(rasterOptionHelper);*/
	}

	public void setMappableItems(String [] items){
	}
	
	protected void mapFrom(int index, int selIndex){
		System.err.println("AdvLinControl:  mapFrom for " + index);
	}
	
	protected void editMapping(int index){
		System.err.println("AdvLinControl:  editMapping for " + index);
	}
	
	protected void isMapped(int index, boolean b){
		System.err.println("AdvLinControl:  isMapped for " + index);
		int selIndex = mapFromCombo[index].getSelectionIndex();
		switch(index){
		case 0:  // set lineWidth 
			break;
		case 1:
			break;  // set lineColor
		default:
			break;
		}
	}	
	

	public void loadFields(){}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	};
}
