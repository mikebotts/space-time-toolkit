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
import org.vast.stt.apps.STTConfig;
import org.vast.stt.data.DataException;
import org.vast.stt.project.DataProvider;
import org.vast.stt.scene.DataEntryList;
import org.vast.stt.scene.DataItem;
import org.vast.stt.scene.DataItemIterator;
import org.vast.stt.scene.Scene;

public final class MasterTimeWidget implements SelectionListener, TimeListener
{
	Group mainGroup;
	private CalendarSpinner absTimeSpinner;
	private TimeSpinner stepSpinner;
	private Button rtBtn;
	private Button setBtn;
	private double timeStep = 60.0; //  timeStep in seconds
	
	public MasterTimeWidget(Composite parent) {
		init(parent);
		//stepSpinner.setValue(timeStep);
		stepSpinner.resetCaret();
	}

	public void init(Composite parent){
		//  Scrolled Composite to hold everything
		ScrolledComposite scroller = 
			new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
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

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == rtBtn) {
			//  TODO start/stop realtime mode
		} else if (e.widget == setBtn) {
			//  popup setTimeStep Spinner
			StepSpinnerDialog ssd = new StepSpinnerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			int rc = ssd.getReturnCode();
			if(rc == IDialogConstants.OK_ID){
				stepSpinner.setValue(ssd.getTimeStep());
				stepSpinner.resetCaret();
			}
		}
	}

	public void timeChanged(double newTime) {
		//  TODO  mod, support multiple Scenes (after Scene is inited)
		Scene scene = STTConfig.getInstance().getCurrentProject().getSceneList().get(0);
		//  Do I need to reset scene's TimeSettings here?
		//scene.setTimeSettings();
		//
		DataEntryList dataList = scene.getDataList();
		DataItemIterator dit = dataList.getItemIterator();
		DataItem itemTmp = null;
		DataProvider provTmp = null;
		while(dit.hasNext()){
			itemTmp = dit.next();
			provTmp = itemTmp.getDataProvider();
			if(provTmp.isTimeSubsetSupported() && itemTmp.isEnabled()) {
				provTmp.getTimeExtent().setBaseTime(newTime);
				System.err.println("New Time for: " + itemTmp.getName() + " = " + newTime);
				try {
					provTmp.updateData();
				} catch (DataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
