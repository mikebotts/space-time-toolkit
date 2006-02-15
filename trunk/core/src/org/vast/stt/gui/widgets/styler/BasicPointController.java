package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.PointStyler;

public class BasicPointController extends OptionController  
{
	private Composite parent;
	private PointOptionHelper pointOptionHelper;
	
	public BasicPointController(Composite parent, PointStyler styler){
		this.parent = parent;
		this.styler = styler;

		pointOptionHelper = new PointOptionHelper(this);
		buildControls();
	}
	
	public void buildControls() {
		// TODO Auto-generated method stub
		optionControls = new OptionControl[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		Spinner sizeSpinner = optionControls[0].createSpinner("Point Size:", 1,10);
		sizeSpinner.setSelection((int)pointOptionHelper.getPointSize());
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createColorButton("Point Color:", pointOptionHelper.getPointColor());

		addSelectionListener(pointOptionHelper);
	}
	
}
