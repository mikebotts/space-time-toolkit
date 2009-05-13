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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/**
 * <p><b>Title:</b>
 * Notification WizardPage
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO WizardNotification type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Feb 10, 2009
 * @version 1.0
 */
public class WizardPageNotification extends WizardPage
{
	protected Button notifRadio;
	protected Button pollRadio;
	protected Text uriField;
	protected Button taskStartCheckbox;
	protected Button taskEndCheckbox;
	protected Button newSegmentCheckbox;
	
	
	protected WizardPageNotification()
	{
		super("");
		setTitle("Notification Selection");
		setDescription("Select desired notifications");
	}

	
	public void createControl(Composite parent)
	{
		final Composite body = new Composite(parent, SWT.NONE);
	    GridLayout gl = new GridLayout();
	    gl.numColumns = 1;
	    body.setLayout(gl);
		
	    GridData radioData = new GridData();
	    radioData.verticalIndent = 10;
	    radioData.widthHint = 300;
	    
	    GridData optionsData = new GridData();
		optionsData.horizontalIndent = 10;
		
		// notification options
		notifRadio = new Button(body, SWT.RADIO);
		notifRadio.setLayoutData(radioData);
		notifRadio.setText("Get notified");
		
		Composite uriGroup = new Composite(body, SWT.NONE);
		uriGroup.setLayout(new GridLayout(2, false));
		final Label uriLabel = new Label(uriGroup, SWT.NONE);
		uriLabel.setText("  Notification Endpoint: ");
		uriField = new Text(uriGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		GridData layoutData = new GridData();
		layoutData.minimumWidth = 250;
		layoutData.grabExcessHorizontalSpace = true;
		uriField.setLayoutData(layoutData);
		uriField.setText("myemail@company.org");
		
		taskStartCheckbox = new Button(body, SWT.CHECK);
		taskStartCheckbox.setLayoutData(optionsData);
		taskStartCheckbox.setText("Begining of task");
		
		taskEndCheckbox = new Button(body, SWT.CHECK);
		taskEndCheckbox.setLayoutData(optionsData);
		taskEndCheckbox.setText("End of task");
		
		newSegmentCheckbox = new Button(body, SWT.CHECK);
		newSegmentCheckbox.setLayoutData(optionsData);
		newSegmentCheckbox.setText("New segment acquired");
			
		// polling options
		pollRadio = new Button(body, SWT.RADIO);
		pollRadio.setLayoutData(radioData);
		pollRadio.setText("Automatically poll for status");

		// do nothing
		Button noActionRadio = new Button(body, SWT.RADIO);
		noActionRadio.setLayoutData(radioData);
		noActionRadio.setText("Let me poll manually");
		
		SelectionAdapter radioListener = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				if (((Button)event.widget).getSelection() == true)
				{
					if (event.widget == notifRadio)
						enableFields(true);
					else
						enableFields(false);
				}				
			}			
			
			protected void enableFields(boolean enabled)
			{
				uriLabel.setEnabled(enabled);
				uriField.setEnabled(enabled);
				taskStartCheckbox.setEnabled(enabled);
				taskEndCheckbox.setEnabled(enabled);
				newSegmentCheckbox.setEnabled(enabled);
			}
		};
		
		notifRadio.addSelectionListener(radioListener);
		pollRadio.addSelectionListener(radioListener);
		noActionRadio.addSelectionListener(radioListener);
		notifRadio.setSelection(true);
		
		setControl(body);
	}
	
	
	@Override
	public boolean canFlipToNextPage()
	{
		return false;
	}

}
