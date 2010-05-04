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

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import com.spotimage.stt.sps.SPSPlugin;

/**
 * <p><b>Title:</b>
 * Server WizardPage
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
public class WizardPageServer extends WizardPage
{
	protected Text address;
	
	
	protected WizardPageServer()
	{
		super("");
		setTitle("Server Selection");
		setDescription("Select an SPS service to connect to");
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
		
	    // preselected servers
		field = new Composite(body, SWT.NONE);
		field.setLayout(new GridLayout(2, false));
		label = new Label(field, SWT.NONE);
		label.setText("Predefined Server: ");
		
		Combo combo = new Combo(field, SWT.DROP_DOWN);
		String[][] servers = new String[][]
		{
			{"Spotimage EO SPS (Local)", "http://localhost:8080/axis2/services/SPSv20"},
			{"Spotimage EO SPS (Online)", "http://ws.spotimage.com/axis2/services/SPSv20"},
			{"ESRIN EO SPS", "http://ssews1.esrin.esa.int/HMA-SPS/services/HMA-SPS"},
			{"Pléiades EO SPS", "http://10.69.164.157:8080/axis2/services/SPSPrototype.SPSPrototypeHttpSoap11Endpoint/"}
		};
		for (int s=0; s<servers.length; s++)
		{
			combo.add(servers[s][0]);
			combo.setData(servers[s][0], servers[s][1]);
		}
		
		// add listener		
		combo.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent event)
			{							
			}

			public void widgetSelected(SelectionEvent event)
			{
				Combo combo = ((Combo)event.widget);
				int selected = combo.getSelectionIndex();
				String serverName = combo.getItem(selected);
				address.setText((String)combo.getData(serverName));
			}			
		});
		
		// Address box
		field = new Composite(body, SWT.NONE);
		field.setLayout(new GridLayout(2, false));
		label = new Label(field, SWT.NONE);
		label.setText("Server Endpoint: ");
		
		address = new Text(field, SWT.LEFT | SWT.BORDER);
		GridData layoutData = new GridData();
		layoutData.minimumWidth = 350;
		layoutData.grabExcessHorizontalSpace = true;
		address.setLayoutData(layoutData);
		
		address.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent event)
			{
				((AddSPSWizard)getWizard()).server = address.getText();//"http://localhost:8080/axis2/services/SPSv20";
				getWizard().getContainer().updateButtons();
			}			
		});
				
		setControl(body);
	}
	
	
	@Override
	public boolean canFlipToNextPage()
	{
		String text = address.getText();
		
		if (text == null)
			return false;
		
		try
		{
			new URL(text);
		}
		catch (MalformedURLException e)
		{
			((AddSPSWizard)getWizard()).server = null;
			return false;
		}
		
		return true;
	}

}
