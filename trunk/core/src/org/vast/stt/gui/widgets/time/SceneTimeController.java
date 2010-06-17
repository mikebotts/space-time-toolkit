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

package org.vast.stt.gui.widgets.time;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.dynamics.RealTimeUpdater;
import org.vast.stt.dynamics.TimeExtentUpdater;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.STTTimeExtent;

public class SceneTimeController implements SelectionListener, TimeSpinnerListener 
{
	private WorldScene scene;
	private MasterTimeWidget widget;
	
	public SceneTimeController(Composite parent, WorldScene scene) {
		this.scene = scene;
		widget = new MasterTimeWidget(parent, false);
		widget.setTitle("Scene Time");
		widget.addListeners(this, this);
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == widget.absTimeSpinner.rtBtn) {
			if (scene == null)
				return;
			boolean rt = widget.absTimeSpinner.rtBtn.getSelection();
			if(rt)
				widget.absTimeSpinner.disableDateChanges();
			else
				widget.absTimeSpinner.setEnabled(true);
			if (rt) {
				STTTimeExtent extent = scene.getTimeExtent();
				//  Just create new one every time for now....
				RealTimeUpdater updater = new RealTimeUpdater();
				updater.setUpdatePeriod(widget.stepTimeSpinner.getValue());
				updater.setEnabled(true);
				extent.setUpdater(updater);
				extent.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED), false);
			} else {
				STTTimeExtent extent = scene.getTimeExtent();
				extent.getUpdater().setEnabled(false);
			}
		} else if (e.widget == widget.setBtn) {
			//  popup setTimeStep Spinner
			StepSpinnerDialog ssd = new StepSpinnerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
					widget.stepTimeSpinner.getValue());
			int rc = ssd.getReturnCode();
			if (rc == IDialogConstants.OK_ID) {
				widget.stepTimeSpinner.setValue(ssd.getTimeStep());
			}
		}
	}

	public void timeChanged(TimeSpinner spinner, double newTime) {
		if(spinner == widget.stepTimeSpinner) {
			double oldTime = widget.absTimeSpinner.getValue();
			newTime += oldTime;
			widget.absTimeSpinner.setValue(newTime);
		}
		if (scene == null)
			return;
		scene.getTimeExtent().setBaseTime(newTime);
		scene.getTimeExtent().dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED), false);
	}

	public void setScene(WorldScene scene) {
		this.scene = scene;

		if (scene != null) {
			STTTimeExtent extent = scene.getTimeExtent();
			double sceneTime = extent.getBaseTime();
			widget.absTimeSpinner.setValue(sceneTime);
			TimeExtentUpdater updater = extent.getUpdater();
			if (updater instanceof RealTimeUpdater && updater.isEnabled()) {
				widget.absTimeSpinner.rtBtn.setSelection(true);
				widget.absTimeSpinner.disableDateChanges();
				double updatePd = ((RealTimeUpdater) updater).getUpdatePeriod();
				widget.stepTimeSpinner.setValue(updatePd);
			} else {
				widget.absTimeSpinner.rtBtn.setSelection(false);
				widget.setEnabled(true);
				//  temporarily commenting out- this is causing timeStep to get reset when Widget gets refreshed- TC, 6/16/2010
//				double stepTime = extent.getTimeStep();
//				widget.stepTimeSpinner.setValue(stepTime);
			}
		}
	}

}
