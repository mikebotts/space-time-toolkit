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

public class AddSOSItemWizard extends Wizard implements INewWizard 
{
	SOSLayerCapabilities caps;
	SOSMappingPage mappingPage;
	SOSStylerPage stylerPage;
	
	public AddSOSItemWizard(SOSLayerCapabilities caps){
		this.caps = caps;
		this.setWindowTitle("Add Items to Scene Tree");
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	public void addPages()
	{
		SOSOfferingChooserPage sosChooserPage = new SOSOfferingChooserPage(caps);
		addPage(sosChooserPage);
		mappingPage = new SOSMappingPage(caps);
		addPage(mappingPage);
		//stylerPage = new SOSStylerPage();
		//addPage(stylerPage);
	}	

	@Override
	public boolean performFinish() {
		//  Drop items 
		return true;
	}

	

}

