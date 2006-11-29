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
 * <p>Copyright (c) 2006</p>
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

