package org.vast.stt.gui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public abstract class OptionChooser {
	protected Composite optComp;
	protected ScrolledComposite optScr;
	
	public OptionChooser(Composite parent){
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
        scrollerGD.heightHint = 60;
        //scrollerGD.minimumHeight = 80;
		optScr.setLayoutData(scrollerGD);
		
		GridLayout optLayout = new GridLayout(1, false);
		optLayout.marginHeight = 1;
		optComp.setLayout(optLayout);
		optComp.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	abstract public void buildControls(Object inputElement);
	
	//  There's no way to remove a Control from a Composite (that I have found)
	//  Therefore, just dispose and null them, and recreate as needed.  TC
	public void removeOldControls(){
		Control [] controls = optComp.getChildren();
		for(int i=0; i<controls.length; i++){
			controls[i].dispose();
			controls[i] = null;
		}
	}


}
