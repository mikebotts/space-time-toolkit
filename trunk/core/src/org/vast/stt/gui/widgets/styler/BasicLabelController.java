package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.LabelStyler;

public class BasicLabelController extends OptionController implements KeyListener  
{
	private Composite parent;
	private LabelOptionHelper labelOptionHelper;
	
	public BasicLabelController(Composite parent, LabelStyler styler){
		this.parent = parent;
		this.styler = styler;

		labelOptionHelper = new LabelOptionHelper(this);
		buildControls();
	}
	
	public void buildControls() {
		// TODO populate widgets with current Font's values
		optionControls = new OptionControl[3];
		
		optionControls[0] = new OptionControl(parent, 0x0);
		optionControls[0].createText("Label Text:", "dummy");
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createButton("Font:", "FontName");
		
		optionControls[2] = new OptionControl(parent, 0x0);
		optionControls[2].createColorButton("Font Color", labelOptionHelper.getLabelColor());
		
		//  ARE we going to provide user control of these params?
		// rotation
		
		//  anchor x,y
		
		//  displacement x,y
		
		addSelectionListener(labelOptionHelper);
	}

	public void keyPressed(KeyEvent e) {
		//  if e.control == numericText
		e.doit = (e.keyCode >=48 && e.keyCode <= 57);
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	
}
