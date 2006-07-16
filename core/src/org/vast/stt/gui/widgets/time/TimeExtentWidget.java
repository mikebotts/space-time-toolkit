package org.vast.stt.gui.widgets.time;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.data.DataException;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.TimeExtent;

public class TimeExtentWidget implements SelectionListener, TimeListener
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
	private Combo biasCombo;
	final Color BLUE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
	final Color GREEN = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GREEN);
	final Color RED = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED);
	final Color WHITE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE);

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
		layout.numColumns = 2;
		layout.verticalSpacing = 6;
		layout.makeColumnsEqualWidth = false;
		mainGroup.setLayout(layout);
		
		//  Spinners
		biasCombo = new Combo(mainGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		biasCombo.setItems(new String[]{"+", "-"});
		biasCombo.select(0);
		//biasCombo.setTextLimit(1);
		biasCombo.setForeground(BLUE);
		biasCombo.addSelectionListener(this);
		GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gridData.horizontalIndent = 20;
		biasCombo.setLayoutData(gridData);
		biasSpinner = new TimeSpinner(mainGroup, "Time Bias");
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gridData.horizontalAlignment = SWT.RIGHT;
		//biasSpinner.setBackground(GREEN);
		biasSpinner.setLayoutData(gridData);
        biasSpinner.addTimeListener(this);
		
		stepSpinner = new TimeSpinner(mainGroup, "Time Step");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.horizontalSpan = 2;
		stepSpinner.setLayoutData(gridData);
        stepSpinner.addTimeListener(this);
        
		leadSpinner = new TimeSpinner(mainGroup, "Delta Lead");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.horizontalSpan = 2;
		leadSpinner.setLayoutData(gridData);
        leadSpinner.addTimeListener(this);

		lagSpinner = new TimeSpinner(mainGroup, "Delta Lag");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.horizontalSpan = 2;
		lagSpinner.setLayoutData(gridData);
        lagSpinner.addTimeListener(this);

		absTimeSpinner = new CalendarSpinner(mainGroup, "Absolute Time", SWT.VERTICAL);
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
		gridData.verticalIndent = 10;
		absTimeSpinner.setLayoutData(gridData);
        absTimeSpinner.addTimeListener(this);

		//  Add UseAbsTime toggle
		useAbsTimeBtn = new Button(mainGroup, SWT.CHECK);
		useAbsTimeBtn.setText("Use Absolute Time");
		useAbsTimeBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;  
		gridData.horizontalSpan = 2;
		useAbsTimeBtn.setLayoutData(gridData);
		//  TODO  init with real dataItem
		//setUseAbsoluteTime(dataItem.getDataProvider().getTimeExtent().getUseAbsoluteTime());
		setUseAbsoluteTime(false);
		
		continuousUpdateBtn = new Button(mainGroup, SWT.CHECK);
		continuousUpdateBtn.setText("Enable Continuous Update");
		continuousUpdateBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;   
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 10;
		continuousUpdateBtn.setLayoutData(gridData);
		
		//  Update now btn
		updateNowBtn = new Button(mainGroup, SWT.PUSH);
		updateNowBtn.setText("Update Now");
		updateNowBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;  
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 8;
		updateNowBtn.setLayoutData(gridData);

		//  Must give sroller sufficient size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
	}

	private void setUseAbsoluteTime(boolean b){
		//TimeExtent timeExtent = dataItem.getDataProvider().getTimeExtent();
		//timeExtent.setUseAbsoluteTime(b);
		absTimeSpinner.setEnabled(b);
		//absTimeSpinner.setBackground()
	}
	
	public void setDataItem(DataItem item){
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
		absTimeSpinner.setValue(timeExtent.getBaseTime());//timeExtent.getAbsoluteTime());
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
			setUseAbsoluteTime(useAbsTimeBtn.getSelection());
		} else if (e.widget == continuousUpdateBtn) {
			boolean contUp = continuousUpdateBtn.getSelection();
			//TimeExtent timeExtent = dataItem.getDataProvider().getTimeExtent();
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
		} else if(e.widget == biasCombo){
			TimeExtent timeExtent = dataItem.getDataProvider().getTimeExtent();
			double sense = (biasCombo.getSelectionIndex() == 0) ? 1.0 : -1.0;
			timeExtent.setTimeBias(timeExtent.getTimeBias() * sense);
		} else {
			System.err.println(e);
		}
	}

    public void timeChanged(TimeSpinner spinner, double newTime)
    {
        if (this.dataItem != null)
        {
            // update time extent object
            TimeExtent timeExtent = dataItem.getDataProvider().getTimeExtent();
            timeExtent.setAbsoluteTime(absTimeSpinner.getValue());
            timeExtent.setBaseTime(absTimeSpinner.getValue());
            timeExtent.setLagTimeDelta(lagSpinner.getValue());
            timeExtent.setLeadTimeDelta(leadSpinner.getValue());

            if (continuousUpdateBtn.getSelection())
            {
                this.dataItem.getDataProvider().clearData();
                this.dataItem.getDataProvider().forceUpdate();
            }
        }
    }	
}
