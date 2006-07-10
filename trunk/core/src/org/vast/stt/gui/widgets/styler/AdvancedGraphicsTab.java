package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Geometry;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;
import org.vast.stt.style.PolygonStyler;

public class AdvancedGraphicsTab extends  ScrolledComposite  {

	Composite mainGroup;
	DataItem dataItem;
	AdvancedOptionController optionController;
	private OptionListener optionListener;
	String [] mappableItems;
	final Color WHITE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE);
	
	public AdvancedGraphicsTab(Composite parent, DataItem item, OptionListener ol){
		super(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.dataItem = item;
		this.optionListener = ol;
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
		gridLayout.numColumns = 5;
		mainGroup.setLayout(gridLayout);
		mainGroup.setBackground(WHITE);
	}
	
	public void setActiveStyler(DataStyler styler){
		//  Not making activeStyler a class member yet
		buildControls(styler);
	}
	
	public void buildControls(DataStyler styler){
		removeOldControls();
		addTopRow();
		if(styler instanceof PointStyler){
			optionController = new AdvancedPointController(mainGroup, (PointStyler)styler);
		} else if(styler instanceof LineStyler) {
			optionController = new AdvancedLineController(mainGroup, (LineStyler)styler);
		} else if(styler instanceof PolygonStyler){
			
		}
		
		//  can remove null check when all Stlyer types are supported
		if(optionController != null) {
			optionController.addSelectionListener(optionListener);
			optionListener.setAdvancedController(optionController);
			optionController.setMappableItems(mappableItems);
		}
		this.layout();
		this.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.redraw();
	}

	public void addTopRow(){
		//  Add Labels for top row
		Label toLabel = new Label(mainGroup, SWT.LEFT);
		Label fromLabel = new Label(mainGroup, SWT.LEFT);
		Label gainLabel = new Label(mainGroup, SWT.LEFT);
		Label offsetLabel = new Label(mainGroup, SWT.LEFT);
		Label lutLabel = new Label(mainGroup, SWT.LEFT);
		toLabel.setText("Map To:");
		fromLabel.setText("MapFrom:");
		gainLabel.setText("Gain");
		offsetLabel.setText("Offset");
		lutLabel.setText("");
		toLabel.setBackground(WHITE);
		fromLabel.setBackground(WHITE);
		gainLabel.setBackground(WHITE);
		offsetLabel.setBackground(WHITE);
		lutLabel.setBackground(WHITE);
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
		optionListener.setAdvancedController(null);
	}

}
