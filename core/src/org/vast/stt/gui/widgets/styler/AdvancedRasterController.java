package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.style.DataStyler;

public class AdvancedRasterController extends AdvancedOptionController 
	implements SelectionListener {
	private Composite parent;
	//private RasterOptionHelper rasterOptionHelper;
	
	public AdvancedRasterController(Composite parent, DataStyler styler){
		this.parent = parent;
		this.styler = styler;
		
		//rasterOptionHelper = new RasterOptionHelper(this);
		buildControls();
	}
	
	public void buildControls(){
		optionControls = new OptionControl[2];
		mapFromCombo = new Combo[2];
		lutButton = new Button[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		optionControls[0].createText("Width:", "500");
		//advWidthSpinner.setSelection((int)rasterOptionHelper.getPointSize());
		//  add other controls
		addMappingControls(parent, 0);
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createText("Height:", "500");

		//addMappingControls(parent, 1);
		//  disable color mapFromCombo (consider just not showing this)
		mapFromCombo[1].setEnabled(false);
		//addSelectionListener(rasterOptionHelper);
	}

	public void setMappableItems(String [] items){
	}
	
	protected void doLut(int index){
		System.err.println("AdvLinControl:  doLut for " + index);
	}
	
	protected void doMapping(int index){
	}		

}
