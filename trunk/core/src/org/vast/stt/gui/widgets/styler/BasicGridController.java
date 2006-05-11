package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.GridStyler;

public class BasicGridController extends OptionController 
{
	private Composite parent;
	private GridOptionHelper gridOptionHelper;
	
	public BasicGridController(Composite parent, GridStyler styler){
		this.parent = parent;
		this.styler = styler;

		gridOptionHelper = new GridOptionHelper(this);
		buildControls();
	}
	
	public void buildControls() {
		// TODO Auto-generated method stub
		optionControls = new OptionControl[7];
		
		optionControls[0] = new OptionControl(parent, 0x0);
		int width = gridOptionHelper.getGridWidth();
		optionControls[0].createNumericText("Grid Width:", "" + width);

		optionControls[1] = new OptionControl(parent, 0x0);
		int length = gridOptionHelper.getGridLength();
		optionControls[1].createNumericText("Grid Length:", "" + length);

		optionControls[2] = new OptionControl(parent, 0x0);
		int depth = gridOptionHelper.getGridDepth();
		optionControls[2].createNumericText("Grid Depth:", "" + depth);
		
		optionControls[3] = new OptionControl(parent, 0x0);
		//boolean fillEnabled = gridOptionHelper.getFillEnabled();
		Button fillToggle = optionControls[3].createCheckbox("Fill Grid:", true);

		optionControls[4] = new OptionControl(parent, 0x0);
		optionControls[4].createColorButton("Fill Color:", gridOptionHelper.getFillColor());

		optionControls[5] = new OptionControl(parent, 0x0);
		//boolean fillEnabled = gridOptionHelper.getShowWiremesh();
		Button meshToggle = optionControls[5].createCheckbox("Show Wiremesh:", false);

		optionControls[6] = new OptionControl(parent, 0x0);
		//optionControls[6].createColorButton("Mesh Color:", gridOptionHelper.getMeshColor());
		optionControls[6].createColorButton("Mesh Color:", 
				gridOptionHelper.getFillColor());

		addSelectionListener(gridOptionHelper);
	}
}
