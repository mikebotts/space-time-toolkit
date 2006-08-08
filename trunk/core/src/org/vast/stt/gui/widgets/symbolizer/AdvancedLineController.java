package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.project.DataItem;


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

	public AdvancedLineController(Composite parent, DataItem item, LineSymbolizer symbolizer){
		super(item);
		this.parent = parent;
		this.symbolizer = symbolizer;
		
		lineOptionHelper = new LineOptionHelper(symbolizer);
		buildControls();
		addSelectionListener(this);
	}
	
	//  These controls differ from basic in that they have 3 channels for color
	public void buildControls(){
		optionControls = new OptionControl[2];
		mapFromCombo = new Combo[2];
		gainText = new Text[2];
		offsetText = new Text[2];
		lutButton = new Button[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		optionControls[0].createSpinner("LineWidth:", 1, 10);
		
		//  add other controls
		addMappingControls(parent, 0);
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createColorButton("Line Color:", lineOptionHelper.getLineColor());

		addMappingControls(parent, 1);
		mapFromCombo[1].setEnabled(false);
		//  set initial state
		loadFields();
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
	
    @Override
    // reset value of all controls to what is currently in symbolizer
	public void loadFields(){
		Spinner widthSpinner = (Spinner)optionControls[0].getControl();
		widthSpinner.setSelection((int)lineOptionHelper.getLineWidth());
		optionControls[1].setColorLabelColor(lineOptionHelper.getLineColor());
	}
	
	public void widgetDefaultSelected(SelectionEvent e){
	}
	
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		if(control == optionControls[0].getControl()) {
			Spinner widthSpinner = (Spinner)control;
			float w = new Float(widthSpinner.getSelection()).floatValue();
			lineOptionHelper.setLineWidth(w);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		} else if (control == optionControls[1].getControl()) {
			ColorDialog colorChooser = 
				new ColorDialog(control.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			// TODO:  add alpha support
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[1].setColorLabelColor(sldColor); 
			lineOptionHelper.setLineColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		}
	}
}


