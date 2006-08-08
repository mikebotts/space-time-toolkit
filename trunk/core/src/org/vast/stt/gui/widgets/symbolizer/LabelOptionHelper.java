package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.Fill;
import org.vast.ows.sld.Font;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.TextSymbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;


public class LabelOptionHelper implements SelectionListener {

	OptionController optionController;
	TextSymbolizer symbolizer;

	public LabelOptionHelper(OptionController loc){
		optionController = loc;
		//  styler must not change for this to work
		symbolizer = (TextSymbolizer)optionController.getSymbolizer();
	}
	
	public Color getLabelColor(){
		Fill fill = symbolizer.getFill();
		if(fill == null) {
			System.err.println("Fill is NULL.  Do what now?");
			return null;
		}
		Color fillColor = fill.getColor();
		ScalarParameter redSP = fillColor.getRed();
		ScalarParameter greenSP = fillColor.getGreen();
		ScalarParameter blueSP = fillColor.getBlue();
		ScalarParameter alphaSP = fillColor.getAlpha();
		
		if(!redSP.isConstant() || !greenSP.isConstant() || !blueSP.isConstant() || !alphaSP.isConstant()) {
			System.err.println("At least one FillColor channel is mapped.  Do what now?");
			return new Color(0.5f, 0.0f, 0.0f, 1.0f);		
		}
		
		return fillColor;
	}
	
	private void setLabelColor(Color c){
		Fill fill = symbolizer.getFill();
		if(fill == null) {
			System.err.println("Fill is NULL.  Do what now?");
			return;
		}
		fill.setColor(c);
	}
		
	private void setFont(FontData fontData) {
		System.err.println("Font is: " + 
				fontData.getName() + "," + fontData.getHeight() + ", " + fontData.getStyle());
		
		//  TODO  map fontData to SLD Font parameters
		//   Need to get available system fonts from JOGL
		//  also may need to know how GL uses font atts
		
		Font newFont = new Font();
		//newFont.setFamily();
        symbolizer.setFont(newFont);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		OptionControl[] optionControl = optionController.getControls();

		if(control == optionControl[0].getControl()) {  //  Label Text

		} else if (control == optionControl[1].getControl()) {  //  Label Font
			FontDialog fontChooser = new FontDialog(control.getShell());
			FontData fontData = fontChooser.open();
			if(fontData == null)
				return;
			setFont(fontData);
            optionController.getDataItem().dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		} else if (control == optionControl[2].getControl()) { // Label Color
			ColorDialog colorChooser = new ColorDialog(control.getShell());
			RGB rgb = colorChooser.open();
			if(rgb == null)
				return;
			// TODO:  add alpha support
			Color sldColor = new Color(rgb.red, rgb.green, rgb.blue, 255);
			optionControl[2].setColorLabelColor(sldColor); 
			setLabelColor(sldColor);
            optionController.getDataItem().dispatchEvent(new STTEvent(symbolizer, EventType.ITEM_SYMBOLIZER_CHANGED));
		}
	}
}
