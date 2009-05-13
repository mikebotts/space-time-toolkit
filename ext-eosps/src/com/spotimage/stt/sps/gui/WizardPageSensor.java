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

import java.net.HttpURLConnection;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.vast.ows.GetCapabilitiesRequest;
import org.vast.ows.OWSUtils;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.spotimage.stt.sps.SPSPlugin;
import java.util.ArrayList;
import java.util.List;


/**
 * <p><b>Title:</b>
 * Sensor WizardPage
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO WizardPageSensor type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Mar 20, 2008
 * @version 1.0
 */
public class WizardPageSensor extends WizardPage
{
	protected Text sensorID;
	protected Label sensorDesc;
	protected List<String> sensorDescriptions = new ArrayList<String>();
	
	
	protected WizardPageSensor()
	{
		super("");
		setTitle("Sensor Selection");
		setDescription("Select a sensor to task");
		ImageDescriptor img = SPSPlugin.getImageDescriptor("icons/sat_icon_small.jpg");
		setImageDescriptor(img);
	}

	
	public void createControl(Composite parent)
	{
		final Composite body = new Composite(parent, SWT.NONE);
	    GridLayout gl = new GridLayout();
	    gl.numColumns = 1;
	    body.setLayout(gl);
	    
	    Composite field;
		Label label;
		GridData layoutData;
		
		// load capabilities button
		Button loadCaps = new Button(body, SWT.NONE);
		loadCaps.setText("Load Offerings");
				
	    // autodetected sensors
		field = new Composite(body, SWT.NONE);
		field.setLayout(new GridLayout(2, false));
		label = new Label(field, SWT.NONE);
		label.setText("Autodetected Sensors: ");
		
		final Combo combo = new Combo(field, SWT.DROP_DOWN);
		layoutData = new GridData();
		layoutData.minimumWidth = 250;
		layoutData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(layoutData);
		
		combo.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent event)
			{							
			}

			public void widgetSelected(SelectionEvent event)
			{
				Combo combo = ((Combo)event.widget);
				int selected = combo.getSelectionIndex();
				String ID = combo.getItem(selected);
				sensorID.setText(ID);
				sensorDesc.setText("Description:  " + sensorDescriptions.get(selected));
				sensorDesc.pack();
			}			
		});
		
		// event handler to load sensor IDs from capabilities
		loadCaps.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent event)
			{				
			}

			public void widgetSelected(SelectionEvent event)
			{
				Cursor oldCursor = body.getCursor();
				Cursor cursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
				body.setCursor(cursor);
				
				// populate combos w/ caps doc sensors
				try
				{
					GetCapabilitiesRequest getCapsRequest = new GetCapabilitiesRequest();
					getCapsRequest.setGetServer(((AddSPSWizard)getWizard()).server);
					getCapsRequest.setVersion("2.0");
					getCapsRequest.setService("SPS");
					OWSUtils owsUtils = new OWSUtils();
					HttpURLConnection connection = owsUtils.sendGetRequest(getCapsRequest);
					DOMHelper dom = new DOMHelper(connection.getInputStream(), false);
					NodeList offElts = dom.getElements("contents/SPSContents/offering/SensorOffering");
					for (int s=0; s<offElts.getLength(); s++)
					{
						Element offeringElt = (Element)offElts.item(s);
						combo.add(dom.getElementValue(offeringElt, "sensorID"));
						sensorDescriptions.add(dom.getElementValue(offeringElt, "abstract"));
					}
					combo.select(0);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				body.setCursor(oldCursor);
			}			
		});
		
		// SensorID box
		field = new Composite(body, SWT.NONE);
		field.setLayout(new GridLayout(2, false));
		label = new Label(field, SWT.NONE);
		label.setText("Sensor ID: ");
		
		sensorID = new Text(field, SWT.LEFT | SWT.BORDER);
		layoutData = new GridData();
		layoutData.minimumWidth = 250;
		layoutData.grabExcessHorizontalSpace = true;
		sensorID.setLayoutData(layoutData);
		
		// Sensor Description
		sensorDesc = new Label(body, SWT.NONE);
		sensorDesc.setText("Description: ");
		//label.setLayoutData(layoutData);
		
		sensorID.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent event)
			{
				((AddSPSWizard)getWizard()).sensorID = sensorID.getText();
				getWizard().getContainer().updateButtons();
			}			
		});
		
		setControl(body);
	}
	
	
	@Override
	public boolean canFlipToNextPage()
	{
		String text = sensorID.getText();
		if (text == null || text.trim().equals(""))
			return false;
		else
			return true;
	}

}
