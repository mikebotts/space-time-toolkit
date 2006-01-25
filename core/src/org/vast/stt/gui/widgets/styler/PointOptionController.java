package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.style.PointStyler;

public class PointOptionController implements OptionController{
	
	private Composite parent;
	private PointStyler styler;
	private OptionControl[] optionControl;
	private Spinner sizeSpinner;
	private Button colorButton;
	
	public PointOptionController(Composite parent, PointStyler styler){
		this.parent = parent;
		this.styler = styler;
		buildBasicOptions();
	}
	
	public void buildAdvancedOptions() {
		// TODO Auto-generated method stub
		
	}

	public void buildBasicOptions() {
		// TODO Auto-generated method stub
		optionControl = new OptionControl[2];
		optionControl[0] = new OptionControl(parent);
		sizeSpinner = optionControl[0].createSpinner("Point Size:", 1,10);

		optionControl[1] = new OptionControl(parent);
//		ScalarParameter colorSP = styler.getSymbolizer().get
//		Color sldColor;
//		if(colorSP != null) 
//			sldColor = (Color)colorSP.getConstantValue();
//		else 
//			sldColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);
		//colorButton = optionControl[1].createColorButton("Point Color:", sldColor);
		colorButton = optionControl[1].createButton("Point Color:", "...");
		colorButton.addSelectionListener(this);
	}
	
//	public void turnOnControls(){
//		for(int i=0; i<optionControl.length; i++)
//			optionControl[i].setVisible(true);
//	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		//  uncomment when Stylers are working
		if(control == sizeSpinner) {
	//		Graphic graphic = styler.getSymbolizer().getGraphic();
	//		ScalarParameter size = new ScalarParameter();
	//		size.setConstantValue(new Integer(spinner.getSelection()));
	//		graphic.setSize(new ScalarParameter());
		} else if(control == colorButton) {
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			//  uncomment when Stylers are working
//			PointStyler styler = (PointStyler)button.getData();
//			Graphic graphic = styler.getSymbolizer().getGraphic();
//			List graphicSourceList = graphic.getImages();
//			if(graphicSourceList == null)  return;
//			GraphicSource graphicSource = (GraphicSource)graphicSourceList.get(0);
//			if(graphicSource instanceof GraphicMark) {
//				GraphicMark gm = (GraphicMark)graphicSource;
//				ScalarParameter newColor = new ScalarParameter();
//				newColor.setConstantValue(rgb);
//				gm.getFill().setColor(newColor);
//			}
		}
	}

}
