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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import com.spotimage.stt.sps.provider.NotificationOptions;


/**
 * <p><b>Title:</b>
 * Notifications Wizard
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO NotificationsWizard type description
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Feb 10, 2009
 * @version 1.0
 */
public class NotificationsWizard extends Wizard
{
	protected NotificationOptions notificationOptions;
	
	
	public NotificationsWizard()
	{
		this.setWindowTitle("Notification Options");
	}
	
	
	@Override
	public void addPages()
	{
		WizardPage page = new WizardPageNotification();
		addPage(page);
	}

	
	@Override
	public boolean canFinish()
	{
		return true;
	}
	
	
	@Override
	public boolean performFinish()
	{					
		notificationOptions = new NotificationOptions();
		WizardPageNotification page = (WizardPageNotification)this.getPages()[0];
		
		if (page.notifRadio.getSelection())
		{
			notificationOptions.destUri = page.uriField.getText();
					
			String topicList = "";
			if (page.taskStartCheckbox.getSelection())
				topicList += "sps:TASK_ACCEPTED|";
			if (page.taskEndCheckbox.getSelection())
				topicList += "sps:TASK_COMPLETED|";
			if (page.newSegmentCheckbox.getSelection())
				topicList += "eo:SEGMENT_VALIDATED|";
			
			if (topicList.length() > 0)
				notificationOptions.topicList = topicList.substring(0, topicList.length()-1);
		}
		else
			notificationOptions.pollAuto = page.pollRadio.getSelection();
		
		return true;
	}


	public NotificationOptions getNotificationOptions()
	{
		return notificationOptions;
	}

}
