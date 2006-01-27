package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;

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
	
	public StyleOptionChooser(Composite parent) {
		super(parent);
	}

	public void buildControls(Object stylerObj){
		DataStyler styler = (DataStyler) stylerObj;
		removeOldControls();

		if(styler instanceof PointStyler) {
			//if(pointOpts == null)
				PointOptionController pointOpts = new PointOptionController(optComp, (PointStyler)styler);
		} else if (styler instanceof LineStyler) {
			//if(lineOpts == null)
				LineOptionController lineOpts = new LineOptionController(optComp, (LineStyler)styler); 
		} else
			System.err.println("Styler not supported yet: " + styler);
		
		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	

}
