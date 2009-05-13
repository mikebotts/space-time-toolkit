/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit SPS Plugin".
 
 The Initial Developer of the Original Code is Spotimage S.A.
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Alexandre Robin <alexandre.robin@spotimage.fr> for more
 information.
 
 Contributor(s): 
    Alexandre Robin <alexandre.robin@spotimage.fr>
 
******************************* END LICENSE BLOCK ***************************/

package com.spotimage.stt.sps.gui;

import java.io.InputStream;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.gui.dialogs.DataProviderJob;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.gui.views.SceneTreeView;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.xml.DOMHelper;
import com.spotimage.stt.sps.provider.SPSFeasibilityProvider;

/**
 * <p><b>Title:</b>
 * Add SPS Wizard
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO AddSPSItemWizard type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Mar 20, 2008
 * @version 1.0
 */
public class AddSPSWizard extends Wizard
{
	protected String itemName = "EO SPS Feasibility Study";
	protected String server;
	protected String sensorID;
	
	
	public AddSPSWizard()
	{
		this.setWindowTitle("Add New SPS Server");
	}
	
	
	@Override
	public void addPages()
	{
		WizardPage page;
		page = new WizardPageServer();
		addPage(page);
		page = new WizardPageSensor();
		addPage(page);
		page = new WizardPageItem();
		addPage(page);
	}

	
	@Override
	public boolean canFinish()
	{
		if (server == null || server.trim().equals(""))
			return false;
			
		if (sensorID == null || sensorID.trim().equals(""))
			return false;
		
		if (itemName == null || itemName.trim().equals(""))
			return false;
			
		return true;
	}
	
	
	@Override
	public boolean performFinish()
	{					
		try
		{
			// create status item from template
			InputStream is = getClass().getResourceAsStream("FeasibilityItem.xml");
			DOMHelper dom = new DOMHelper(is, false);
			DataTreeReader itemReader = new DataTreeReader();
			DataItem newItem = itemReader.readDataItem(dom, dom.getBaseElement());
			
			// init item with right name, server and sensorID
			newItem.setName(itemName);
			SPSFeasibilityProvider provider = (SPSFeasibilityProvider)newItem.getDataProvider();
			provider.setServer(server, "2.0");
			provider.setSensorID(sensorID);
			provider.setName(itemName);
			new DataProviderJob(provider);
			
			// insert in tree (next to select item or in selected folder)
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			SceneTreeView view = (SceneTreeView)page.findView(SceneTreeView.ID);
			ISelection selection = view.getSite().getSelectionProvider().getSelection();
			if (selection != null)
			{
				Object selectedObj = ((IStructuredSelection)selection).getFirstElement();
				if (selectedObj != null)
				{
					if (selectedObj instanceof Scene)
					{
						((Scene)selectedObj).getDataTree().add(newItem);
					}
					if (selectedObj instanceof DataItem)
		            {
					    DataItem selectedItem = (DataItem)selectedObj;
					    ((DataFolder)selectedItem.getParent()).add(newItem);
		            }
					else if (selectedObj instanceof DataFolder)
		            {
						DataFolder selectedFolder = (DataFolder)selectedObj;
						selectedFolder.add(newItem);
		            }
				}
			}
			else
			{
				ScenePageInput pageInput = (ScenePageInput)page.getInput();
				pageInput.getScene().getDataTree().add(newItem);
			}
			view.refreshView();
			
			// pop up SPS views
			String viewID;
			viewID = SPSFeasibilityFormView.ID;
			page.showView(viewID);
			viewID = SPSFeasibilityResultView.ID;
			page.showView(viewID);
			viewID = SPSStatusView.ID;
			page.showView(viewID);
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
