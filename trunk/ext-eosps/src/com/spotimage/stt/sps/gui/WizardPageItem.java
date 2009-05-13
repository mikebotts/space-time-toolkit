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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import com.spotimage.stt.sps.SPSPlugin;

/**
 * <p><b>Title:</b>
 * Item Properties WizardPage
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO WizardPageServer type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Mar 20, 2008
 * @version 1.0
 */
public class WizardPageItem extends WizardPage
{
	protected Text itemName;
	
	
	protected WizardPageItem()
	{
		super("");
		setTitle("Item Properties");
		setDescription("Pick new item properties");
		ImageDescriptor img = SPSPlugin.getImageDescriptor("icons/sat_icon_small.jpg");
		setImageDescriptor(img);
	}

	
	public void createControl(Composite parent)
	{
		Composite body = new Composite(parent, SWT.NONE);
	    GridLayout gl = new GridLayout();
	    gl.numColumns = 1;
	    body.setLayout(gl);
	    
	    Composite field;
		Label label;
		
		// Item Name box
		field = new Composite(body, SWT.NONE);
		field.setLayout(new GridLayout(2, false));
		label = new Label(field, SWT.NONE);
		label.setText("Item Name: ");
		
		itemName = new Text(field, SWT.LEFT | SWT.BORDER);
		GridData layoutData = new GridData();
		layoutData.minimumWidth = 200;
		layoutData.grabExcessHorizontalSpace = true;
		itemName.setLayoutData(layoutData);
		itemName.setText(((AddSPSWizard)getWizard()).itemName);
		
		itemName.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent event)
			{
				((AddSPSWizard)getWizard()).itemName = itemName.getText();
				getWizard().getContainer().updateButtons();
			}			
		});
				
		setControl(body);
	}
	
	
	@Override
	public boolean canFlipToNextPage()
	{
		return false;
	}

}
