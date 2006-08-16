package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;

public class BasicGridMeshController extends OptionController {
	private GridOptionHelper gridOptionHelper;
	
	public BasicGridMeshController(Composite parent, GridSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		
		gridOptionHelper = new GridOptionHelper(symbolizer);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.SPINNER, "Mesh Width:", new int[] {1, 10}),	
			//new OptionParams(OptionControl.ControlType.TEXT, "Som text:",	"blah"),
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Mesh Color:",	null),
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Mesh Opacity:", "1.0")	
		};
		optionControls = OptionControl.createControls(parent, params);
		loadFields();
		addSelectionListener(this);
	}
	
	// reset value of all controls to what is currently in symbolizer
	public void loadFields(){
		//  TODO check for mapping
		Spinner widthSpinner = (Spinner)optionControls[0].getControl();
		widthSpinner.setSelection((int)gridOptionHelper.getGridMeshWidth());
		optionControls[1].setColorLabelColor(gridOptionHelper.getGridMeshColor());
		//  TODO support mesh opacity
	}
	
	public void widgetDefaultSelected(SelectionEvent e){
	}
	
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		if(control == optionControls[0].getControl()) {
			Spinner widthSpinner = (Spinner)control;
			float w = new Float(widthSpinner.getSelection()).floatValue();
			gridOptionHelper.setGridMeshWidth(w);
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
			gridOptionHelper.setGridMeshColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		} else {
			//TODO:  support mesh opacity
		}
	}
	
}
