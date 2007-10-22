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

package org.vast.stt.gui.widgets.symbolizer;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vast.stt.data.DataNode;
import org.vast.stt.project.tree.DataItem;

/**
 * <p><b>Title:</b>
 *  GeometryDialog
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Nov 28, 2006
 * @version 1.0
 */

public class GeometryDialog extends Dialog
{
	AdvancedGeometryTab advGeomTab;
	DataItem dataItem;
	
	public GeometryDialog(Shell parent, DataItem item){
		super(parent);
		this.dataItem = item;
		this.open();
	}
	
	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Configure Geometry");
    }

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite)super.createDialogArea(parent);
		comp.setLayout(new GridLayout(2, false));

		//  Type Combo
		Label typeLabel = new Label(comp, SWT.RIGHT);
		GridData gridData = new GridData();
		typeLabel.setText("Graphic Type:");
		
		advGeomTab = new AdvancedGeometryTab(this.getParentShell());
		String [] mappableItems = getMappableItems();
		advGeomTab.setMappableItems(mappableItems);
		
		return comp;
	}

	protected void okPressed() {
		
		super.okPressed();
	}
	
	//  TODO move this somewhere where AdvancedStyleDialog can share it
	protected String [] getMappableItems(){
		DataNode node = dataItem.getDataProvider().getDataNode();
		if (node == null) {
			System.err
					.println("ASD.getMappables():  Node is still null (probably not yet enabled.");
			return null;
		}
		List<String> mappingList = node.getPossibleScalarMappings();
		return mappingList.toArray(new String[0]);
	}
}

