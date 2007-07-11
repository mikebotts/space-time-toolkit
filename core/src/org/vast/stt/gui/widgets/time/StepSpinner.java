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
 * <p>Copyright (c) 2006</p>
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

