package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.stt.gui.widgets.OptionController;

public class BasicGridBorderController extends OptionController{
	private GridOptionHelper gridOptionHelper;
	
	public BasicGridBorderController(Composite parent, GridSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		
		gridOptionHelper = new GridOptionHelper(symbolizer);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){

	}
	
	// reset value of all controls to what is currently in symbolizer
	public void loadFields(){

	}
	
	public void widgetDefaultSelected(SelectionEvent e){
	}
	
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
	}

}
