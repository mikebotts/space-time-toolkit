package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.project.DataStyler;
import org.vast.stt.style.PointStyler;

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
		//  disable color mapFromCombo (consider just not showing this)
		mapFromCombo[1].setEnabled(false);
		addSelectionListener(pointOptionHelper);
	}

	//  Override to set the initial state of the Combos
	//  TODO:  add support for LUTS
	//  TODO:  test for mapped size, color
	//  TODO:  somehow need to mod enabled of basicControls, 
	//		   if they exist (through OptListener)
	public void setMappableItems(String [] items){
		super.setMappableItems(items);
		//  Size
		ScalarParameter sizeSP = ((PointStyler)styler).getSymbolizer().getGraphic().getSize();
		//if(sizeSP != null)
		setOptionState(sizeSP, 0);
		//  Color
		//ScalarParameter colorSP = pointOptionHelper.getPointColorScalar();
		//if(colorSP != null)
		//setOptionState(colorSP, 1);
	}
	
	protected void doLut(int index){
		System.err.println("AdvLinControl:  doLut for " + index);
	}
	
	protected void doMapping(int index){
		System.err.println("AdvLinControl:  doMapping for " + index);
		//int selIndex = mapFromCombo[index].getSelectionIndex();
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
