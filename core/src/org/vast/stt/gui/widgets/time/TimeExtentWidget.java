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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

/**
 * <p><b>Title:</b><br/>
 * TimeExtentWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Widget containing ManualTimeWidget and TimeExtentSpinners.  
 *  It's a little busy and may be confusing to users, but it does allow
 *  for precise control of all item time parameters.  One solution may 
 *  be to break ManualTimeWidget and TimeExtentSpinners into separate 
 *  Views, or make this a TabbedFolder with one tab for ManualTime control
 *  and one for Spinners.  
 *  
 *  Also, it took some hammering to get the things to align and look halfway decent.
 *  Expect inconsistencies when porting to Linux/Mac- if so, consider a FormLayout 
 *  next time...  AWT, Swing, SWT- freakin layout managers never change...  TC
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jul 7, 2007
 * @version 1.0
 */
public class TimeExtentWidget
{	
	protected Button overrideTimeBtn;
	protected Button continuousUpdateBtn;
	protected Button updateNowBtn;    
	protected Group mainGroup;
	protected TimeSpinner biasSpinner;
	protected TimeSpinner stepSpinner;
	protected TimeSpinner leadSpinner;
	protected TimeSpinner lagSpinner;
	protected MasterTimeWidget manualTimeWidget;
	protected Combo biasCombo;
	//  Do I have to dispose of these colors. or does the workench take care of that?
	static final Color BLUE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
	static final Color GREEN = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GREEN);
	static final Color RED = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED);
	static final Color WHITE = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE);
	
	public TimeExtentWidget(Composite parent) {
		init(parent);
	}

	public void setItemName(String name){
		mainGroup.setText(name);
//		Color col = mainGroup.getBackground();
//		mainGroup.setBackground(RED);
	//	mainGroup.redraw();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//mainGroup.setBackground(col);
	}
	
	public void init(Composite parent){
		//  Scrolled Composite to hold everything
		ScrolledComposite scroller = 
			new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
	    scroller.setExpandVertical(true);
		scroller.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		GridData gridData;
		//  MainGroup
	    mainGroup = new Group(scroller, 0x0);
		mainGroup.setText("itemName");
		scroller.setContent(mainGroup);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 3;
		layout.marginTop = -5;
		layout.makeColumnsEqualWidth = false;
		mainGroup.setLayout(layout);
		
        //  Override Time toggle
		overrideTimeBtn = new Button(mainGroup, SWT.CHECK);
		overrideTimeBtn.setText("Override Scene Time");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;  
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 9;
		overrideTimeBtn.setLayoutData(gridData);
		overrideTimeBtn.setToolTipText("Override the Scene time with the Time Controller below");

        //  ManualTimeWidget
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
		gridData.verticalIndent = 2;
        manualTimeWidget = new MasterTimeWidget(mainGroup, gridData, true);
        manualTimeWidget.setTitle("Override Time Control");

        //  Continuous/Update now btn
		continuousUpdateBtn = new Button(mainGroup, SWT.CHECK);
		continuousUpdateBtn.setText("Continuous update");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;   
		gridData.horizontalSpan = 1;
		gridData.verticalIndent = 8;
		continuousUpdateBtn.setLayoutData(gridData);
		continuousUpdateBtn.setToolTipText("Update the data with each time parameter change");
		
		updateNowBtn = new Button(mainGroup, SWT.PUSH);
		updateNowBtn.setText("Update Now");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;  
		gridData.horizontalSpan = 1;
		gridData.verticalIndent = 8;
		updateNowBtn.setLayoutData(gridData);

		//  Spinners
		//  SpinnerGroup 
		Group spinnerGroup = new Group(mainGroup, 0x0);
		spinnerGroup.setText("Advanced Controls");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 6;
		spinnerGroup.setLayoutData(gridData);
		GridLayout spinnerLayout = new GridLayout();
		spinnerLayout.numColumns = 2;
		spinnerLayout.verticalSpacing = 1;
		spinnerLayout.makeColumnsEqualWidth = false;
		spinnerGroup.setLayout(spinnerLayout);
		
		leadSpinner = new TimeSpinner(spinnerGroup, "Delta Lead");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.horizontalSpan = 2;
		leadSpinner.setLayoutData(gridData);

		lagSpinner = new TimeSpinner(spinnerGroup, "Delta Lag");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.horizontalSpan = 2;
		lagSpinner.setLayoutData(gridData);
		
        stepSpinner = new TimeSpinner(spinnerGroup, "Time Step");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.horizontalSpan = 2;
		stepSpinner.setLayoutData(gridData);
        
        biasCombo = new Combo(spinnerGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		biasCombo.setItems(new String[]{"+", "-"});
		biasCombo.select(0);
		biasCombo.setForeground(BLUE);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		biasCombo.setLayoutData(gridData);
		biasSpinner = new TimeSpinner(spinnerGroup, "Time Bias");
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gridData.horizontalAlignment = SWT.RIGHT;
		biasSpinner.setLayoutData(gridData);
		
		//  Must give sroller sufficient size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));	
	}

	public int getBiasSense(){
		int index = biasCombo.getSelectionIndex();
		return (index == 0 ? 1 : -1);
	}
	
	public void setBiasValue(double value){
		biasSpinner.setValue(Math.abs(value));
		int index = (value >= 0.0 ? 0: 1);
		biasCombo.select(index);
	}
	
	public boolean getOverrideSceneTime(){
		return overrideTimeBtn.getSelection();
	}
	
	public void addListeners(TimeSpinnerListener spinnerListener, SelectionListener selectionListener){
		overrideTimeBtn.addSelectionListener(selectionListener);
		updateNowBtn.addSelectionListener(selectionListener);
		continuousUpdateBtn.addSelectionListener(selectionListener);
		biasCombo.addSelectionListener(selectionListener);
		leadSpinner.addTimeSpinnerListener(spinnerListener);
		lagSpinner.addTimeSpinnerListener(spinnerListener);
		stepSpinner.addTimeSpinnerListener(spinnerListener);
		biasSpinner.addTimeSpinnerListener(spinnerListener);
		manualTimeWidget.addListeners(spinnerListener, selectionListener);
	}
    
    public void removeListeners(TimeSpinnerListener spinnerListener, SelectionListener selectionListener){
    	overrideTimeBtn.removeSelectionListener(selectionListener);
		updateNowBtn.removeSelectionListener(selectionListener);
		continuousUpdateBtn.removeSelectionListener(selectionListener);
		biasCombo.removeSelectionListener(selectionListener);
		leadSpinner.removeTimeSpinnerListener(spinnerListener);
		lagSpinner.removeTimeSpinnerListener(spinnerListener);
		stepSpinner.removeTimeSpinnerListener(spinnerListener);
		biasSpinner.removeTimeSpinnerListener(spinnerListener);
		manualTimeWidget.removeListeners(spinnerListener, selectionListener);
   }
}
