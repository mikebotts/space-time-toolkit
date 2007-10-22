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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * <p><b>Title:</b><br/>
 * MasterTimeWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Widget for MasterTime (Calendar) spinner, stepSpinner, and optionally,
 *  setTime to now btn.
 *  
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 */
public final class MasterTimeWidget
{
    private Group mainGroup;
    protected CalendarSpinner absTimeSpinner;
    protected StepSpinner stepTimeSpinner;
    protected Button setBtn;
    protected Button baseAtNowBtn;    
    protected double timeStep = 3.0; //  timeStep in seconds
    protected boolean showTimeAtNow;
    
    public MasterTimeWidget(Composite parent, boolean showTimeAtNow) {
    	this(parent, new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1), showTimeAtNow);
    }

    public MasterTimeWidget(Composite parent, Object layoutData, boolean showTimeAtNow) {
    	this.showTimeAtNow = showTimeAtNow;
        init(parent, layoutData);
        //stepTimeSpinner.resetCaret();
    }
    
    public void setTitle(String title){
    	mainGroup.setText(title);
    }

    public void init(Composite parent, Object layoutData)
    {
        //  Scrolled Composite to hold everything
        ScrolledComposite scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setLayoutData(layoutData);

        mainGroup = new Group(scroller, 0x0);
       // mainGroup.setLayoutData(layoutData);
        scroller.setContent(mainGroup);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
       // layout.verticalSpacing = 3;
        //layout.horizontalSpacing = 1;
        layout.marginRight = 1;
        //  Adding extraBtn puts huge pad at bottom of mainGroup for some reason-
        //  compensate with negative marginBottom setting
        if(showTimeAtNow)
        	layout.marginBottom = -17;
        mainGroup.setLayout(layout);

        //  MasterTime
        absTimeSpinner = new CalendarSpinner(mainGroup, "Master Time");
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.RIGHT;
        gridData.horizontalSpan = 2;
        absTimeSpinner.setLayoutData(gridData);

        //  Time Step
        setBtn = new Button(mainGroup, SWT.PUSH);
        setBtn.setText("Set");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.LEFT;
        setBtn.setLayoutData(gridData);
        setBtn.setToolTipText("Change the step value shown in the Step Time Spinner");
        
        stepTimeSpinner = new StepSpinner(mainGroup, "Step Time");
        stepTimeSpinner.setValue(timeStep);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.RIGHT;
        stepTimeSpinner.setLayoutData(gridData);

//      Base at Now Btn...
		//  Only needed for OGC services, but including for all 
		//  items for now
        if(showTimeAtNow) {
			baseAtNowBtn = new Button(mainGroup, SWT.CHECK);
			baseAtNowBtn.setText("Set time=now");
			gridData = new GridData();
			gridData.horizontalAlignment = SWT.RIGHT;   
			gridData.horizontalSpan = 2;
			gridData.verticalIndent = 4;
			gridData.heightHint = 12;
			baseAtNowBtn.setLayoutData(gridData);
			baseAtNowBtn.setToolTipText("For OGC Web services, set the time paramter to 'now'");
			//baseAtNowBtn.addSelectionListener(this);
        }
        //  Must give sroller sufficient size
        scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public void setEnabled(boolean b){
    	absTimeSpinner.setEnabled(b);
    	absTimeSpinner.rtBtn.setEnabled(b);
    	stepTimeSpinner.setEnabled(b);
    	setBtn.setEnabled(b);
    	if(showTimeAtNow)
    		baseAtNowBtn.setEnabled(b);
    }
    
    public void addListeners(TimeSpinnerListener spinnerListener, SelectionListener selectionListener){
    	 absTimeSpinner.addTimeSpinnerListener(spinnerListener);
    	 stepTimeSpinner.addTimeSpinnerListener(spinnerListener);
         absTimeSpinner.rtBtn.addSelectionListener(selectionListener);
         setBtn.addSelectionListener(selectionListener);
         if(showTimeAtNow)
        	 baseAtNowBtn.addSelectionListener(selectionListener);
    }
    
    public void removeListeners(TimeSpinnerListener spinnerListener, SelectionListener selectionListener){
   	    absTimeSpinner.addTimeSpinnerListener(spinnerListener);
   	    stepTimeSpinner.addTimeSpinnerListener(spinnerListener);
        absTimeSpinner.rtBtn.addSelectionListener(selectionListener);
        setBtn.addSelectionListener(selectionListener);
        if(showTimeAtNow)
       	    baseAtNowBtn.addSelectionListener(selectionListener);
   }
}
