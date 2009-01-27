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
import org.vast.stt.dynamics.SceneTimeUpdater;
import org.vast.stt.dynamics.TimeExtentUpdater;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.STTTimeExtent;

public class TimeExtentController implements SelectionListener, TimeSpinnerListener, STTEventListener 
{
	private STTTimeExtent timeExtent;
	private TimeExtentWidget extentWidget;
	private WorldScene worldScene;
	
	public TimeExtentController(Composite parent) {
		extentWidget = new TimeExtentWidget(parent);
		extentWidget.addListeners(this, this);
	}
	
	public void setDataItem(DataItem item){
		//  Remove this listener on current timeExtent obj, if it exists
		if (timeExtent!=null)
		    timeExtent.removeListener(this);
		
		extentWidget.mainGroup.setText(item.getName());
		//  get timeExtent from new dataItem
		timeExtent = item.getDataProvider().getTimeExtent();
		if(timeExtent == null)
			return;
			
		//  Reset widget values based on new timeExtent object state
		extentWidget.setBiasValue(timeExtent.getTimeBias());
		extentWidget.leadSpinner.setValue(timeExtent.getLeadTimeDelta());
		extentWidget.lagSpinner.setValue(timeExtent.getLagTimeDelta());
		extentWidget.stepSpinner.setValue(timeExtent.getTimeStep());
		
		//  Is an updater enabled?  If so, disable controls
		boolean useAutoTime = (timeExtent.getUpdater() != null) && timeExtent.getUpdater().isEnabled();
		extentWidget.manualTimeWidget.absTimeSpinner.setValue(timeExtent.getBaseTime());
        extentWidget.manualTimeWidget.setEnabled(!useAutoTime);
        extentWidget.overrideTimeBtn.setSelection(!useAutoTime);
        
		//  Fix for baseAtNow btn
		if(timeExtent.isBaseAtNow()) {
			extentWidget.manualTimeWidget.baseAtNowBtn.setSelection(true);
			//  disable masterTimeWidget???
	        //extentWidget.manualTimeWidget.setEnabled(!useAutoTime);
		} 

		//  Make this Controller a listener to timeExtent changes so it can refresh it's current time display
		//  if other sources (i.e. RealTimeUpdater/SCeneTimeUpdater) are changing the time value
		timeExtent.addListener(this);
	}
	
	private RealTimeUpdater createRealtimeUpdater(){
		RealTimeUpdater rtUpdater = new RealTimeUpdater();
		rtUpdater.setUpdatePeriod(extentWidget.manualTimeWidget.stepTimeSpinner.getValue());
		
		return rtUpdater;
	}
	
	private SceneTimeUpdater createSceneTimeUpdater(){
		SceneTimeUpdater stUpdater = new SceneTimeUpdater(worldScene);
		stUpdater.setTimeExtent(timeExtent);
		
		return stUpdater;
	}
	
	private void setOverrideSceneTime(boolean b){
		extentWidget.manualTimeWidget.setEnabled(b);
		//	disable old updater (this should ensure old updater Thread terminates)
		TimeExtentUpdater updater = timeExtent.getUpdater();
		if(updater != null) {
			updater.setEnabled(false);
			updater = null;
		}
		
		// switch between RealTime or SceneTime updater
		if(b){
			// revert to override value if it was specified
		    if (timeExtent.getDefaultBaseTime() != Double.NaN)
			    timeExtent.setBaseTime(timeExtent.getDefaultBaseTime());
			
			if (extentWidget.manualTimeWidget.absTimeSpinner.rtBtn.getSelection()) {
				RealTimeUpdater rtu = createRealtimeUpdater();
				rtu.setEnabled(true);
				timeExtent.setUpdater(rtu);
			} else
	            extentWidget.manualTimeWidget.addListeners(this, this);
		} else { 
            extentWidget.manualTimeWidget.removeListeners(this, this);
			SceneTimeUpdater sceneUpdater = createSceneTimeUpdater();
			sceneUpdater.setEnabled(true);
            timeExtent.setUpdater(sceneUpdater);
            
            // backup override value
            timeExtent.setDefaultBaseTime(timeExtent.getBaseTime());
		}
		timeExtent.dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED));
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if(timeExtent == null)
			return;
		if(e.widget == extentWidget.overrideTimeBtn){
            setOverrideSceneTime(extentWidget.overrideTimeBtn.getSelection()); 
		} else if(e.widget == extentWidget.updateNowBtn){
            timeExtent.dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED));
		} else if(e.widget == extentWidget.manualTimeWidget.absTimeSpinner.rtBtn){
			boolean rt = extentWidget.manualTimeWidget.absTimeSpinner.rtBtn.getSelection();
			if (rt) {
				extentWidget.manualTimeWidget.absTimeSpinner.disableDateChanges();
				//  disable Calendar spinner controls
				//  disable old updater (this should ensure old updater Thread terminates)
				timeExtent.getUpdater().setEnabled(false);
				//  create new RT updater
				RealTimeUpdater rtu = createRealtimeUpdater();
				rtu.setEnabled(true);
				timeExtent.setUpdater(rtu);
				timeExtent.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED));
			} else {
				extentWidget.manualTimeWidget.absTimeSpinner.setEnabled(true);
				//   disable old updater (this should ensure old updater Thread terminates)
				timeExtent.getUpdater().setEnabled(false);
				timeExtent.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED));
			}       
        } else if(e.widget == extentWidget.biasCombo){
			timeExtent.setTimeBias(Math.abs(timeExtent.getTimeBias()) * extentWidget.getBiasSense());
			timeExtent.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED));
        } else if(e.widget == extentWidget.continuousUpdateBtn){
        	extentWidget.updateNowBtn.setEnabled(!extentWidget.continuousUpdateBtn.getSelection());
        	if(extentWidget.continuousUpdateBtn.getSelection())
        		timeExtent.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED));
        } else if(e.widget == extentWidget.manualTimeWidget.baseAtNowBtn){
        	timeExtent.setBaseAtNow(extentWidget.continuousUpdateBtn.getSelection());
        	//  How to interact with manual time controls/RT..etc...
        } else if (e.widget == extentWidget.manualTimeWidget.setBtn) {
        	StepSpinnerDialog ssd = new StepSpinnerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
        			extentWidget.manualTimeWidget.stepTimeSpinner.getValue());
			int rc = ssd.getReturnCode();
			if (rc == IDialogConstants.OK_ID) {
				extentWidget.manualTimeWidget.stepTimeSpinner.setValue(ssd.getTimeStep());
			}
        } else {
			System.err.println(e);
		}
	}

	/**
	 * Handle events from the spinners 
	 */
    public void timeChanged(TimeSpinner spinner, double newTime) {
    	if (this.timeExtent == null)
        	return;
    	//  If this event came from stepTime spinner, advance time
    	//  by the  current value of step 
    	if(spinner == extentWidget.manualTimeWidget.stepTimeSpinner){
    		double baseTime = extentWidget.manualTimeWidget.absTimeSpinner.getValue();
    		baseTime += newTime;
    		extentWidget.manualTimeWidget.absTimeSpinner.setValue(baseTime);
    	}
        // update time extent object 
    	if(extentWidget.getOverrideSceneTime()) {
    		double newBaseTime = extentWidget.manualTimeWidget.absTimeSpinner.getValue();
    		timeExtent.setBaseTime(newBaseTime);
    		timeExtent.setDefaultBaseTime(newBaseTime);
    	}
        timeExtent.setLagTimeDelta(extentWidget.lagSpinner.getValue());
        timeExtent.setLeadTimeDelta(extentWidget.leadSpinner.getValue());
        timeExtent.setTimeStep(extentWidget.stepSpinner.getValue());
        timeExtent.setTimeBias(extentWidget.biasSpinner.getValue() * extentWidget.getBiasSense());
        if (extentWidget.continuousUpdateBtn.getSelection() == true)
            timeExtent.dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED));            
    }

	/**
	 * handleEvent - for events coming from timeExtent object - Change
	 * 			     the override calendar spinner value ONLY if this is 
	 *               a RT updater AND overrideSceneTime is enabled  
	 */
	public void handleEvent(STTEvent e) {
	    switch (e.type) {
	        case TIME_EXTENT_CHANGED:
	        	if(!(timeExtent.getUpdater() instanceof RealTimeUpdater))
	        		return;

	            if (e.source !=  extentWidget.manualTimeWidget.absTimeSpinner) {
	            	ChangeTime fs = new ChangeTime();
	            	PlatformUI.getWorkbench().getDisplay().asyncExec(fs);
	            }
	            break;
	    }
	}
	    
    class ChangeTime implements Runnable
    {
        public void run(){
        	if(!extentWidget.overrideTimeBtn.getSelection())
        		return;
        	extentWidget.manualTimeWidget.absTimeSpinner.setValue(timeExtent.getAdjustedTime());
        }
    }

    /*
     * Set the current worldScene so we can create SceneTimeUpdaters
     */
	public void setScene(WorldScene worldScene) {
		this.worldScene = worldScene;
	}
}
