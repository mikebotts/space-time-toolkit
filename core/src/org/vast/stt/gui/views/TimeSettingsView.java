package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.data.DataProvider;
import org.vast.stt.gui.widgets.CalendarSpinner;
import org.vast.stt.gui.widgets.TimeSpinner;
import org.vast.stt.gui.widgets.TimeZoneCombo;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataItem;

/**
 * <p><b>Title:</b><br/>
 * TimeSettingsView
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	View for manipulating the TimeSettings of DataItems.   
 *
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 19, 2005
 * @version 1.0
 * 
 * @TODO  add +/- to Bias
 * @TODO  add control for buttons
 */
public class TimeSettingsView extends ViewPart implements ISelectionListener, SelectionListener{
	
	public static final String ID = "STT.TimeSettingsView";
	Label itemLabel;
	TimeZoneCombo tzCombo;
	Button useAbsTimeBtn;
	Button continusUpdateBtn;
	Button updateNowBtn;
	TimeSpinner biasSpinner;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		initView(parent);
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);		
	}
	
	public void initView(Composite parent){
		//  Scrolled Composite to hold everything
		ScrolledComposite scroller = new ScrolledComposite(parent, SWT.SHADOW_ETCHED_OUT | SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
	    scroller.setExpandVertical(true);

	    //  Main Group Holds Label, TimeSpinners, etc.
		Group mainGroup = new Group(scroller, SWT.SHADOW_NONE);
		scroller.setContent(mainGroup);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 4;
		mainGroup.setLayout(layout);
		
		//  Create Label
		itemLabel = new Label(mainGroup, SWT.SHADOW_IN);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		itemLabel.setLayoutData(gridData);
		
		//  Create Spinners
		biasSpinner = new TimeSpinner(mainGroup, "Time Bias");
		gridData = new GridData();
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
		gridData.verticalIndent = 12;
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
		gridData.verticalIndent = 12;
		//gridData.verticalSpan = 2;
		continusUpdateBtn.setLayoutData(gridData);
		
		//  Update now btn
		updateNowBtn = new Button(mainGroup, SWT.PUSH);
		updateNowBtn.setText("Update Now");
		updateNowBtn.addSelectionListener(this);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;   
		updateNowBtn.setLayoutData(gridData);
		
		
		//  Must give sroller sufficient size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		if (part instanceof SceneTreeView)
		{
			DataEntry selectedItem = (DataEntry)((IStructuredSelection)selection).getFirstElement();
			if(selectedItem instanceof DataItem) { 
				System.err.println("item " + selectedItem);
				itemLabel.setText(selectedItem.getName());
				DataProvider prov = ((DataItem)selectedItem).getDataProvider();
				//  If provider is null, this widget isn't supported.
				if(prov!=null) {
				}
			} else {
				//  TODO  support BBox for DataItemList
			}
		}		
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
