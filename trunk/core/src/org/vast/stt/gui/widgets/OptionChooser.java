/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Tony Cook
 * 
 * TODO:  Right now, parent of this class MUST use GridLayout \
 *        with 3 columns.  Refactor layout dependence out.
 */

public abstract class OptionChooser {  //  extends Group/ScrolledComp
	protected Composite optComp;  //  hold onto scroller and composite 
	protected ScrolledComposite optScr;
	//  TODO fix access
	public OptionController optionController = null;

	public OptionChooser(Composite parent){
		init(parent);
	}

	abstract public void buildControls(Object inputElement);
	
	public void init(Composite parent){
		optScr = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		optScr.setExpandHorizontal(true);
		optScr.setExpandVertical(true);
		optComp = new Composite(optScr, SWT.NONE);
		optScr.setContent(optComp);

		GridData scrollerGD = new GridData();
        scrollerGD.horizontalSpan = 3;
        scrollerGD.horizontalAlignment = GridData.FILL;
        scrollerGD.verticalAlignment = GridData.FILL;
        scrollerGD.grabExcessHorizontalSpace = true;
        scrollerGD.grabExcessVerticalSpace = true;
        //scrollerGD.heightHint = 60;
        scrollerGD.minimumHeight = 80;
		optScr.setLayoutData(scrollerGD);
		
		GridLayout optLayout = new GridLayout(1, false);
		optLayout.marginHeight = 1;
		optComp.setLayout(optLayout);
		optComp.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

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
