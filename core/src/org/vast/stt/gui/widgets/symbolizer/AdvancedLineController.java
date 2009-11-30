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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.MappingFunction;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionParams;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b>
 * Advanced Line Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds advanced Line controls for Advanced Dialog
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class AdvancedLineController extends AdvancedOptionController
{ 
	private Composite parent;
	private LineOptionHelper lineOptionHelper;

	public AdvancedLineController(Composite parent, DataItem item, LineSymbolizer symbolizer){
		super(item);
		this.parent = parent;
		this.symbolizer = symbolizer;
		
		lineOptionHelper = new LineOptionHelper(symbolizer);
		buildControls();
		addSelectionListener(this);
	}
	
	//  These controls differ from basic in that they have 3 channels for color
	public void buildControls(){
		OptionParams[] params =	{
			new OptionParams(OptionControl.ControlType.SPINNER, "Line Width:", new int[] {1, 10}),	
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Line Color:",	null)
		};
		
		initMappingControls(params.length);
		
		addMappingButtons(parent, 0);
		optionControls[0] = OptionControl.createControl(parent, params[0]);
//		GridData gd = new GridData();
//		gd.horizontalAlignment = SWT.BEGINNING;
//		optionControls[0].setLayoutData(gd);
		addMapFromCombos(parent, 0);
		
		addMappingButtons(parent, 1);
		optionControls[1] = OptionControl.createControl(parent, params[1]);
		addMapFromCombos(parent, 1);
		mapFromCombo[1].setEnabled(false);

		//  set initial state
		loadFields();
	}
	
	//  Override to set the initial state of the Combos
	//  TODO:  add support for LUTS
	//  TODO:  test for mapped size, color
	public void setMappableItems(String [] items){
		super.setMappableItems(items);
		//  Width
		ScalarParameter widthSP = ((LineSymbolizer)symbolizer).getStroke().getWidth();
		if(widthSP != null)  //  is null sometimes.
			setOptionState(widthSP, 0);
		//  Color
		ScalarParameter redSP = ((LineSymbolizer)symbolizer).getStroke().getColor().getRed();		
		if(redSP != null)
			setOptionState(redSP, 1);
        ScalarParameter greenSP = ((LineSymbolizer)symbolizer).getStroke().getColor().getGreen();       
        if(redSP != null)
            setOptionState(greenSP, 1);
        ScalarParameter blueSP = ((LineSymbolizer)symbolizer).getStroke().getColor().getBlue();       
        if(redSP != null)
            setOptionState(blueSP, 1);
        ScalarParameter alphaSP = ((LineSymbolizer)symbolizer).getStroke().getColor().getAlpha();       
        if(redSP != null)
            setOptionState(alphaSP, 1);
	}
	
	protected void mapFrom(int index,  int selIndex){
		System.err.println("AdvLinControl:  mapFrom for " + index +", " + 
				mapFromCombo[index].getText());
	}
	
	protected void editMapping(int index){
		System.err.println("AdvLinControl:  editMapping for " + index);
		//  TODO  reset options in MappintOptChooser
		switch(index){
		case 0:  //  lineWidth
			MappingFunction func = lineOptionHelper.getWidthMappingFunction();
			
			break;
		case 1:  //  color
			break;
		}
	}
	
	protected void isMapped(int index, boolean b){
		System.err.println("AdvLinControl:  isMapped for " + index);
		//dataItem.getDataProvider().
		int selIndex = mapFromCombo[index].getSelectionIndex();
		switch(index){
		case 0:  // set lineWidth 
			optionControls[0].setEnabled(!b);
			lineOptionHelper.setWidthConstant(!b);
			break;
		case 1:
			break;  // set lineColor
		default:
			break;
		}
	}	
	
    @Override
    // reset value of all controls to what is currently in symbolizer
    //   TODO  deal with color and rgba
    public void loadFields(){

    	//  width
    	Runnable loadFieldsThread = new Runnable(){
    		public void run(){
    			Spinner widthSpinner = (Spinner)optionControls[0].getControl();
    			widthSpinner.setSelection((int)lineOptionHelper.getLineWidth());
    			boolean widthMapped = !lineOptionHelper.getWidthConstant();
    			isMappedBtn[0].setSelection(widthMapped);
    			optionControls[0].setEnabled(!widthMapped);
    			if(widthMapped) {
    				String widthProp = lineOptionHelper.getWidthProperty();
    				//mapFromCombo[0].select(widthProp);
    				selectMapFromCombo(0, widthProp);
    			}
    			
    			//  color
    			optionControls[1].setColorLabelColor(lineOptionHelper.getLineColor());
    		}
    	};
    	PlatformUI.getWorkbench().getDisplay().asyncExec(loadFieldsThread);    	

	}
	
	public void widgetDefaultSelected(SelectionEvent e){
	}
	
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		System.err.println(e);

		if(control == optionControls[0].getControl()) {
			Spinner widthSpinner = (Spinner)control;
			float w = new Float(widthSpinner.getSelection()).floatValue();
			lineOptionHelper.setLineWidth(w);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		} else if (control == optionControls[1].getControl()) {
			ColorDialog colorChooser = 
				new ColorDialog(control.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			// TODO:  add alpha support
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			lineOptionHelper.setLineColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED), false);
		} else 
			handleMappingEvent(e);
			
	}
}


