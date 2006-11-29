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
 * <p>Copyright (c) 2006</p>
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
			//labelProv.setCapabilities((OWSLayerCapabilities) parentElement);
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
