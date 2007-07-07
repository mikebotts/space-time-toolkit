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
import org.vast.stt.provider.STTTimeExtent;

public class TimeExtentController implements SelectionListener, TimeSpinnerListener, STTEventListener 
{
	private STTTimeExtent timeExtent;
	private TimeExtentWidget extentWidget;
	//  Get a handle to the SceneTimeUpdater somehow
	private SceneTimeUpdater sceneUpdater;
	
	public TimeExtentController(Composite parent) {
		extentWidget = new TimeExtentWidget(parent);
		extentWidget.addListeners(this, this);
	}
	
	public void setDataItem(DataItem item){
		//  Remove this listener on current timeExtent obj, if it exists
		if (timeExtent!=null)
		    timeExtent.removeListener(this);
		
		extentWidget.mainGroup.setText(item.getName());
		//  reset timeExtent
		timeExtent = item.getDataProvider().getTimeExtent();
		if(timeExtent == null)
			return;
		//  Get a handle to the current updater
		TimeExtentUpdater initialUpdater = timeExtent.getUpdater();
		if(initialUpdater instanceof SceneTimeUpdater) {
			sceneUpdater = (SceneTimeUpdater)initialUpdater;
		} else {
			System.err.println("TimeExtentController- intial updater is RT- FIX THIS!!!!");
		}
			
		extentWidget.biasSpinner.setValue(timeExtent.getTimeBias());
		extentWidget.leadSpinner.setValue(timeExtent.getLeadTimeDelta());
		extentWidget.lagSpinner.setValue(timeExtent.getLagTimeDelta());
		extentWidget.stepSpinner.setValue(timeExtent.getTimeStep());
		extentWidget.manualTimeWidget.absTimeSpinner.setValue(timeExtent.getBaseTime());
		//  Is an updater enabled?  If so, disable controls
		boolean useAutoTime = (timeExtent.getUpdater() != null) && timeExtent.getUpdater().isEnabled();
        extentWidget.manualTimeWidget.setEnabled(!useAutoTime);
        extentWidget.overrideTimeBtn.setSelection(!useAutoTime);
//		//  Fix for baseAtNow btn
		if(timeExtent.isBaseAtNow()) {
			extentWidget.manualTimeWidget.baseAtNowBtn.setSelection(true);
			//  disable masterTimeWidget???
	        //extentWidget.manualTimeWidget.setEnabled(!useAutoTime);
		} 

		//  Make this timeExtent a listener to timeExtent changes so it can refresh it's current time display
		//  if other sources (i.e. RealTimeUpdater/SCeneTimeUpdater) are changing the time value
		timeExtent.addListener(this);
	}
	
	private RealTimeUpdater createRealtimeUpdater(){
		RealTimeUpdater rtUpdater = new RealTimeUpdater();
		rtUpdater.setUpdatePeriod(extentWidget.manualTimeWidget.stepSpinner.getValue());
		
		return rtUpdater;
	}
	
	private void setOverrideSceneTime(boolean b){
		extentWidget.manualTimeWidget.setEnabled(b);
		//	Disable old updater, whether Scene or RT
		timeExtent.getUpdater().setEnabled(false);
		
		//  reset baseTime of timeExtent to overide cal value
		if(b){
			timeExtent.setBaseTime(extentWidget.manualTimeWidget.absTimeSpinner.getValue());
			if (extentWidget.manualTimeWidget.absTimeSpinner.rtBtn.getSelection()) {
				RealTimeUpdater rtu = createRealtimeUpdater();
				rtu.setEnabled(true);
				timeExtent.setUpdater(rtu);
			}
		} else {  //  Revert to scene updater
			sceneUpdater.setEnabled(true);
            timeExtent.setUpdater(sceneUpdater);
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
			//extentWidget.manualTimeWidget.absTimeSpinner.setEnabled(!rt);
			if (rt) {
				extentWidget.manualTimeWidget.absTimeSpinner.disableDateChanges();
				//  disable Calendar spinner controls
				//  disable old updater?
				timeExtent.getUpdater().setEnabled(false);
				//  Use cached RT updater
				RealTimeUpdater rtu = createRealtimeUpdater();
				rtu.setEnabled(true);
				timeExtent.setUpdater(rtu);
				timeExtent.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED));
			} else {
				extentWidget.manualTimeWidget.absTimeSpinner.setEnabled(true);
				//  disable RT updater
				timeExtent.getUpdater().setEnabled(false);
				timeExtent.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED));
			}       
        } else if(e.widget == extentWidget.biasCombo){
			double sense = (extentWidget.biasCombo.getSelectionIndex() == 0) ? 1.0 : -1.0;
			timeExtent.setTimeBias(timeExtent.getTimeBias() * sense);
        } else if(e.widget == extentWidget.continuousUpdateBtn){
        	extentWidget.updateNowBtn.setEnabled(!extentWidget.continuousUpdateBtn.getSelection());
        } else if(e.widget == extentWidget.manualTimeWidget.baseAtNowBtn){
        	timeExtent.setBaseAtNow(extentWidget.continuousUpdateBtn.getSelection());
        	//  How to interact with manual time controls/RT..etc...
        } else if (e.widget == extentWidget.manualTimeWidget.setBtn) {
        	StepSpinnerDialog ssd = new StepSpinnerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			int rc = ssd.getReturnCode();
			if (rc == IDialogConstants.OK_ID) {
				extentWidget.manualTimeWidget.stepSpinner.setValue(ssd.getTimeStep());
				extentWidget.manualTimeWidget.stepSpinner.resetCaret();
			}
        } else {
			System.err.println(e);
		}
	}

	/**
	 * Handle events from the spinners 
	 */
    public void timeChanged(TimeSpinner spinner, double newTime) {
        if (this.timeExtent != null) {
            // update time extent object
            timeExtent.setBaseTime(extentWidget.manualTimeWidget.absTimeSpinner.getValue());
            timeExtent.setLagTimeDelta(extentWidget.lagSpinner.getValue());
            timeExtent.setLeadTimeDelta(extentWidget.leadSpinner.getValue());
            timeExtent.setTimeStep(extentWidget.stepSpinner.getValue());
            timeExtent.setTimeBias(extentWidget.biasSpinner.getValue());
            if (extentWidget.continuousUpdateBtn.getSelection() == true)
                timeExtent.dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED));            
        }
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
}
