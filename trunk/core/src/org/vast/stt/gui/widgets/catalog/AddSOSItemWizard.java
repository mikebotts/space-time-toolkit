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

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.stt.project.tree.DataItem;

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
	SOSSymbolizerPage symPage;
	SOSOfferingChooserPage sosChooserPage;
	DataItem [] newItems;
	boolean canFinish = false;
	
	public AddSOSItemWizard(SOSLayerCapabilities caps){
		this.caps = caps;
		this.setWindowTitle("Add Items to Scene Tree");
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	public void addPages()
	{
		sosChooserPage = new SOSOfferingChooserPage(caps);
		addPage(sosChooserPage);
		mappingPage = new SOSMappingPage(caps);
		addPage(mappingPage);
		symPage = new SOSSymbolizerPage();
		addPage(symPage);
	}	

	@Override
	public boolean performFinish() {
		HashMap <String , String[]> selMappings = mappingPage.getSelectedMappings();
		String [] symTypes = symPage.getSelectedSymbolizerTypes();
		Iterator<String> offIt = selMappings.keySet().iterator();
		String offTmp;
		String [] mapTmp;
		newItems = new DataItem[selMappings.size()];
		while(offIt.hasNext()){
			offTmp = offIt.next();
			mapTmp = selMappings.get(offTmp);
			for(int i=0; i<symTypes.length; i++){
				newItems[i] = SOSLayerFactory.createSOSLayer(offTmp, mapTmp, symTypes[i]);
			}
		}
		
		//  Drop items 
		return true;
	}

	public void setCanFinish(boolean b){
		canFinish = b;
	}
	
	public boolean canFinish(){
		return canFinish;
	}

	public DataItem[] getNewItems() {
		return newItems;
	}

}
