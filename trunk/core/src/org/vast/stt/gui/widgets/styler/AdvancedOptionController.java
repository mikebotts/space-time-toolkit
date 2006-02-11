package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.scene.DataItem;

abstract public class AdvancedOptionController extends OptionController
		implements SelectionListener {
	protected Combo [] mapFromCombo;
	protected Button [] lutButton;
	protected String [] mappableItems;
	
	abstract protected void doLut(int i);
	abstract protected void doMapping(int i);
	
	// TODO:  load these based on actual mappable items	
	public void setMappableItems(String [] items){
		this.mappableItems = new String[items.length+1];
		mappableItems[0] = "<constant>";
		for(int i=0; i<items.length; i++){
			mappableItems[i+1] = items[i];
		}
		for(int i=0; i<mapFromCombo.length; i++) {
			mapFromCombo[i].setItems(mappableItems);
			mapFromCombo[i].select(0);
		}
	}
	

	//  Always the same: MapTo Label, MapCombo, LUT Button
	protected void addMappingControls(Composite parent, int index){
		//final Label mapToLabel = new Label(parent, SWT.NONE);
		//mapToLabel.setText("Map To:");

		mapFromCombo[index] = new Combo(parent, SWT.READ_ONLY);
		//final GridData gridData = new GridData(GridData.END, GridData.CENTER, false, false);
		mapFromCombo[index].addSelectionListener(this);
		
		lutButton[index] = new Button(parent, SWT.PUSH);
		lutButton[index].setText("LUT");
		lutButton[index].addSelectionListener(this);
	}

	public void widgetDefaultSelected(SelectionEvent e){
	}
	
	/**
	 * This widgetSelected is for events coming from the additional
	 * mapping controls (LUT and Combo) on the advancedDialog.
	 * OptionControl events still handled through LineOptionHelper class.  
	 */
	public void widgetSelected(SelectionEvent e) {
		Control source = (Control)e.getSource();
		for(int i=0; i<lutButton.length; i++){
			if(source == lutButton[i]) {
				doLut(i);
				return;
			} 
			if(source == mapFromCombo[i]) {
				doMapping(i);
				return;
			}
		}
	}
	
	public Combo[] getMapFromCombos(){
		return mapFromCombo;
	}
	
	public void addSelectionListener(SelectionListener sl){
		super.addSelectionListener(sl); 
		for(int i=0; i<optionControls.length; i++) {
			mapFromCombo[i].addSelectionListener(sl);
			lutButton[i].addSelectionListener(sl);
		}
		
	}	

	public void removeSelectionListener(SelectionListener sl){
		super.addSelectionListener(sl); 
		for(int i=0; i<optionControls.length; i++){
			mapFromCombo[i].removeSelectionListener(sl);
			lutButton[i].removeSelectionListener(sl);
		}
	}
	
}
