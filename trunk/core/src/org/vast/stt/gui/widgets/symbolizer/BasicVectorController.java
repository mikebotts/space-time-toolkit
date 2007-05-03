package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.VectorSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;


/**
 * <p><b>Title:</b>
 * Basic Line Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds basic Vector controls for StyleWidget
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class BasicVectorController extends OptionController // implements SelectionListener
{
	private VectorOptionHelper vectorOptionHelper;
	
	public BasicVectorController(Composite parent, VectorSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		
		vectorOptionHelper = new VectorOptionHelper(symbolizer);
		buildControls(parent);
	}

    public void buildControls(Composite parent){
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.SPINNER, "Line Width:", new int[] {1, 10}),	
			//new OptionParams(OptionControl.ControlType.TEXT, "Som text:",	"blah"),
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Line Color:",	null)
		};
		optionControls = OptionControl.createControls(parent, params);
		loadFields();
		addSelectionListener(this);
	}
	
	// reset value of all controls to what is currently in symbolizer
	public void loadFields(){
		Spinner widthSpinner = (Spinner)optionControls[0].getControl();
		widthSpinner.setSelection((int)vectorOptionHelper.getLineWidth());
		optionControls[1].setColorLabelColor(vectorOptionHelper.getLineColor());
    }
	
	public void widgetDefaultSelected(SelectionEvent e){
	}
	
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		if(control == optionControls[0].getControl()) {
			Spinner widthSpinner = (Spinner)control;
			float w = new Float(widthSpinner.getSelection()).floatValue();
			vectorOptionHelper.setLineWidth(w);
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
			vectorOptionHelper.setLineColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		}
	}
	
}