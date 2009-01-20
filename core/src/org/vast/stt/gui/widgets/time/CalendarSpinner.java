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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * <p><b>Title:</b><br/>
 * CalendarSpinner
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Extension of TimeSpinner which allows Month display and behaves like 
 *  a calendar.  
 *  Also adds button for realtime and TimeZone combo.
 *  
 *  May want to break timeZone out of this at some pt, but it made things easier in 
 *  my latest refactoring of the TimeExtentWidget
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 */
public class CalendarSpinner extends TimeSpinner implements SelectionListener{

	private TimeZoneCombo tzCombo;
	protected Button rtBtn;

	public CalendarSpinner(Composite parent, String label) {
		super();
		String formatStr = "MMM dd, yyyy HH:mm:ss";
		tsModel = new CalendarSpinnerModel(formatStr);
		start = tsModel.getStart();
		len = tsModel.getLength();
		initGui(parent, label);

		// Set initial time and field pos to realtime and mintues
		currentField = start.length - 2;
		this.setValue(System.currentTimeMillis()/1000);
		resetCaret();
	}

	protected void initGui(Composite parent, String label){
		GridData gridData = new GridData();

		//  Create main group for text and Buttons
		mainGroup = new Group(parent, SWT.SHADOW_NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.marginTop = -10;
		gridLayout.marginBottom = -2;
		gridLayout.marginRight = -3;
		mainGroup.setLayout(gridLayout);
		mainGroup.addDisposeListener(this);
		
		//  Text/Spinner widget
		spinnerGroup = new Composite(mainGroup, 0x0);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;
		spinnerGroup.setLayout(gridLayout);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		spinnerGroup.setLayoutData(gridData);
		text = new StyledText(spinnerGroup, SWT.RIGHT | SWT.BORDER | SWT.READ_ONLY);
		ensureTextWidth();
		text.setFont(entryFont);
		text.setToolTipText(tsModel.formatStr);
		
		text.addTraverseListener(this);
		text.addMouseListener(this);
		text.addKeyListener(this);
		text.addFocusListener(this);

		// SpinnerGroup
		spinnerGroup = new Composite(spinnerGroup, SWT.SHADOW_NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 1;
		gridLayout.marginTop = -2;
		gridLayout.marginBottom = -5;
		spinnerGroup.setLayout(gridLayout);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		spinnerGroup.setLayoutData(gridData);
		//  Spinner buttons
		upBtn = new Button(spinnerGroup, SWT.ARROW | SWT.UP);
		gridData = new GridData();
		gridData.heightHint = BTN_SIZE;
		gridData.widthHint = BTN_SIZE;
    	upBtn.setLayoutData(gridData);
    	upBtn.addMouseListener(this);
    	upBtn.addFocusListener(this);
		downBtn = new Button(spinnerGroup, SWT.ARROW | SWT.DOWN);
		gridData = new GridData();
		gridData.heightHint = BTN_SIZE;
		gridData.widthHint = BTN_SIZE;
		downBtn.setLayoutData(gridData);
    	downBtn.addMouseListener(this);
    	downBtn.addFocusListener(this);

    	 //  RT toggle
        rtBtn = new Button(mainGroup, SWT.CHECK);
        rtBtn.setText("Real Time");
        gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        //gridData.horizontalSpan = 2;
        rtBtn.setLayoutData(gridData);
        rtBtn.setEnabled(false);
        
        tzCombo = new TimeZoneCombo(mainGroup);
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		tzCombo.setLayoutData(gridData);
		tzCombo.addSelectionListener(this);
	}

	//  Should set widthHint == the widest possible date the widget can support.  That
	//  will require getting the Font Metrics and doing some calculations.  For now, 
	//  just make it wide enough to display properly on Winders.  When porting to 
	//  Mac/Linux, this will need to be addressed.
	//  Note that this isn't an issue with the base TimeSpinners because I init them
	//  to all 0's, and there is no character data allowed.
	private void ensureTextWidth(){
		GridData gridData = new GridData();
		gridData.widthHint = 127;
		text.setLayoutData(gridData);
	}
	
	public void setEnabled(boolean b){
		super.setEnabled(b);
		if(b)
			tzCombo.setBackground(activeBackground);
		else
			tzCombo.setBackground(GRAY);
	}
	
	//  Special case where we want to disable text and spinner Btns, 
	//  but NOT RT and TimeZone fields
	public void disableDateChanges(){
		text.setEnabled(false);
		spinnerGroup.setEnabled(false);
		text.setBackground(GRAY);
	}
	
//	public void setValue(double jul1970_seconds){
//		tsModel.setValue(new Double(jul1970_seconds));
//		text.setText(tsModel.toString());
//		//hiliteField(currentField, true);
//	}
//		
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if(e.widget == tzCombo.getCombo()){
			//  change displayed time, but no need to issue updateData() calls
			((CalendarSpinnerModel)tsModel).setZoneOffset(tzCombo.getZoneOffset());
			text.setText(tsModel.toString());
		}
	}
}
