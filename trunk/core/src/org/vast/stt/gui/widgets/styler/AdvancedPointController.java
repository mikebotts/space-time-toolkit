package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.scene.DataItem;
import org.vast.stt.style.DataStyler;

public class AdvancedPointController extends AdvancedOptionController 
	implements SelectionListener {
	private Composite parent;
	private PointOptionHelper pointOptionHelper;
	
	public AdvancedPointController(Composite parent, DataStyler styler){
		this.parent = parent;
		this.styler = styler;
		
		pointOptionHelper = new PointOptionHelper(this);
		buildControls();
	}
	
	
	public void buildControls(){
		optionControls = new OptionControl[2];
		mapFromCombo = new Combo[2];
		lutButton = new Button[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		Spinner advWidthSpinner = optionControls[0].createSpinner("Point Size:", 1, 10);
		advWidthSpinner.setSelection((int)pointOptionHelper.getPointSize());
		//  add other controls
		addMappingControls(parent, 0);
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createColorButton("Point Color:", pointOptionHelper.getPointColor());

		addMappingControls(parent, 1);

		addSelectionListener(pointOptionHelper);
	}
	
	protected void doLut(int index){
		System.err.println("AdvLinControl:  doLut for " + index);
	}
	
	protected void doMapping(int index){
		System.err.println("AdvLinControl:  doMapping for " + index);
		int selIndex = mapFromCombo[index].getSelectionIndex();
		switch(index){
		case 0:  // set lineWidth 
			break;
		case 1:
			break;  // set lineColor
		default:
			break;
		}
	}		
//	public void widgetDefaultSelected(SelectionEvent e){
//	}
//	
//	/**
//	 * This widgetSelected is for events coming from the additional
//	 * mapping controls (LUT and Combo) on the advancedDialog.
//	 * OptionControl events still handled through LineOptionHelper class.  
//	 */
//	public void widgetSelected(SelectionEvent e) {
//		System.err.println(e);
//	}
}
