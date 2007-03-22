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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.stt.gui.widgets.symbolizer.AdvancedGeometryTab;

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

public class SOSMappingPage extends WizardPage 
{
	SOSLayerCapabilities caps;
	String [] offerings;
	private AdvancedGeometryTab geometryComp;
	String [] dataComponents;
	
	public SOSMappingPage(SOSLayerCapabilities caps){
		super("Map Offerings");
		this.caps = caps;
		setTitle("Map Offerings to Display");
		setDescription("Map SOS Offering components to Display space");
		getComponents();
	}
	
	public void setOfferings(String [] offerings){
		this.offerings = offerings;
	}
	
	private void getComponents(){
		//  issue dummy request
		//  Get DataComponents;
		dataComponents = new String [] {"lat","longitude","other"};
	}
	
	public void createControl(Composite parent){
		geometryComp = new AdvancedGeometryTab(parent);
		geometryComp.setMappableItems(dataComponents);
		setControl(geometryComp);
	}
	
/*	public IWizardPage getNextPage(){
		storeCurrentMappings();
		if(moreOfferings) {
			getComponents();
			updateControl();
		}
		return mappingPage;
	}
*/}


