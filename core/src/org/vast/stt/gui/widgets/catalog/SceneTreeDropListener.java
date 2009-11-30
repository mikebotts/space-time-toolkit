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

import java.util.Iterator;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.sas.SASLayerCapabilities;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.wcs.WCSLayerCapabilities;
import org.vast.ows.wfs.WFSLayerCapabilities;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.DataTree;
import org.vast.stt.project.world.WorldScene;

/**
 * <p><b>Title:</b>
 *  SceneTreeDropListener
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Performs Drops onto the SceneTree
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Nov 2006
 * @version 1.0
 */

public class SceneTreeDropListener extends ViewerDropAdapter 
{
	public SceneTreeDropListener(StructuredViewer viewer) {
		super(viewer);
	}

	/**
	 *  This is called when a Layer from LayerTree in CapabilitiesView 
	 *  is dropped onto the SceneTree.  The data is an instance of 
	 *  OWSLayerCapabilities
	 */
	public boolean performDrop(Object data) {
		OWSLayerCapabilities caps = (OWSLayerCapabilities) data;

//		DataItem newItem = new DataItem(); // use world item for stuffs to be rendered in the world view
//		newItem.setName(caps.getName());

		if (data instanceof SOSLayerCapabilities) {
			startAddItemWizard((SOSLayerCapabilities)caps);
			//DataItem [] items = createSOSItem((SOSLayerCapabilities)caps);
			//			for(int i=0; i<items.length; i++) {
//				//  Popup addItem wizard BEFORE drop occurs
//				dropItem(items[i]);
//			}
			return true;
		} else if (data instanceof SASLayerCapabilities) {
			startAddItemWizard((SASLayerCapabilities)caps);
			return true;
		} else if (data instanceof WMSLayerCapabilities) {
			DataItem item = WMSLayerFactory.createWMSLayer((WMSLayerCapabilities)caps);
			return dropItem(item);
		} else if (data instanceof WCSLayerCapabilities) {
			System.err.println("Add WCS drop support");
			return false;
		} else if (data instanceof WFSLayerCapabilities) {
			System.err.println("Add WFS drop support");
			return false;
		} else {
			System.err
					.println("SceneTreeDropListener.performDrop():  Unknown Caps type.");
			return false;
		}
	}

	public void startAddItemWizard(SOSLayerCapabilities caps){
		AddSOSItemWizard addItemWizard = new AddSOSItemWizard(caps, this);
		addItemWizard.init(PlatformUI.getWorkbench(), null);
		// Instantiates the wizard container with the wizard and opens it
		WizardDialog dialog = 
			new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), addItemWizard);
		dialog.create();
		dialog.open();
	}
	
	public void startAddItemWizard(SASLayerCapabilities caps){
		AddSASItemWizard addItemWizard = new AddSASItemWizard(caps, this);
		addItemWizard.init(PlatformUI.getWorkbench(), null);
		// Instantiates the wizard container with the wizard and opens it
		WizardDialog dialog = 
			new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), addItemWizard);
		dialog.create();
		dialog.open();
	}
	
	/**
	 * Method declared on ViewerDropAdapter
	 */
	public boolean validateDrop(Object target, int op, TransferData type) {
		boolean dropOk = LayerTransfer.getInstance().isSupportedType(type);
		dropOk = dropOk && (target instanceof DataEntry);
//		return dropOk;
		return dropOk || true;
	}

	public void dropItems(DataItem [] items){
		for(int i=0; i<items.length; i++)
			dropItem(items[i]);
	}
	
	protected boolean dropItem(DataItem item) {
		if(item.getName()==null){
			System.err.println("DataItem name is null in SceneTreeDropListner.  Should not have gotten here.");
			return false;
		}
		
		DataEntry dropTarget = (DataEntry) this.getCurrentTarget();
		TreeViewer vwr = (TreeViewer) this.getViewer();
		//  If target is a folder, drop into folder and open it
		if (dropTarget instanceof DataFolder) {
			((DataFolder) dropTarget).add(item);
			item.dispatchEvent(new STTEvent(item, EventType.SCENE_TREE_CHANGED), false);
			//		   vwr.reveal(item);  //  vwr.reveal() does not work as advertised in Eclipse 3.1!!!
			vwr.expandToLevel(dropTarget, 2); //  ensure dropped item is visible
			vwr.refresh();
			return true;
		}
		DataTree tree = null;
		//  If target is scene (Un-expanded tree), drop at root of Tree and expand
		if (dropTarget instanceof WorldScene) {
			tree = ((WorldScene) dropTarget).getDataTree();
			tree.add(0, item);
			item.dispatchEvent(new STTEvent(item, EventType.SCENE_TREE_CHANGED), false);
			vwr.expandToLevel(2); //  ensure dropped item is visible
			vwr.refresh();
			return true;
		}

		//  If we get here, WorldScene MUST be expandedElements[0];
		Object[] elems = vwr.getExpandedElements();
		WorldScene scene = (WorldScene) elems[0];
		tree = scene.getDataTree();

		//  Iterate the DataTree to find the target item,
		//  and insert it there, then refresh
		DataEntry entryTmp = null;
		Iterator<DataEntry> it = tree.iterator();
		boolean done = false;
		int index = 0;
		while (it.hasNext() && !done) {
			entryTmp = it.next();
			if (entryTmp instanceof DataFolder) {
				done = recurseFolder((DataFolder) entryTmp,
						(DataItem) dropTarget, item);
			} else {
				if (entryTmp == dropTarget) {
					tree.add(index, item);
					break;
				}
			}
			index++;
		}
		item.dispatchEvent(new STTEvent(item, EventType.SCENE_TREE_CHANGED), false);
		vwr.refresh();
		return true;
	}

	protected boolean recurseFolder(DataFolder folder, DataItem dropTarget,
			DataItem newItem) {
		Iterator<DataEntry> it = folder.iterator();
		DataEntry entryTmp;
		int index = 0;
		while (it.hasNext()) {
			entryTmp = it.next();
			if (entryTmp instanceof DataFolder) {
				recurseFolder((DataFolder) entryTmp, dropTarget, newItem);
			} else if (entryTmp == dropTarget) {
				folder.add(index, newItem);
				return true;
			}
			index++;
		}
		return false;
	}
}
