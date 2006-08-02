package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.MappingFunction;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.functions.LinearAdjustment;
import org.vast.stt.gui.widgets.OptionController;

abstract public class AdvancedOptionController extends OptionController implements SelectionListener
{
	protected Combo [] mapFromCombo;
	protected Text [] gainText;
	protected Text [] offsetText;
	protected Button [] lutButton;
	protected String [] mappableItems;
    Display display = PlatformUI.getWorkbench().getDisplay();
    
    
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
		}
	}

	protected void setOptionState(ScalarParameter scalar, int index){
		String prop = scalar.getPropertyName();
		if(scalar.isConstant() || prop == null) {  // constant
			mapFromCombo[index].select(0);  
			optionControls[index].setEnabled(true);
			gainText[index].setEnabled(false);
			offsetText[index].setEnabled(false);
			lutButton[index].setEnabled(false);
		}
		else { // scalar.isMapped() == true, option is mapped to a property
			int nameIndex = findName(mappableItems, prop);
			if(nameIndex == -1){
				System.err.println("PropertyName is" + prop + 
						"but doesn't match any mappable properties");
				mapFromCombo[index].select(0);  // constant
				optionControls[index].setEnabled(true);
			} else {
				mapFromCombo[index].select(nameIndex);
				optionControls[index].setEnabled(false);
				//  need to get mapping function and present its controls somehow
				MappingFunction func = scalar.getMappingFunction();
				if(func instanceof LinearAdjustment){
					LinearAdjustment la = (LinearAdjustment)func;
					//System.err.println("LinAdj:  Gain, Offset: " + la.getGain() + ", " + la.getOffset());
					gainText[index].setEnabled(true);
					offsetText[index].setEnabled(true);
					lutButton[index].setEnabled(false);
					gainText[index].setText(la.getGain() + "");
					offsetText[index].setText(la.getOffset() + "");
				} else {  //  mappingFunc is LUT based...
					gainText[index].setEnabled(false);
					offsetText[index].setEnabled(false);
					lutButton[index].setEnabled(true);
				}
			}
		} 
	}

	private int findName(String [] srcArr, String target){
		for(int i=0; i<srcArr.length; i++){
			if(srcArr[i].equalsIgnoreCase(target))
				return i;
		}
		return -1;
	}
	
	protected void addMappingControls(Composite parent, int index){
		mapFromCombo[index] = new Combo(parent, SWT.READ_ONLY);
		mapFromCombo[index].addSelectionListener(this);
		
		GridData gd = new GridData();
		gd.widthHint = 30;
		gainText[index] = new Text(parent,SWT.RIGHT );
		offsetText[index] = new Text(parent, SWT.RIGHT);
		gainText[index].setTextLimit(7);
		gainText[index].setLayoutData(gd);
		//  make bg gray to distinguish it from bg of parent
		gainText[index].setBackground(new Color(display, 210, 210, 210));
		gd = new GridData();
		gd.widthHint = 35;
		offsetText[index].setTextLimit(7);
		offsetText[index].setLayoutData(gd);
		//  make bg gray to distinguish it from bg of parent
		offsetText[index].setBackground(new Color(display, 210, 210, 210));

		//  set inititial vals for gain, offset
		//  set enabled state
		
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
