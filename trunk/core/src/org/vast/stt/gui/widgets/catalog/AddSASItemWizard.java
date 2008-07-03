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

package org.vast.stt.gui.widgets.catalog;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.vast.math.Vector3d;
import org.vast.ows.sas.SASLayerCapabilities;
import org.vast.stt.project.tree.DataItem;

/**
 * <p><b>AddItemWizard:</b>
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Mar 8, 2007
 * @version 1.0
 */

public class AddSASItemWizard extends Wizard implements INewWizard 
{
	SASLayerCapabilities caps;
	SASMappingPage mappingPage;
	SASSymbolizerPage symPage;
	SASSubscriptionChooserPage sasChooserPage;
	DataItem [] newItems;
	boolean canFinish = false;
	SceneTreeDropListener dropListener;
	
	public AddSASItemWizard(SASLayerCapabilities caps, SceneTreeDropListener dropper){
		this.caps = caps;
		this.setWindowTitle("Add Items to Scene Tree");
		this.dropListener = dropper;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	public void addPages()
	{
		sasChooserPage = new SASSubscriptionChooserPage(caps);
		addPage(sasChooserPage);
		mappingPage = new SASMappingPage(caps);
		addPage(mappingPage);
		symPage = new SASSymbolizerPage();
		addPage(symPage);
	}	

	@Override
	public boolean performFinish() {
		HashMap <String , String[]> selMappings = mappingPage.getSelectedMappings();
		String [] symTypes = symPage.getSelectedSymbolizerTypes();
		Vector3d foi = mappingPage.getFoiLocation();
		Iterator<String> offIt = selMappings.keySet().iterator();
		String offTmp;
		String [] mapTmp;
		newItems = new DataItem[selMappings.size()];
		while(offIt.hasNext()){
			offTmp = offIt.next();
			mapTmp = selMappings.get(offTmp);
			for(int i=0; i<symTypes.length; i++){
				newItems[i] = SASLayerFactory.createSASLayer(offTmp, caps, mapTmp, symTypes[i], foi);
			}
		}
		
		//  Drop items 
		dropListener.dropItems(newItems);
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

