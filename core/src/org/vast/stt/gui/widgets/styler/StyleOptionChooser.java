package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.project.DataStyler;
import org.vast.stt.style.GridStyler;
import org.vast.stt.style.LabelStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;
import org.vast.stt.style.RasterStyler;
import org.vast.stt.style.TextureMappingStyler;

/**
 * <p><b>Title:</b><br/>
 * StyleOptionChooser
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	StyleOptionChooser is a composite that holds label/control pairs for 
 *  selecting options for a particular Styler type.   
 *  Currently supports the following Control types:
 *  	Spinner, Button, Combo
 *  Currently supports the following Styler types
 *  	Point, Line    
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 18, 2006
 * @version 1.0
 * 
 * TODO  add chooser/mapping widget when user selects + (add) style
 * TODO  add support for other Stylers (Polygon, Raster) 
 * TODO  support advanced options      
 */

public class StyleOptionChooser extends OptionChooser {
	
	//  Need to keep controllers in memory and just rebuild their
	//  controls as needed, so basic and advanced options can
	//  co-exist and change together
	private OptionListener optListener;

	public StyleOptionChooser(Composite parent, OptionListener ol) {
		super(parent);
		this.optListener = ol;
	}

	public void buildControls(Object stylerObj){
		DataStyler styler = (DataStyler) stylerObj;
		removeOldControls();

		OptionController optionController = null;
		if(styler instanceof PointStyler) {
			optionController = new BasicPointController(optComp, (PointStyler)styler);
		} else if (styler instanceof LineStyler) {
			optionController = new BasicLineController(optComp, (LineStyler)styler);
		} else if (styler instanceof GridStyler) {
			optionController = new BasicGridController(optComp, (GridStyler)styler);
		} else if (styler instanceof RasterStyler) {
			optionController = new BasicRasterController(optComp, (RasterStyler)styler);
		} else if (styler instanceof LabelStyler) {
			optionController = new BasicLabelController(optComp, (LabelStyler)styler);
		} else if (styler instanceof TextureMappingStyler) {
			optionController = new BasicTextureController(optComp, (TextureMappingStyler)styler);
		} else 
			System.err.println("Styler not supported yet: " + styler);
		
		if(optionController == null)
			return;
		optionController.addSelectionListener(optListener);
		optListener.setBasicController(optionController);

		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	

}
