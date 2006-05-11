package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vast.ows.sld.Dimensions;
import org.vast.ows.sld.RasterSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.RasterStyler;

/**
 * 
 * @author tcook
 * @since: 3/16/05
 */

public class BasicRasterController extends OptionController
{
	private Composite parent;
	private RasterOptionHelper rasterOptionHelper;
	
	public BasicRasterController(Composite parent, RasterStyler styler){
		this.parent = parent;
		this.styler = styler;

		rasterOptionHelper = new RasterOptionHelper(this);
		buildControls();
	}
	
	public void buildControls() {
		optionControls = new OptionControl[6];
		//  Image Width
		optionControls[0] = new OptionControl(parent, 0x0);
		//  get width from styler
		optionControls[0].createNumericText("Width:", "" + rasterOptionHelper.getWidth());

		//  Image Height
		optionControls[1] = new OptionControl(parent,0x0);
		//  get height from styler
		optionControls[1].createNumericText("Height:", "" + rasterOptionHelper.getHeight());

		addSelectionListener(rasterOptionHelper);
	}
}
