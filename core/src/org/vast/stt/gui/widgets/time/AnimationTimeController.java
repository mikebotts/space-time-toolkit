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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.dynamics.RealTimeUpdater;
import org.vast.stt.dynamics.TimeExtentUpdater;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.STTTimeExtent;

public class AnimationTimeController implements SelectionListener, TimeSpinnerListener 
{
	private WorldScene scene;
	private AnimationTimeWidget widget;
	
	public AnimationTimeController(Composite parent, WorldScene scene) {
		this.scene = scene;
		widget = new AnimationTimeWidget(parent, false);
		widget.setTitle("Animation Time");
		widget.addListeners(this, this);
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == widget.startAnimateButton){
			double startTime= widget.beginTimeSpinner.getValue();
			double stopTime = widget.endTimeSpinner.getValue();
			double timeStep = widget.stepTimeSpinner.getValue();
			while (startTime <= stopTime){
				startTime = startTime + timeStep;
				widget.beginTimeSpinner.timeUp();
			}
			
		}
	}

	public void timeChanged(TimeSpinner spinner, double newTime) {
		if(spinner == widget.stepTimeSpinner) {
			double oldTime = widget.beginTimeSpinner.getValue();
			newTime += oldTime;
			widget.beginTimeSpinner.setValue(newTime);
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
			widget.beginTimeSpinner.setValue(sceneTime);
			TimeExtentUpdater updater = extent.getUpdater();
			if (updater instanceof RealTimeUpdater && updater.isEnabled()) {
				//widget.beginTimeSpinner.rtBtn.setSelection(true);
				widget.beginTimeSpinner.disableDateChanges();
				double updatePd = ((RealTimeUpdater) updater).getUpdatePeriod();
				widget.stepTimeSpinner.setValue(updatePd);
			} else {
				//widget.beginTimeSpinner.rtBtn.setSelection(false);
				widget.setEnabled(true);
				double stepTime = extent.getTimeStep();
				widget.stepTimeSpinner.setValue(stepTime);
			}
		}
	}

}
