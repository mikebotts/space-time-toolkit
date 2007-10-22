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
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.sttx.gui.widgets.smart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;


/**
 * <p><b>Title:</b>
 *   PressureWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date May 21, 2007
 * @version 1.0
 */
public class PressureWidget  implements SelectionListener, KeyListener
{
	private Slider minSlider;
	private Slider maxSlider;
	//  Scale uses integers (0-100 by default) for allowable values.  I'll have to map these
	//  to floating point for pressures)
	private float minPressureLimit;
	private float maxPressureLimit;
	private float minPressure;  
	private float maxPressure; 
	private final int minSliderVal = 0;
	private final int maxSliderVal = 100; 
	private Text minText;
	private Text maxText;
	private Button continuousUpdateBtn;
	private Button updateNowBtn;
	
	public PressureWidget(Composite parent){
		buildControls(parent);
		
		//  test defaults
		setMinPressureLimit(200.0f);
		setMaxPressureLimit(1000.0f);
		setMinPressure(485.0f);
		setMaxPressure(875.0f);
	}
	
	public void buildControls(Composite parent){
		// Create two separate sliders for now
		//  and a text field for each
        final ScrolledComposite mainSC = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        mainSC.setExpandVertical(true);
        mainSC.setExpandHorizontal(true);
        //  START HERE:  Try this with OptSCroller
        mainSC.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

        Group mainGroup = new Group(mainSC, SWT.NONE);
        mainGroup.setText("Pressure Chooser");
        final GridLayout gridLayout = new GridLayout(2, false);
        mainGroup.setLayout(gridLayout);
		
        GridData gd;
        //  Labels
        Label minLabel = new Label(mainGroup, 0x0);
        minLabel.setText("Min");
        gd = new GridData(SWT.CENTER, SWT.END, true, false);
        minLabel.setLayoutData(gd);
        Label maxLabel = new Label(mainGroup, 0x0);
        maxLabel.setText("Max");
        gd = new GridData(SWT.CENTER, SWT.END, true, false);
        maxLabel.setLayoutData(gd);
        
        minSlider = new Slider(mainGroup, SWT.VERTICAL);
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        minSlider.setLayoutData(gd);
        maxSlider = new Slider(mainGroup, SWT.VERTICAL);
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        maxSlider.setLayoutData(gd);
        //  Assign new min/maxes to allow finer control over pressure selection
        minSlider.setMinimum(minSliderVal);
        minSlider.setMaximum(maxSliderVal+10);  //  add 10 b/c SWT Slider only goes up to max-10
        maxSlider.setMinimum(minSliderVal);
        maxSlider.setMaximum(maxSliderVal+10);  // add 10 b/c SWT Slider only goes up to max-10
        
        //
        minSlider.addSelectionListener(this);
        maxSlider.addSelectionListener(this);
        
        minText = new Text(mainGroup, SWT.RIGHT);
		gd = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
        gd.verticalIndent = 6;
        minText.setLayoutData(gd);
        minText.addSelectionListener(this);
        minText.addKeyListener(this);
        maxText = new Text(mainGroup, SWT.RIGHT);
		gd = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
        gd.verticalIndent = 6;
        maxText.setLayoutData(gd);
        maxText.addSelectionListener(this);
        maxText.addKeyListener(this);
        
        continuousUpdateBtn = new Button(mainGroup, SWT.CHECK);
		continuousUpdateBtn.setText("Continuous Update");
		continuousUpdateBtn.addSelectionListener(this);
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;   
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		continuousUpdateBtn.setLayoutData(gd);
		
		updateNowBtn = new Button(mainGroup, SWT.PUSH);
		updateNowBtn.setText("Update Now");
		updateNowBtn.addSelectionListener(this);
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;  
		gd.horizontalSpan = 2;
		gd.verticalIndent = 8;
		updateNowBtn.setLayoutData(gd);
        
        mainSC.setContent(mainGroup);
        mainSC.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private float mapToPressure(int ival){
		float delSlideVal = maxSliderVal - minSliderVal;
		float frac = (ival - minSliderVal)/ delSlideVal;
		float delPresVal = maxPressureLimit - minPressureLimit;
		float newPres = minPressureLimit + (frac*delPresVal);
		return newPres;
	}
	
	private int mapToSlider(float fval){
		float delPresVal = maxPressureLimit - minPressureLimit;
		float frac = (fval - minPressureLimit)/ delPresVal;
		float delSliderVal = maxSliderVal - minSliderVal;
		int newSlider = minSliderVal + (int)(frac*delSliderVal);
		return newSlider;
	}
	
	public void widgetSelected(SelectionEvent e) {
		if(e.widget == minSlider) {
			int val = minSlider.getSelection();
			minPressure = mapToPressure(val);
			if (minPressure > maxPressure - 0.1f) {
				minPressure = maxPressure - 0.1f;
				minSlider.setSelection(mapToSlider(minPressure));
			}
			minText.setText("" + minPressure);
		} else if (e.widget == maxSlider) {
			int val = maxSlider.getSelection();
			maxPressure = mapToPressure(val);
			if (maxPressure < minPressure + 0.1f) {
				maxPressure = minPressure + 0.1f;
				maxSlider.setSelection(mapToSlider(maxPressure));
			}			
			maxText.setText("" + maxPressure);
		} else if (e.widget == updateNowBtn) {
			;  //add code/call to update Pressure values in DataProvider
		} else if (e.widget == continuousUpdateBtn) {
			;  //  add call to enable continuousUpdate in DataProvider
			updateNowBtn.setEnabled(!continuousUpdateBtn.getSelection());
		} 
	}

	public void widgetDefaultSelected(SelectionEvent e){
		if (e.widget == minText) {
			float minPressure = Float.parseFloat(minText.getText());
			if (minPressure > maxPressure - 0.1f) {
				minPressure = maxPressure - 0.1f;
				minText.setText("" + minPressure);
			}
			minSlider.setSelection(mapToSlider(minPressure));
		} else if (e.widget == maxText) {
			float maxPressure = Float.parseFloat(maxText.getText());
			if (maxPressure < minPressure + 0.1f) {
				maxPressure = minPressure + 0.1f;
				maxText.setText("" + maxPressure);
			}	
			maxSlider.setSelection(mapToSlider(maxPressure));
		}
			
	}
	
	public void keyPressed(KeyEvent e) {
		//  accept only numbers, decimal point
		e.doit = ( (e.keyCode >=48 && e.keyCode <= 57) || e.keyCode == 46 || e.keyCode == 13);
	}

	public void keyReleased(KeyEvent e) {
	}
	
	public void setMinPressureLimit(float minPressure) {
		this.minPressureLimit = minPressure;
	}

	public void setMaxPressureLimit(float maxPressure) {
		this.maxPressureLimit = maxPressure;
	}

	public float getMinPressure() {
		return minPressure;
	}

	public void setMinPressure(float minPressure) {
		this.minPressure = minPressure;
		//  set slider and TF, aslo
		minSlider.setSelection(mapToSlider(minPressure));
		minText.setText("" + minPressure);
	}

	public float getMaxPressure() {
		return maxPressure;
	}

	public void setMaxPressure(float maxPressure) {
		this.maxPressure = maxPressure;
		//  set slider and TF, aslo
		maxSlider.setSelection(mapToSlider(maxPressure));
		maxText.setText("" + maxPressure);
	}
}