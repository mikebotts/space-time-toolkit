package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vast.ows.sld.Dimensions;
import org.vast.ows.sld.RasterSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.RasterStyler;
import org.vast.stt.style.TextureMappingStyler;

/**
 * 
 * @author tcook
 * @since: 3/16/05
 */

public class BasicTextureController extends OptionController
{
	private Composite parent;
	private RasterOptionHelper rasterOptionHelper;
	private GridOptionHelper gridOptionHelper;
	
	public BasicTextureController(Composite parent, TextureMappingStyler styler){
		this.parent = parent;
		this.styler = styler;

		rasterOptionHelper = new RasterOptionHelper(this);
		gridOptionHelper = new GridOptionHelper(this);
		buildControls();
	}
	
	public void buildControls() {
		optionControls = new OptionControl[7];
		//  Image Width
		optionControls[0] = new OptionControl(parent, 0x0);
		//  get width from styler
		optionControls[0].createNumericText("Width:", "" + rasterOptionHelper.getWidth());

		//  Image Height
		optionControls[1] = new OptionControl(parent,0x0);
		//  get height from styler
		optionControls[1].createNumericText("Height:", "" + rasterOptionHelper.getHeight());

		optionControls[2] = new OptionControl(parent, 0x0);
		int width = gridOptionHelper.getGridWidth();
		optionControls[2].createNumericText("Grid Width:", "" + width);

		optionControls[3] = new OptionControl(parent, 0x0);
		int length = gridOptionHelper.getGridLength();
		optionControls[3].createNumericText("Grid Length:", "" + length);

		optionControls[4] = new OptionControl(parent, 0x0);
		int depth = gridOptionHelper.getGridDepth();
		optionControls[4].createNumericText("Grid Depth:", "" + depth);
		
		//  Disallow fill options for Texture?
//		optionControls[3] = new OptionControl(parent, 0x0);
//		//boolean fillEnabled = gridOptionHelper.getFillEnabled();
//		Button fillToggle = optionControls[3].createCheckbox("Fill Grid:", true);
//
//		optionControls[4] = new OptionControl(parent, 0x0);
//		optionControls[4].createColorButton("Fill Color:", gridOptionHelper.getFillColor());
//
		optionControls[5] = new OptionControl(parent, 0x0);
		//boolean fillEnabled = gridOptionHelper.getShowWiremesh();
		Button meshToggle = optionControls[5].createCheckbox("Show Wiremesh:", false);

		optionControls[6] = new OptionControl(parent, 0x0);
		//optionControls[6].createColorButton("Mesh Color:", gridOptionHelper.getMeshColor());
		optionControls[6].createColorButton("Mesh Color:",	gridOptionHelper.getFillColor());

		addSelectionListener(gridOptionHelper);
		addSelectionListener(rasterOptionHelper);
	}
}
