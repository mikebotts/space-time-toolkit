package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.gui.widgets.OptionControl;
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
public class AdvancedLineController extends AdvancedOptionController { 
	//implements SelectionListener {
	private Composite parent;
	private LineOptionHelper lineOptionHelper;

	public AdvancedLineController(Composite parent, LineStyler styler){
		this.parent = parent;
		this.styler = styler;
		
		lineOptionHelper = new LineOptionHelper(this);
		buildControls();
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
	
	//  Override to set the initial state of the Combos
	//  TODO:  add support for LUTS
	//  TODO:  test for mapped size, color
	//  TODO:  somehow need to mod enabled of basicControls, 
	//		   if they exist (through OptListener)
	public void setMappableItems(String [] items){
		super.setMappableItems(items);
		//  Width
		ScalarParameter widthSP = ((LineStyler)styler).getSymbolizer().getStroke().getWidth();
		if(widthSP != null)  //  is null sometimes.
			setOptionState(widthSP, 0);
		//  Color
		ScalarParameter colorSP = ((LineStyler)styler).getSymbolizer().getStroke().getColor();		
		if(colorSP != null)
			setOptionState(colorSP, 1);
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
}
