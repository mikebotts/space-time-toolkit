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

public final class MasterTimeWidget implements SelectionListener { //, ValueChangedListener {
	Group mainGroup;
	private CalendarSpinner absTimeSpinner;
	private TimeZoneCombo tzCombo;
	private TimeSpinner stepSpinner;
	private Button rtBtn;
	private Button setBtn;
	
	public MasterTimeWidget(Composite parent) {
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
		
		//  Time Step
		stepSpinner = new TimeSpinner(mainGroup, "Time Step");
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
		// TODO Auto-generated method stub
		if(e.widget == tzCombo.getCombo()){
			//
		} else if (e.widget == rtBtn) {
			//  start/stop realtime mode
		} else if (e.widget == setBtn) {
			//  popup setTimeStep Spinner
		}
	}

}
