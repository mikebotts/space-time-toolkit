package org.vast.stt.gui.widgets;

import org.eclipse.swt.events.SelectionListener;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.style.DataStyler;

public abstract class OptionController { //implements SelectionListener {
	protected OptionControl[] optionControls;
	protected DataStyler styler;
	//protected Symbolizer symbolizer;
	
	public OptionControl[] getControls(){
		return optionControls;
	}
	
	//  TODO:  Remove Styler (requires fix to OptionListener)
	public DataStyler getStyler(){
		return styler;
	}
	
	public void addSelectionListener(SelectionListener sl){
		for(int i=0; i<optionControls.length; i++)
			optionControls[i].addSelectionListener(sl);
	}	

	public void removeSelectionListener(SelectionListener sl){
		for(int i=0; i<optionControls.length; i++){
			optionControls[i].removeSelectionListener(sl);
		}
	}
	
}
