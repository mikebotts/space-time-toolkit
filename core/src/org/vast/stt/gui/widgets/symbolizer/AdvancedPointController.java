package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.project.DataItem;


/**
 * <p><b>Title:</b>
 * Advanced Point Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds advanced Point controls for Advanced Dialog
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class AdvancedPointController extends AdvancedOptionController// implements SelectionListener
{
	private Composite parent;
	private PointOptionHelper pointOptionHelper;
	
	public AdvancedPointController(Composite parent, DataItem item, PointSymbolizer symbolizer){
		super(item);
		this.parent = parent;
		this.symbolizer = symbolizer;
		
		pointOptionHelper = new PointOptionHelper(this);
		buildControls();
		addSelectionListener(this);
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

		loadFields();
	}

	//  Override to set the initial state of the Combos
	//  TODO:  add support for LUTS
	//  TODO:  test for mapped size, color
	//  TODO:  somehow need to mod enabled of basicControls, 
	//		   if they exist (through OptListener)
	public void setMappableItems(String [] items){
		super.setMappableItems(items);
		//  Size
		ScalarParameter sizeSP = ((PointSymbolizer)symbolizer).getGraphic().getSize();
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

	//==============
	//=============
    @Override
    public void loadFields(){
    	//  Redraw ALL graphics options
    	System.err.println("REDRAW Adv Pts");
    }

    public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		if(control == optionControls[0].getControl()) {
			Spinner sizeSpinner = (Spinner)control;
			float size = (float)sizeSpinner.getSelection();
			pointOptionHelper.setPointSize(size);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		} else if(control == optionControls[1].getControl()) {
			Button colorButton = (Button)control;
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			pointOptionHelper.setPointColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		}
	}

}