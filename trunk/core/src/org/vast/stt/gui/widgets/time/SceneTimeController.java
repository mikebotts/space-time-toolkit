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
				updater.setUpdatePeriod(widget.stepSpinner.getValue());
				updater.setEnabled(true);
				extent.setUpdater(updater);
				extent.dispatchEvent(new STTEvent(this,	EventType.TIME_EXTENT_CHANGED));
			} else {
				STTTimeExtent extent = scene.getTimeExtent();
				extent.getUpdater().setEnabled(false);
			}
		} else if (e.widget == widget.setBtn) {
			//  popup setTimeStep Spinner
			StepSpinnerDialog ssd = new StepSpinnerDialog(PlatformUI
					.getWorkbench().getDisplay().getActiveShell());
			int rc = ssd.getReturnCode();
			if (rc == IDialogConstants.OK_ID) {
				widget.stepSpinner.setValue(ssd.getTimeStep());
				widget.stepSpinner.resetCaret();
			}
		}
	}

	public void timeChanged(TimeSpinner spinner, double newTime) {
		if (scene == null)
			return;
		scene.getTimeExtent().setBaseTime(newTime);
		scene.getTimeExtent().dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED));
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
				double updatePd = ((RealTimeUpdater) updater).getUpdatePeriod();
				widget.stepSpinner.setValue(updatePd);
			} else {
				widget.absTimeSpinner.rtBtn.setSelection(false);
				double stepTime = extent.getTimeStep();
				widget.stepSpinner.setValue(stepTime);
			}
		}
	}

}
