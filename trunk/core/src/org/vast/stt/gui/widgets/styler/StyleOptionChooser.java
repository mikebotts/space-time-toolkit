package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
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
	ScrolledComposite optScr;
	PointOptionController pointOpts;
	LineOptionController lineOpts;
	
	public StyleOptionChooser(Composite parent){
		init(parent);
	}

	public void init(Composite parent){
		optScr = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		optScr.setExpandHorizontal(true);
		optScr.setExpandVertical(true);
		optComp = new Composite(optScr, SWT.NONE);
		optScr.setContent(optComp);

		GridData scrollerGD = new GridData();
        scrollerGD.horizontalSpan = 6;
        scrollerGD.horizontalAlignment = GridData.FILL;
        scrollerGD.verticalAlignment = GridData.FILL;
        scrollerGD.grabExcessHorizontalSpace = true;
        scrollerGD.grabExcessVerticalSpace = true;
        //scrollerGD.heightHint = 80;
        scrollerGD.minimumHeight = 80;
		optScr.setLayoutData(scrollerGD);
		
		GridLayout optLayout = new GridLayout(1, false);
		optComp.setLayout(optLayout);
		optComp.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		//buildControls(new LineStyler());
		//optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//optComp.layout();
	}
	
	public void buildControls(DataStyler styler){
		removeOldControls();

		if(styler instanceof PointStyler) {
			//if(pointOpts == null)
				pointOpts = new PointOptionController(optComp, (PointStyler)styler);
		} else if (styler instanceof LineStyler) {
			//if(lineOpts == null)
				lineOpts = new LineOptionController(optComp, (LineStyler)styler); 
		} else
			System.err.println("Styler not supported yet: " + styler);
		
		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	

	//  There's no way to remove a Control from a Composite (that I have found)
	//  Therefore, just dispose and null them, and recreate as needed.  TC
	protected void removeOldControls(){
		Control [] controls = optComp.getChildren();
		for(int i=0; i<controls.length; i++){
			controls[i].dispose();
			controls[i] = null;
		}
	}


}
