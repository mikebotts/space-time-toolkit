package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.PolygonSymbolizer;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.project.DataItem;


/**
 * <p><b>Title:</b>
 * Advanced Raster Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Builds advanced Raster controls for Advanced Dialog
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Feb 06, 2006
 * @version 1.0
 */
public class AdvancedRasterController extends AdvancedOptionController 
	implements SelectionListener {
	private Composite parent;
	//private RasterOptionHelper rasterOptionHelper;
	
	public AdvancedRasterController(Composite parent, DataItem item, PolygonSymbolizer symbolizer){
		super(item);
		this.parent = parent;
		this.symbolizer = symbolizer;
		
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

	public void loadFields(){};
}
