package org.vast.stt.gui.widgets.time;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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
 *  a calendar 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 */
public class CalendarSpinner extends TimeSpinner {

	public CalendarSpinner(Composite parent, String label, int orientation) {
		super();
		tsModel = new CalendarSpinnerModel("MMM dd, yyyy HH:mm:ss");
		initGui(parent, label, orientation);

		//  Set initial time elsewhere
		this.setValue(9.995e8);
		text.setCaretOffset(13);
		tsModel.selectField(text);
	}

	public CalendarSpinner(Composite parent, String label) {
		this(parent, label, SWT.HORIZONTAL);
	}
	
	protected void initGui(Composite parent, String label, int orientation){
		//  Create main group for text and Buttons
		mainGroup = new Group(parent, SWT.SHADOW_NONE);
		mainGroup.setText(label);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = (orientation==SWT.HORIZONTAL) ? 3 : 2;
		gridLayout.marginTop = -10;
		gridLayout.marginBottom = -2;
		gridLayout.marginRight = -3;
		mainGroup.setLayout(gridLayout);
		mainGroup.addDisposeListener(this);
		//  Text widget		
		text = new StyledText(mainGroup, SWT.RIGHT | SWT.BORDER | SWT.READ_ONLY);
		GridData gridData = new GridData();
		//gridData.verticalAlignment = SWT.CENTER;
		//gridData.heightHint = 18;
		text.setLayoutData(gridData);
		text.setFont(entryFont);
		
		text.addTraverseListener(this);
		text.addMouseListener(this);
		text.addKeyListener(this);

		// SpinnerGroup
		Composite spinnerGroup = new Composite(mainGroup, SWT.SHADOW_NONE);
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
		gridData.heightHint = 15;
    	upBtn.setLayoutData(gridData);
    	upBtn.addMouseListener(this);
		downBtn = new Button(spinnerGroup, SWT.ARROW | SWT.DOWN);
		gridData = new GridData();
		gridData.heightHint = 15;
		downBtn.setLayoutData(gridData);
    	downBtn.addMouseListener(this);

    	//  TimeZone Combo
		TimeZoneCombo tzCombo = new TimeZoneCombo(mainGroup);
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		tzCombo.setLayoutData(gridData);
		//tzCombo.addSelectionListener(this);
	}


	public void setValue(Calendar value){
		tsModel.setValue(value);
		text.setText(tsModel.toString());
		tsModel.selectField(text);
	}
	
	public void setValue(double jul1970_seconds){
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis((long)jul1970_seconds*1000l);
		tsModel.setValue(cal);
		text.setText(tsModel.toString());
		tsModel.selectField(text);
	}
}
