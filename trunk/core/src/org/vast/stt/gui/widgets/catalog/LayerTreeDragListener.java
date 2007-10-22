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
 * <p>Copyright (c) 2007</p>
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

