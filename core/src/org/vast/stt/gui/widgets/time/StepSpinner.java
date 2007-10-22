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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * <p><b>Title:</b>
 *  downBtn.addMouseListener(this);
    	downBtn.addFocusListener(this);
 * </p>
 *
 * <p><b>Description:</b><br/>
 *   Override normal TimeSpinner behavior so value of Spinner 
 *   doesn't change when up/down Btns are pressed
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jul 9, 2007
 * @version 1.0
 */
public class StepSpinner extends TimeSpinner implements SelectionListener
{
	
	public StepSpinner(Composite parent, String label) {
		super();
		tsModel = new TimeSpinnerModel("YYYY DDD HH:mm:SS");
		start = tsModel.getStart();
		len = tsModel.getLength();
		initGui(parent, label);
		text.setEnabled(false);
		addListeners();
		upBtn.setToolTipText("Increment the Date controller by the Step Time");
		downBtn.setToolTipText("Decrement the Date controller by the Step Time");
	}
	
	public void addListeners(){
//		upBtn.addSelectionListener(this);
//		downBtn.addSelectionListener(this);
		upBtn.addMouseListener(this);
    	upBtn.addFocusListener(this);
    	downBtn.addMouseListener(this);
    	downBtn.addFocusListener(this);
	}
	
	public void setEnabled(boolean b){
		super.setEnabled(b);
		//  StepSpinner text is not editable (could change this behavior 
		// to allow user to type in step value in the text area)
		text.setEnabled(false);
	}
	
	public void setValue(double value){
		tsModel.setValue(new Double(value));
		text.setText(tsModel.toString());
		//  Do not hilite anything
//		hiliteField(currentField, true);
	}
	

	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if(e.widget == upBtn ){
			publishTimeChanged(1);
		} else if (e.widget == downBtn){
			publishTimeChanged(-1);
		}
	}

	protected void timeUp(){
		publishTimeChanged(1);
	}

	protected void timeDown(){
		publishTimeChanged(-1);
	}
	
	protected void publishTimeChanged(int sense){
		double t = getValue();
		t *= sense;
		for(TimeSpinnerListener tsTmp: timeSpinnerListeners){
			tsTmp.timeChanged(this, t);
		}
	}

}

