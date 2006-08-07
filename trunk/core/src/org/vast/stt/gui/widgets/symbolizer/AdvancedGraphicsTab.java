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
import org.vast.stt.project.DataItem;
import org.vast.ows.sld.*;


public class AdvancedGraphicsTab extends ScrolledComposite  {

	Composite mainGroup;
	DataItem dataItem;
	AdvancedOptionController optionController;
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
		if(optionController!=null)
			dataItem.removeListener(optionController);
		if(symbolizer instanceof PointSymbolizer){
			optionController = new AdvancedPointController(mainGroup, dataItem, (PointSymbolizer)symbolizer);
		} else if(symbolizer instanceof LineSymbolizer) {
			optionController = new AdvancedLineController(mainGroup,  dataItem, (LineSymbolizer)symbolizer);
		} else if(symbolizer instanceof PolygonSymbolizer){
			
		}
		
		//  can remove null check when all Stlyer types are supported
		if(optionController != null) {
			optionController.setMappableItems(mappableItems);
			dataItem.addListener(optionController);

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
	}

}
