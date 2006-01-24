package org.vast.stt.gui.widgets.styler;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Graphic;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.GraphicSource;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;
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

public class StyleOptionChooser {
	Composite optComp;
	PointOptionController pointOpts;
	LineOptionController lineOpts;
	
	public StyleOptionChooser(Composite parent){
		init(parent);
	}

	public void init(Composite parent){
		ScrolledComposite optScr = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		optScr.setExpandHorizontal(true);
		optScr.setExpandVertical(true);
		optComp = new Composite(optScr, SWT.NONE);
		optScr.setContent(optComp);

		GridData scrollerGD = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true);
		scrollerGD.heightHint = 80;
		scrollerGD.widthHint = 125;
		optScr.setLayoutData(scrollerGD);
		
		GridLayout optLayout = new GridLayout(2, false);
		optComp.setLayout(optLayout);
		optComp.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		//buildControls(new LineStyler());
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));	
		//  initial controls should come from DataItem (vie StyleWidget)
	}
	
	public void buildControls(DataStyler styler){
		turnOffOldControls();

		if(styler instanceof PointStyler) {
			//if(pointOpts == null)
				pointOpts = new PointOptionController(optComp, (PointStyler)styler);
		} else if (styler instanceof LineStyler) {
			//if(lineOpts == null)
				lineOpts = new LineOptionController(optComp, (LineStyler)styler); 
		} else
			System.err.println("Styler not supported yet: " + styler);
		
		optComp.layout(true);		
		optComp.redraw();
	}	

	//  There HAS to be a better way to remove a control from a Composite than to 
	//  destroy it, but I can't find it.  TC
	protected void turnOffOldControls(){
		Control [] controls = optComp.getChildren();
		for(int i=0; i<controls.length; i++){
			controls[i].dispose();
			controls[i] = null;
		}
	}


}
