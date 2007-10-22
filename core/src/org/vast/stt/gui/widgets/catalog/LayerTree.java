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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vast.ows.OWSLayerCapabilities;

/**
 * <p><b>Title:</b>
 *  LayerTree
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Tree for adding layers (dataItems) to the Scene.  This tree is populated by 
 *  a "GetCapabilities" button event in CapabiltiesWidget.  Layers can be dragged from 
 *  this tree to the sceneTree.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class LayerTree implements ITreeContentProvider {
	TreeViewer treeViewer;
	LayerLabelProvider labelProv;
	
	public LayerTree(Composite parent){
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(this);
		labelProv = new LayerLabelProvider();
		treeViewer.setLabelProvider(labelProv);
		
		//  Add Drag support
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { LayerTransfer.getInstance()};
		LayerTreeDragListener dragListener = new LayerTreeDragListener(treeViewer);
		treeViewer.addDragSupport(ops, transfers, dragListener);
		treeViewer.addSelectionChangedListener(dragListener);
	}  
	
	public Control getControl(){
		return treeViewer.getControl();
	}
	
	public void setInput(List layers){
		treeViewer.setInput(layers);
	}
	
	//  TreeContentProvider interfaces
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof List) {
			List parentList = (List) parentElement;
			if (parentList.size() > 0) {
				//Object testObj = parentList.get(0);  //  may not need to test the object
				return ((ArrayList) parentElement).toArray();
			}
			return null;
		} else if (parentElement instanceof OWSLayerCapabilities) {
			labelProv.setCapabilities((OWSLayerCapabilities) parentElement);
			LayerInfo info = new LayerInfo((OWSLayerCapabilities) parentElement);
			return info.getOptions();
		} else if (parentElement instanceof List[]) {
			return (List[]) parentElement;
		} else
			return null;

	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof List || element instanceof List[])
			return true;
		else if (element instanceof OWSLayerCapabilities)
			return true;
		else
			return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
}
