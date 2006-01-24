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
import org.vast.ows.sld.Stroke;
import org.vast.stt.style.LineStyler;

public class LineOptionController implements OptionController {

	private Composite parent;
	private LineStyler styler;
	private OptionControl[] optionControl;
	private Spinner widthSpinner;
	private Button colorButton;
	
	public LineOptionController(Composite parent, LineStyler styler){
		this.parent = parent;
		this.styler = styler;
		buildBasicOptions();
	}

	public void buildAdvancedOptions() {
		// TODO Auto-generated method stub
		
	}

	public void buildBasicOptions() {
		// TODO Auto-generated method stub
		//optionControl = new OptionControl[7];
		optionControl = new OptionControl[2];
		optionControl[0] = new OptionControl(parent);
		widthSpinner = optionControl[0].createSpinner("LineWidth:", 1, 10);
		widthSpinner.addSelectionListener(this);
			
		optionControl[1] = new OptionControl(parent);
		colorButton = optionControl[1].createButton("Line Color:", "...");
		colorButton.addSelectionListener(this);

		//  add a bunch of controls to test scrolling
//		optionControl[2] = new OptionControl(optComp);
//		optionControl[2].createCombo("Line Test:", new String [] { "aaa", "bbb", "ccc" } );
//		optionControl[3] = new OptionControl(optComp);
//		optionControl[3].createSpinner("LineWidth:", 1, 10);
//		optionControl[4] = new OptionControl(optComp);
//		optionControl[4].createSpinner("LineWidth:", 1, 10);
//		optionControl[5] = new OptionControl(optComp);
//		optionControl[5].createSpinner("LineWidth:", 1, 10);
//		optionControl[6] = new OptionControl(optComp);
//		optionControl[6].createSpinner("LineWidth:", 1, 10);
		
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		Control control = (Control)e.getSource();
		if(control == widthSpinner) {
			Stroke stroke = styler.getSymbolizer().getStroke();
			ScalarParameter width = new ScalarParameter();
			width.setConstantValue(new Float(widthSpinner.getSelection()));
			stroke.setWidth(width);
			styler.updateDataMappings();
		} else if (control == colorButton){
			ColorDialog colorChooser = new ColorDialog(colorButton.getShell());
			RGB rgb = colorChooser.open();
			Color color = new Color(rgb.red, rgb.green, rgb.blue, 255);
			Stroke stroke = styler.getSymbolizer().getStroke();
			ScalarParameter newColor = new ScalarParameter();
			newColor.setConstantValue(color);
			stroke.setColor(newColor);
			styler.updateDataMappings();
		}
	}

}
