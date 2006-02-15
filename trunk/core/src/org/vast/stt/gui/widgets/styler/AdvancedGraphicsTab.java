package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Geometry;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.scene.DataItem;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;

public class AdvancedGraphicsTab extends Composite {

	Composite parent;
	DataItem dataItem;
	AdvancedOptionController optionController;
	private OptionListener optionListener;
	String [] mappableItems;
	final Color WHITE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE);
	
	public AdvancedGraphicsTab(Composite parent, DataItem item, OptionListener ol){
		super(parent, SWT.BORDER);
		this.parent = parent;
		this.dataItem = item;
		this.optionListener = ol;
		init();
	}
	
	public void init(){
		this.setBackground(WHITE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.setLayout(gridLayout);
	}
	
	public void setActiveStyler(DataStyler styler){
		//  Not making activeStyler a class member yet
		buildControls(styler);
	}
	
	public void buildControls(DataStyler styler){
		removeOldControls();
		addTopRow();
		if(styler instanceof PointStyler){
			optionController = new AdvancedPointController(this, (PointStyler)styler);
			optionController.addSelectionListener(optionListener);
			optionListener.setAdvancedController(optionController);
		} else if(styler instanceof LineStyler) {
			optionController = new AdvancedLineController(this, (LineStyler)styler);
			optionController.addSelectionListener(optionListener);
			optionListener.setAdvancedController(optionController);
		} else {
			
		}
		optionController.setMappableItems(mappableItems);
		
		this.layout();
		this.redraw();
	}

	public void addTopRow(){
		//  Add Labels for top row
		Label toLabel = new Label(this, SWT.LEFT);
		Label fromLabel = new Label(this, SWT.LEFT);
		Label lutLabel = new Label(this, SWT.LEFT);
		toLabel.setText("Map To:");
		fromLabel.setText("MapFrom:");
		lutLabel.setText("");
		toLabel.setBackground(WHITE);
		fromLabel.setBackground(WHITE);
		lutLabel.setBackground(WHITE);
	}

	public void setMappableItems(String [] items){
		mappableItems = items;
	}

	public void removeOldControls(){
		Control [] controls = this.getChildren();
		for(int i=0; i<controls.length; i++){
			controls[i].dispose();
			controls[i] = null;
		}
	}
	
	public void close(){
		optionListener.setAdvancedController(null);
	}

}
