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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.sos.SOSLayerCapabilities;

/**
 * <p><b>AddItemWizard:</b>
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Mar 8, 2007
 * @version 1.0
 */

public class AddItemWizard extends Wizard implements INewWizard {
	
	OWSLayerCapabilities caps;
	
	public AddItemWizard(OWSLayerCapabilities caps){
		this.caps = caps;
		this.setWindowTitle("Add Items to Scene Tree");
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	public void addPages()
	{
		SOSOfferingChooserPage sosChooserPage = new SOSOfferingChooserPage((SOSLayerCapabilities)caps);
		addPage(sosChooserPage);
		addPage(new SOSOfferingChooserPage((SOSLayerCapabilities)caps));
	}	

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}

	

}

