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
import org.eclipse.swt.widgets.Spinner;

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
public final class AnimationTimeWidget
{
    private Group mainGroup,scaleGroup,controlGroup;
    protected AnimationSpinner beginTimeSpinner;
    protected AnimationSpinner endTimeSpinner;
    protected AnimationStepSpinner stepTimeSpinner;    
    protected Button startAnimateButton;
    protected Button stopAnimateButton;
    protected Spinner scaleTime;
    protected double timeStep = 3.0; //  timeStep in seconds
    
    public AnimationTimeWidget(Composite parent) {
    	this(parent, new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
    }

    public AnimationTimeWidget(Composite parent, Object layoutData) {
        init(parent, layoutData);
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

        mainGroup = new Group(scroller,SWT.SHADOW_ETCHED_IN);
        mainGroup.setLayoutData(layoutData);
        scroller.setContent(mainGroup);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        //layout.verticalSpacing = 10;
        layout.marginRight = 1;
        //  Adding extraBtn puts huge pad at bottom of mainGroup for some reason-
        //  compensate with negative marginBottom setting
     
        mainGroup.setLayout(layout);
        
        //  BeginTime
        beginTimeSpinner = new AnimationSpinner(mainGroup, "Begin Time:");
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.LEFT;
        gridData.horizontalSpan = 2;
        beginTimeSpinner.setLayoutData(gridData);
        
        
        //  EndTime
        endTimeSpinner = new AnimationSpinner(mainGroup, "End Time:");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.LEFT;
        gridData.horizontalSpan = 2;
        endTimeSpinner.setLayoutData(gridData);

        //  Time Step
        stepTimeSpinner = new AnimationStepSpinner(mainGroup, "Step Time");
        stepTimeSpinner.setValue(timeStep);
        gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = SWT.LEFT;
        stepTimeSpinner.setLayoutData(gridData);
        
        // Time Scale
        scaleGroup = new Group(mainGroup,SWT.NONE);
        scaleGroup.setText("Time Scale");
        GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.makeColumnsEqualWidth = false;
		scaleGroup.setLayout(gridLayout);
		//scaleGroup.addDisposeListener(this);
        scaleTime = new Spinner(scaleGroup,SWT.BORDER);
        scaleTime.setMinimum(1);
        scaleTime.setMaximum(10);
        scaleTime.setSelection(1);
        scaleTime.setIncrement(1); 
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.CENTER;
        //gridData.heightHint = 20;
        gridData.horizontalSpan = 1;
        scaleTime.setLayoutData(gridData);
        
        startAnimateButton = new Button(mainGroup, SWT.PUSH);
        startAnimateButton.setText("Begin Animation");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.CENTER;
        gridData.heightHint = 20;
        gridData.horizontalSpan = 1;
        startAnimateButton.setLayoutData(gridData);
        startAnimateButton.setToolTipText("Begin the animation scenario as indicated by time and scene.");
        
        //  Must give scroller sufficient size
        scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public void setEnabled(boolean b){
    	beginTimeSpinner.setEnabled(b);
    	endTimeSpinner.setEnabled(b);
    	stepTimeSpinner.setEnabled(b);
    }
    
    public void addListeners(TimeSpinnerListener spinnerListener, SelectionListener selectionListener){
    	 beginTimeSpinner.addTimeSpinnerListener(spinnerListener);
    	 endTimeSpinner.addTimeSpinnerListener(spinnerListener);
    	 stepTimeSpinner.addTimeSpinnerListener(spinnerListener);
         startAnimateButton.addSelectionListener(selectionListener);
    }
    
    public void removeListeners(TimeSpinnerListener spinnerListener, SelectionListener selectionListener){
    	beginTimeSpinner.addTimeSpinnerListener(spinnerListener);
   	 	endTimeSpinner.addTimeSpinnerListener(spinnerListener);
   	    stepTimeSpinner.addTimeSpinnerListener(spinnerListener);
   }
}
