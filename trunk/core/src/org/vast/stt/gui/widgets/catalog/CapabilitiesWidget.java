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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * <p><b>Title:</b>
 *  TODO:  Add Title
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  TODO: Add Description
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapabilitiesWidget extends Composite 
{
	private CapServerTree capServerTree;
	private Button removeBtn, editBtn, addBtn, getCapsBtn; 
	
	public CapabilitiesWidget(Composite parent, int style) {
		super(parent,  style);
		initGui(parent);
	}

	public void initGui(Composite parent){
		parent.setLayout(new GridLayout());
		
		//  TreeViewer for available Cap Servers
		capServerTree = new CapServerTree(parent);
		capServerTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		//  Buttons
		Composite btnComp = new Composite(parent, 0x0);
		btnComp.setLayout(new GridLayout(4, false));
		removeBtn = new Button(btnComp, SWT.PUSH);
		removeBtn.setText("remove");
		removeBtn.setToolTipText("Remove selected server from Server list");
		addBtn = new Button(btnComp, SWT.PUSH);
		addBtn.setText("add");
		addBtn.setToolTipText("Add a server to Server list");
		editBtn = new Button(btnComp, SWT.PUSH);
		editBtn.setText("edit");
		editBtn.setToolTipText("Edit properties of selected server");
		getCapsBtn = new Button(btnComp, SWT.PUSH);
		getCapsBtn.setText("GetCaps");
		getCapsBtn.setToolTipText("Get Capabilities doc from selected server");
		
		//  TreeViewer for Layers from Caps
	}

}

