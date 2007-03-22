/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.gui.widgets.catalog;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * <p><b>Title:</b>
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Mar 21, 2007
 * @version 1.0
 */

public class SOSStylerPage extends WizardPage {

	
	public SOSStylerPage(){
		super("Symbolizer Offerings");
		setTitle("Select Symbolizers");
		setDescription("Select Symbolizers for rendering Offerings");
	}
	
	public void createControl(Composite parent){
		//  Mapping GUI thingy
	}
}


