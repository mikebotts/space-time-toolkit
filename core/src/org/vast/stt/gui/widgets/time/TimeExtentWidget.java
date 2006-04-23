package org.vast.stt.gui.widgets.time;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.vast.stt.data.DataException;
import org.vast.stt.scene.DataItem;
import org.vast.stt.util.TimeExtent;

public class TimeExtentWidget implements SelectionListener
{	
	Label itemLabel;
	TimeZoneCombo tzCombo;
	Button useAbsTimeBtn;
	Button continuousUpdateBtn;
	Button updateNowBtn;
	TimeSpinner biasSpinner;
	private DataItem dataItem;
	private Group mainGroup;
	private TimeSpinner stepSpinner;
	private TimeSpinner leadSpinner;
	private TimeSpinner lagSpinner;
	private CalendarSpinner absTimeSpinner;
	
	public TimeExtentWidget(Composite parent) {
		init(parent);
	}

	public void init(Composite parent){
		//  Scrolled Composite to hold everything
		ScrolledComposite scroller = 
			new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
	    scroller.setExpandVertical(true);
		scroller.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

	    mainGroup = new Group(scroller, 0x0);
		mainGroup.setText("itemName");
		scroller.setContent(mainGroup);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 6;
		mainGroup.setLayout(layout);
		
		//  Create Spinners
		biasSpinner = new TimeSpinner(mainGroup, "Time Bias");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		biasSpinner.setLayoutData(gridData);

		stepSpinner = new TimeSpinner(mainGroup, "Time Step");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		stepSpinner.setLayoutData(gridData);

		leadSpinner = new TimeSpinner(mainGroup, "Delta Lead");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		leadSpinner.setLayoutData(gridData);

		lagSpinner = new TimeSpinner(mainGroup, "Delta Lag");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		lagSpinner.setLayoutData(gridData);

		absTimeSpinner = new CalendarSpinner(mainGroup, "Absolute Time");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.verticalIndent = 10;
		absTimeSpinner.setLayoutData(gridData);
		//absTimeSpinner.setEnabled(false);
		
		//  Abs Time Zone - may combine with absTimeSpinner into a single class
		Composite tzComp = new Composite(mainGroup, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		tzComp.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;   //  not honored???
		tzComp.setLayoutData(gridData);
		
		Label tzLabel = new Label(tzComp, SWT.NONE);
		tzLabel.setText("Absolute Time Zone:");
		
		tzCombo = new TimeZoneCombo(tzComp);
		tzCombo.addSelectionListener(this);
		
		//  Add UseAbsTime toggle
		useAbsTimeBtn = new Button(mainGroup, SWT.CHECK);
		useAbsTimeBtn.setText("Use Absolute Time");
		useAbsTimeBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;   //  not honored???
		useAbsTimeBtn.setLayoutData(gridData);
		
		continuousUpdateBtn = new Button(mainGroup, SWT.CHECK);
		continuousUpdateBtn.setText("Enable Continuous Update");
		continuousUpdateBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;   
		gridData.verticalIndent = 10;
		//gridData.verticalSpan = 2;
		continuousUpdateBtn.setLayoutData(gridData);
		
		//  Update now btn
		updateNowBtn = new Button(mainGroup, SWT.PUSH);
		updateNowBtn.setText("Update Now");
		updateNowBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;  
		gridData.verticalIndent = 8;
		updateNowBtn.setLayoutData(gridData);
		
		//  Must give sroller sufficient size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
	}

	public void setDataItem(DataItem item){
		mainGroup.setText(item.getName());
		this.dataItem = item;
		mainGroup.setText(item.getName());
		//  set the fields 
		TimeExtent timeExtent = dataItem.getDataProvider().getTimeExtent();
		if(timeExtent == null)
			return;
		biasSpinner.setValue(timeExtent.getTimeBias());
		leadSpinner.setValue(timeExtent.getLeadTimeDelta());
		lagSpinner.setValue(timeExtent.getLagTimeDelta());
		stepSpinner.setValue(timeExtent.getTimeStep());
		absTimeSpinner.setValue(timeExtent.getAbsoluteTime());
		useAbsTimeBtn.setSelection(timeExtent.getUseAbsoluteTime());
		//timeExtent.getAbsoluteTimeZone();
		//  
		//continuousUpdateBtn.setSelection(timeExtent.getContinuousUpdate());
	}
	

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		if(e.widget == useAbsTimeBtn){
			TimeExtent timeExtent = dataItem.getDataProvider().getTimeExtent();
			timeExtent.setUseAbsoluteTime(useAbsTimeBtn.getSelection());
		} else if (e.widget == continuousUpdateBtn) {
			boolean contUp = continuousUpdateBtn.getSelection();
			TimeExtent timeExtent = dataItem.getDataProvider().getTimeExtent();
			if(contUp) {
				//timeExtent.setContinuousUpdate(true);
				updateNowBtn.setEnabled(false);
			} else {
				//timeExtent.setContinuousUpdate(false);
				updateNowBtn.setEnabled(true);
			}
		} else if(e.widget == updateNowBtn){
			try {
				dataItem.getDataProvider().updateData();
			} catch (DataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			System.err.println(e);
		}
	}
	
}
