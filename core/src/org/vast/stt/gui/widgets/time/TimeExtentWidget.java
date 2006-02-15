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
import org.vast.stt.scene.DataItem;
import org.vast.stt.util.TimeExtent;

public class TimeExtentWidget implements SelectionListener
{	
	Label itemLabel;
	TimeZoneCombo tzCombo;
	Button useAbsTimeBtn;
	Button continusUpdateBtn;
	Button updateNowBtn;
	TimeSpinner biasSpinner;
	private DataItem dataItem;
	private Group mainGroup;
	
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

		TimeSpinner stepSpinner = new TimeSpinner(mainGroup, "Time Step");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		stepSpinner.setLayoutData(gridData);

		TimeSpinner leadSpinner = new TimeSpinner(mainGroup, "Delta Lead");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		leadSpinner.setLayoutData(gridData);

		TimeSpinner lagSpinner = new TimeSpinner(mainGroup, "Delta Lag");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		lagSpinner.setLayoutData(gridData);

		//  Abs Time uses different spinner...
		CalendarSpinner absTimeSpinner = new CalendarSpinner(mainGroup, "Absolute Time");
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
		
		continusUpdateBtn = new Button(mainGroup, SWT.CHECK);
		continusUpdateBtn.setText("Enable Continuous Update");
		continusUpdateBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;   
		gridData.verticalIndent = 10;
		//gridData.verticalSpan = 2;
		continusUpdateBtn.setLayoutData(gridData);
		
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
		this.dataItem = item;
		mainGroup.setText(item.getName());
		//TimeExtent timeExtent = dataItem.getDataProvider().getTimeExtent();
		//timeExtent.
	}
	

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		if(e.widget == useAbsTimeBtn){
			//  item.useAbsTime;
		} else if (e.widget == continusUpdateBtn) {
			//  item.enableContUpdate;
		} else if(e.widget == updateNowBtn){
			//  item.setUpdateNow;
		} else {
			System.err.println(e);
		}
	}
	
}
