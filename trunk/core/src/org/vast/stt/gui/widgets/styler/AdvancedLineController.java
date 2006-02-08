package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.LineStyler;

/**
 * <p><b>Title:</b><br/>
 * AdvancedLineController
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Feb 6, 2006
 * @version 1.0
 * 
 */
public class AdvancedLineController extends OptionController 
	implements SelectionListener {
	private Composite parent;
	private LineOptionHelper lineOptionHelper;
	private Combo [] mapFromCombo;
	private Button [] lutButton;
	
	public AdvancedLineController(Composite parent, LineStyler styler){
		this.parent = parent;
		this.styler = styler;
		
		lineOptionHelper = new LineOptionHelper(this);
		buildControls();
		setMappableItems(null);
	}
	
	
	public void buildControls(){
		optionControls = new OptionControl[2];
		mapFromCombo = new Combo[2];
		lutButton = new Button[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		Spinner advWidthSpinner = optionControls[0].createSpinner("LineWidth:", 1, 10);
		advWidthSpinner.setSelection((int)lineOptionHelper.getLineWidth());
		//  add other controls
		addMappingControls(parent, 0);
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createColorButton("Line Color:", lineOptionHelper.getLineColor());

		addMappingControls(parent, 1);

		addSelectionListener(lineOptionHelper);
	}
	
	//  Always the same: MapTo Label, MapCombo, LUT Button
	private void addMappingControls(Composite parent, int index){
		mapFromCombo[index] = new Combo(parent, SWT.READ_ONLY);
		final GridData gridData = new GridData(GridData.END, GridData.CENTER, true, false);
		gridData.widthHint = 53;
		mapFromCombo[index].addSelectionListener(this);

		lutButton[index] = new Button(parent, SWT.PUSH);
		lutButton[index].setText("LUT");
		lutButton[index].addSelectionListener(this);
	}

	// TODO:  load these based on actual mappable items	
	public void setMappableItems(String [] _items){
		String [] items = { "<constant>", "lat", "lon", "alt" }; 
		for (int i=0; i<mapFromCombo.length; i++) {
			mapFromCombo[i].setItems(items);
			mapFromCombo[i].select(0);
		}
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
	
	private void doLut(int index){
		System.err.println("AdvLinControl:  doLut for " + index);
	}
	
	private void doMapping(int index){
		System.err.println("AdvLinControl:  doMapping for " + index);
		int selIndex = mapFromCombo[index].getSelectionIndex();
		if(selIndex == 0) {
			optionControls[index].setEnabled(true);
		} else {
			optionControls[index].setEnabled(false);
		}
	}
}
