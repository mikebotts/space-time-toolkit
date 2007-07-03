package org.vast.stt.gui.widgets.time;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.dynamics.RealTimeUpdater;
import org.vast.stt.dynamics.TimeExtentUpdater;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.STTTimeExtent;

/**
 * <p><b>Title:</b><br/>
 * MasterTimeWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Widget for MasterTime spinner, stepSpinner, realTime button
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 */
public final class MasterTimeWidget implements SelectionListener, TimeListener
{
    private Group mainGroup;
    private CalendarSpinner absTimeSpinner;
    private TimeSpinner stepSpinner;
    private Button rtBtn;
    private Button setBtn;
    private double timeStep = 60.0; //  timeStep in seconds
    private WorldScene scene;


    public MasterTimeWidget(Composite parent)
    {
        init(parent);
        //stepSpinner.setValue(timeStep);
        stepSpinner.resetCaret();
    }

    public void init(Composite parent)
    {
        //  Scrolled Composite to hold everything
        ScrolledComposite scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

        mainGroup = new Group(scroller, 0x0);
        mainGroup.setText("Master Time");
        scroller.setContent(mainGroup);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 6;
        mainGroup.setLayout(layout);

        //  MastermTime
        absTimeSpinner = new CalendarSpinner(mainGroup, "Master Time");
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.RIGHT;
        gridData.horizontalSpan = 2;
        absTimeSpinner.setLayoutData(gridData);
        absTimeSpinner.addTimeListener(this);

        //  Time Step
        stepSpinner = new TimeSpinner(mainGroup, "Time Step");
        stepSpinner.setValue(timeStep);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.RIGHT;
        stepSpinner.setLayoutData(gridData);

        setBtn = new Button(mainGroup, SWT.PUSH);
        setBtn.setText("Set");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.LEFT;
        setBtn.setLayoutData(gridData);
        setBtn.addSelectionListener(this);

        //  RT toggle
        rtBtn = new Button(mainGroup, SWT.CHECK);
        rtBtn.setText("Real Time mode");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.CENTER;
        gridData.horizontalSpan = 2;
        rtBtn.setLayoutData(gridData);
        rtBtn.addSelectionListener(this);
        
        //  Must give sroller sufficient size
        scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    public void widgetSelected(SelectionEvent e)
    {
        if (e.widget == rtBtn)
        {
        	if(scene == null)
        		return;
        	if(rtBtn.getSelection()) {
        		STTTimeExtent extent = scene.getTimeExtent();
        		//  Just create new one every time for now....
        		RealTimeUpdater updater = new RealTimeUpdater();
        		updater.setUpdatePeriod(stepSpinner.getValue());
        		updater.setEnabled(true);
    	        extent.setUpdater(updater);
	            extent.dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED));
        	} else {
        		STTTimeExtent extent = scene.getTimeExtent();
        		extent.getUpdater().setEnabled(false);
        	}
        }
        else if (e.widget == setBtn)
        {
            //  popup setTimeStep Spinner
            StepSpinnerDialog ssd = new StepSpinnerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
            int rc = ssd.getReturnCode();
            if (rc == IDialogConstants.OK_ID)
            {
                stepSpinner.setValue(ssd.getTimeStep());
                stepSpinner.resetCaret();
            }
        }
    }


    public void timeChanged(TimeSpinner spinner, double newTime)
    {
    	if(scene == null)
    		return;
        scene.getTimeExtent().setBaseTime(newTime);
        scene.getTimeExtent().dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED));
    }


    public void setScene(WorldScene scene)
    {
        this.scene = scene;
        
        if (scene != null)
        {
        	STTTimeExtent extent = scene.getTimeExtent();
            double sceneTime = extent.getBaseTime();
            absTimeSpinner.setValue(sceneTime);
            TimeExtentUpdater updater = extent.getUpdater();
            if(updater instanceof RealTimeUpdater && updater.isEnabled()){
            	rtBtn.setSelection(true);
            	double updatePd = ((RealTimeUpdater)updater).getUpdatePeriod();
            	stepSpinner.setValue(updatePd);
            } else {
            	rtBtn.setSelection(false);
                double stepTime = extent.getTimeStep();
                stepSpinner.setValue(stepTime);
            }
        }
        
    }
}
