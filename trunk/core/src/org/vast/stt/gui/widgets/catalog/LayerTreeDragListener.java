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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.wms.WMSLayerCapabilities;

/**
 * <p><b>Title:</b>
 *   LayerTreeDragListener
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  TODO: Add Description
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 31, 2006
 * @version 1.0
 */

public class LayerTreeDragListener extends DragSourceAdapter 
	implements ISelectionChangedListener
{
	private StructuredViewer viewer;
	OWSLayerCapabilities currentSelection;
	boolean dragOk = false;  
	
	public LayerTreeDragListener(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	public void dragStart(DragSourceEvent event) {
		//event.doit = false;
		event.doit = dragOk;
	}
	
	 public void dragSetData(DragSourceEvent event) {
		// Provide the data of the requested type.
		if (LayerTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = currentSelection;
		}
	}	

	/**
	 * selectionChanged - receives event whenever LayerTree selection changes
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		//System.err.println("Sel is " + event.getSelection());
		StructuredSelection sel = (StructuredSelection)event.getSelection();
		Object selObj = sel.getFirstElement();
		if(selObj instanceof OWSLayerCapabilities) {
			if(selObj instanceof WMSLayerCapabilities){
				WMSLayerCapabilities caps = (WMSLayerCapabilities)selObj;
				if(caps.getChildLayers() != null){
					dragOk = false;  //  do not allow layers with children to be dragged
					return;
				}
			}
			dragOk = true;
			currentSelection = (OWSLayerCapabilities)selObj;
		} else
			dragOk = false;
	}
}

