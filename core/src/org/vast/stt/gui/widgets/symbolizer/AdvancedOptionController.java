package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.project.DataItem;

abstract public class AdvancedOptionController extends OptionController 
	implements STTEventListener
{
	protected Combo [] mapFromCombo;
	//protected Text [] gainText;
	//protected Text [] offsetText;
	protected Button [] editMappingBtn;
	protected Button [] isMappedBtn;
	protected String [] mappableItems;
    Display display = PlatformUI.getWorkbench().getDisplay();
    
	abstract protected void isMapped(int i, boolean b);
	abstract protected void editMapping(int i);
	abstract protected void mapFrom(int i, int index);
	
	public AdvancedOptionController(DataItem item){
		this.dataItem = item;
	}
	
	public void initMappingControls(int dim){
		optionControls = new OptionControl[dim];
		mapFromCombo = new Combo[dim];
		editMappingBtn = new Button[dim];
		isMappedBtn = new Button[dim];
	}
	
	// TODO: load these based on actual mappable items
	public void setMappableItems(String [] items){
		for(int i=0; i<mapFromCombo.length; i++) {
			mapFromCombo[i].setItems(items);
		}
	}

	protected void setOptionState(ScalarParameter scalar, int index){
//		String prop = scalar.getPropertyName();
//		if(scalar.isConstant() || prop == null) {  // constant
//			mapFromCombo[index].select(0);  
//			optionControls[index].setEnabled(true);
//			mappingBtn[index].setEnabled(false);
//		}
//		else { // scalar.isMapped() == true, option is mapped to a property
//			int nameIndex = findName(mappableItems, prop);
//			if(nameIndex == -1){
//				System.err.println("PropertyName is" + prop + 
//						"but doesn't match any mappable properties");
//				mapFromCombo[index].select(0);  // constant
//				optionControls[index].setEnabled(true);
//			} else {
//				mapFromCombo[index].select(nameIndex);
//				optionControls[index].setEnabled(false);
//				//  need to get mapping function and present its controls somehow
//				MappingFunction func = scalar.getMappingFunction();
//				if(func instanceof LinearAdjustment){
//					LinearAdjustment la = (LinearAdjustment)func;
//					//System.err.println("LinAdj:  Gain, Offset: " + la.getGain() + ", " + la.getOffset());
//					gainText[index].setEnabled(true);
//					offsetText[index].setEnabled(true);
//					mappingBtn[index].setEnabled(false);
//					gainText[index].setText(la.getGain() + "");
//					offsetText[index].setText(la.getOffset() + "");
//				} else {  //  mappingFunc is LUT based...
//					gainText[index].setEnabled(false);
//					offsetText[index].setEnabled(false);
//					mappingBtn[index].setEnabled(true);
//				}
//			}
//		} 
	}

	protected void addMappingButtons(Composite parent, int index){
		isMappedBtn[index] = new Button(parent, SWT.CHECK);
		isMappedBtn[index].setToolTipText("Map/Unmap this drawable");
		//isMappedBtn[index].addSelectionListener(this);
		//isMappedBtn[index].setSelection(dataItem.getDataProvider().)
		
		editMappingBtn[index] = new Button(parent, SWT.PUSH);
		editMappingBtn[index].setText("Edit");
		editMappingBtn[index].setToolTipText("Edit Mapping Function");
		//editMappingBtn[index].addSelectionListener(this);
	}
	
	protected void addMapFromCombos(Composite parent, int index){
		mapFromCombo[index] = new Combo(parent, SWT.READ_ONLY);
		//mapFromCombo[index].addSelectionListener(this);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		mapFromCombo[index].setLayoutData(gd);
		
	}
/*
	public void widgetDefaultSelected(SelectionEvent e){
	}
	*/
	
	/**
	 * This widgetSelected is for events coming from the additional
	 * mapping controls (LUT and Combo) on the advancedDialog.
	 * All graphic options are handled as STTEvents  
	 */
	public void handleMappingEvent(SelectionEvent e) {
		Control source = (Control)e.getSource();
		for(int i=0; i<isMappedBtn.length; i++){
			if(source == isMappedBtn[i]) {
				isMapped(i, isMappedBtn[i].getSelection());
				return;
			} 
			if(source == mapFromCombo[i]) {
				mapFrom(i, mapFromCombo[i].getSelectionIndex());
				return;
			}
			if(source == editMappingBtn[i]){
				editMapping(i);
			}
		}
	}
	

	public void addSelectionListener(SelectionListener sl){
		super.addSelectionListener(sl); 
		for(int i=0; i<optionControls.length; i++) {
			isMappedBtn[i].addSelectionListener(sl);
			editMappingBtn[i].addSelectionListener(sl);
			mapFromCombo[i].addSelectionListener(sl);
		}
		
	}	

	public void removeSelectionListener(SelectionListener sl){
		super.addSelectionListener(sl); 
		for(int i=0; i<optionControls.length; i++){
			isMappedBtn[i].addSelectionListener(sl);
			editMappingBtn[i].removeSelectionListener(sl);
			mapFromCombo[i].removeSelectionListener(sl);
		}
	}

	// ============ NEW EVENT CODE
	//abstract void loadFields();
	
	public void handleEvent(STTEvent e) {       
        switch (e.type) {
            case ITEM_OPTIONS_CHANGED:
            case ITEM_SYMBOLIZER_CHANGED:
            	loadFields();
        }  
	 }
		
}
