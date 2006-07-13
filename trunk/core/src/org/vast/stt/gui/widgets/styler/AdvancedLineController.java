package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.gui.widgets.OptionControl;


/**
 * <p><b>Title:</b>
 * Advanced Line Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds advanced Line controls for Advanced Dialog
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class AdvancedLineController extends AdvancedOptionController
{ 
	private Composite parent;
	private LineOptionHelper lineOptionHelper;

	public AdvancedLineController(Composite parent, LineSymbolizer symbolizer){
		this.parent = parent;
		this.symbolizer = symbolizer;
		
		lineOptionHelper = new LineOptionHelper(this);
		buildControls();
	}
	
	public void buildControls(){
		optionControls = new OptionControl[2];
		mapFromCombo = new Combo[2];
		gainText = new Text[2];
		offsetText = new Text[2];
		lutButton = new Button[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		Spinner advWidthSpinner = optionControls[0].createSpinner("LineWidth:", 1, 10);
		advWidthSpinner.setSelection((int)lineOptionHelper.getLineWidth());
		//  add other controls
		addMappingControls(parent, 0);
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createColorButton("Line Color:", lineOptionHelper.getLineColor());

		addMappingControls(parent, 1);
		mapFromCombo[1].setEnabled(false);
		addSelectionListener(lineOptionHelper);
	}
	
	//  Override to set the initial state of the Combos
	//  TODO:  add support for LUTS
	//  TODO:  test for mapped size, color
	public void setMappableItems(String [] items){
		super.setMappableItems(items);
		//  Width
		ScalarParameter widthSP = ((LineSymbolizer)symbolizer).getStroke().getWidth();
		if(widthSP != null)  //  is null sometimes.
			setOptionState(widthSP, 0);
		//  Color
		ScalarParameter redSP = ((LineSymbolizer)symbolizer).getStroke().getColor().getRed();		
		if(redSP != null)
			setOptionState(redSP, 1);
        ScalarParameter greenSP = ((LineSymbolizer)symbolizer).getStroke().getColor().getGreen();       
        if(redSP != null)
            setOptionState(greenSP, 1);
        ScalarParameter blueSP = ((LineSymbolizer)symbolizer).getStroke().getColor().getBlue();       
        if(redSP != null)
            setOptionState(blueSP, 1);
        ScalarParameter alphaSP = ((LineSymbolizer)symbolizer).getStroke().getColor().getAlpha();       
        if(redSP != null)
            setOptionState(alphaSP, 1);
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
