package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.LineStyler;

/**
 * OptionListener is a bridge between the basic controls of the StyleWidget 
 * class and the advanced controls of AdvancedStyleDialog.
 * 
 * @author tcook
 *
 */
public class OptionListener implements SelectionListener 
{
	OptionController basicController;
	AdvancedOptionController advancedController;
	
	public void widgetDefaultSelected(SelectionEvent e) {
		this.getClass();
	}
 
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] basicControls = null;
		OptionControl[] advancedControls = null;
		Combo[] mapFromCombos = null;
		DataStyler basicStyler = null;
		DataStyler advancedStyler = null;
		//  Selection can come from either a basic or advancedControl,
		//  but either basicControl or advancedControl (or both) can also be 
		//  null, so be sure to check for that
		if(basicController != null) {
			basicControls = basicController.getControls();
			basicStyler = basicController.getStyler();
		}
		if(advancedController != null) {
			advancedControls = advancedController.getControls();
			advancedStyler = advancedController.getStyler();
			mapFromCombos = advancedController.getMapFromCombos();
		}
		if(basicControls != null){
			for(int i=0; i<basicControls.length; i++){
				if(control == basicControls[i].getControl()){
					//  advancedStyler must be set to null when 
					//  dialog is closed for this to work
					if(advancedStyler == basicStyler){
						advancedControls[i].setSelection(basicControls[i]);
					}
					return;
				}
			}
		}
		if(advancedControls != null){
			for(int i=0; i<advancedControls.length; i++){
				if(control == advancedControls[i].getControl()){
					//  basicStyler must be set to null when StyleView is 
					//  closed for this to work
					if(advancedStyler == basicStyler) {
						basicControls[i].setSelection(advancedControls[i]);
					}
					return;
				}
			}
		}
		if(mapFromCombos != null){
			for(int i=0; i<mapFromCombos.length; i++){
				if(control == mapFromCombos[i]){
					boolean enabled = (mapFromCombos[i].getSelectionIndex() == 0);
					//  basicStyler must be set to null when StyleView is 
					//  closed for this to work
					if(advancedStyler == basicStyler) {
						basicControls[i].setEnabled(enabled);
						advancedControls[i].setEnabled(enabled);
					}
					return;
				}
			}
		}
	}

	public void setBasicController(OptionController loc){
		basicController = loc;
	}
	
	public void setAdvancedController(AdvancedOptionController alc){
		advancedController = alc;
	}
	
}
