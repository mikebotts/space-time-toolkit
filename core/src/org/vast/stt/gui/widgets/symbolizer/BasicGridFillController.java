package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.gui.widgets.OptionParams;

public class BasicGridFillController extends OptionController {
	
	private GridOptionHelper gridOptionHelper;
	
	public BasicGridFillController(Composite parent, GridSymbolizer symbolizer){
		this.symbolizer = symbolizer;
		
		gridOptionHelper = new GridOptionHelper(symbolizer);
		buildControls(parent);
	}
	
	public void buildControls(Composite parent){
		OptionParams[] params = 
		{
			new OptionParams(OptionControl.ControlType.COLOR_BUTTON, "Fill Color:",	null),
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Fill Opacity:", "1.0")	
		};
		optionControls = OptionControl.createControls(parent, params);
		loadFields();
		addSelectionListener(this);
	}
	
	// reset value of all controls to what is currently in symbolizer
	public void loadFields(){
		optionControls[0].setColorLabelColor(gridOptionHelper.getGridFillColor());
		//  TODO support opacity
		((Text)optionControls[1].getControl()).setText(gridOptionHelper.getGridFillOpacity() + "");
	}
	
	//  Enter key in Text widget triggers widgetDefaultSelected instead of WidgetSelected.
	//  Go figure....
	public void widgetDefaultSelected(SelectionEvent e){
		Control control = (Control)e.getSource();
		
		if (control == optionControls[1].getControl()) {
			Text opText = (Text)control;
			String val = opText.getText();
			float fval = Float.parseFloat(val);
			if(fval > 1.0) {
				opText.setText("1.0");
				fval = 1.0f;
			} else if(fval < 0.0) {
				opText.setText("0.0");
				fval = 0.0f;
			}
			gridOptionHelper.setGridFillOpacity(fval);
			dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		}
	}
	
	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();

		if(control == optionControls[0].getControl()) {
			ColorDialog colorChooser = new ColorDialog(control.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			// TODO:  add alpha support
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControls[0].setColorLabelColor(sldColor); 
			gridOptionHelper.setGridFillColor(sldColor);
            dataItem.dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		}
	}
}
