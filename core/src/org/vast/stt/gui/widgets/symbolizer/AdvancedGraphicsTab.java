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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.project.tree.DataItem;
import org.vast.ows.sld.*;


public class AdvancedGraphicsTab extends ScrolledComposite  {

	Composite mainGroup;
	DataItem dataItem;
	protected AdvancedOptionController optionController;
	String [] mappableItems;
	Symbolizer activeSymbolizer;
	final Color WHITE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE);
	
	public AdvancedGraphicsTab(Composite parent, DataItem item){
		super(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.dataItem = item;
		init();
	}
	
	public void init(){
		this.setExpandVertical(true);
		this.setExpandHorizontal(true);
		//  ??
		this.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		this.setBackground(WHITE);

	    mainGroup = new Composite(this, 0x0);
		this.setContent(mainGroup);
			
		final GridLayout gridLayout = new GridLayout();
		//  NOTE:  Should really be 4, but contents aren't rendered initially with 4 columns-
		//         see hack @ bottom of setActiveSymb()
		gridLayout.numColumns = 5;
		mainGroup.setLayout(gridLayout);
		mainGroup.setBackground(WHITE);
	}
	
	public void setActiveSymbolizer(Symbolizer symbolizer){
		if(symbolizer == activeSymbolizer)
			return;  // doNothing (user just re-selected the same entry)
		
		activeSymbolizer = symbolizer;
		
		removeOldControls();
		addTopRow();
		if(optionController!=null) {
			optionController.removeSelectionListener(optionController);
		}
		if(symbolizer instanceof PointSymbolizer){
			optionController = new AdvancedPointController(mainGroup, dataItem, (PointSymbolizer)symbolizer);
		} else if(symbolizer instanceof LineSymbolizer) {
			optionController = new AdvancedLineController(mainGroup,  dataItem, (LineSymbolizer)symbolizer);
		} else if(symbolizer instanceof PolygonSymbolizer){
			
		}
		
		//  can remove null check when all Stlyer types are supported
		if(optionController != null) {
			optionController.setMappableItems(mappableItems);
		}
		this.layout();
		this.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.redraw();	
		//  NOTE:  Here is hack.  I reset layout to 4 columns and redraw, and it works.
		//         This is due to a bug in SWT ScrolledComp, I think.  TC
		GridLayout gridLayout = (GridLayout)mainGroup.getLayout();
		gridLayout.numColumns = 4;
		mainGroup.setLayout(gridLayout);
		this.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.layout();
	}

	public void addTopRow(){
		//  Add Labels for top row
		Label isMappedLabel = new Label(mainGroup, SWT.LEFT);
		Label editMappingLabel = new Label(mainGroup, SWT.LEFT);
		Label toLabel = new Label(mainGroup, SWT.LEFT);
		Label fromLabel = new Label(mainGroup, SWT.LEFT);

		isMappedLabel.setText("Map    ");
		editMappingLabel.setText("Edit  ");
		toLabel.setText("Map To:");
		fromLabel.setText("MapFrom:");
		
		isMappedLabel.setBackground(WHITE);
		editMappingLabel.setBackground(WHITE);
		toLabel.setBackground(WHITE);
		fromLabel.setBackground(WHITE);
	}

	public void setMappableItems(String [] items){
		mappableItems = items;
	}

	public void removeOldControls(){
		Control [] controls = mainGroup.getChildren();
		for(int i=0; i<controls.length; i++){
			controls[i].dispose();
			controls[i] = null;
		}
	}
	
	public void close(){
	}

}
