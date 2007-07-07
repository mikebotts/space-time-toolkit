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
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 */
public class CalendarSpinner extends TimeSpinner implements SelectionListener{

	private TimeZoneCombo tzCombo;
	protected Button rtBtn;

	public CalendarSpinner(Composite parent, String label) {
		super();
		formatStr = "MMM dd, yyyy HH:mm:ss";
		tsModel = new CalendarSpinnerModel(formatStr);
		start = tsModel.getStart();
		len = tsModel.getLength();
		initGui(parent, label);

		//  Set initial time elsewhere
		this.setValue(1.1e9);
		text.setCaretOffset(13);
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
		//gridData.verticalAlignment = SWT.CENTER;
		//gridData.heightHint = 18;
		//text.setLayoutData(gridData);
		text.setFont(entryFont);
		text.setToolTipText(formatStr);
		
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
        
        tzCombo = new TimeZoneCombo(mainGroup);
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		tzCombo.setLayoutData(gridData);
		tzCombo.addSelectionListener(this);
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
	
	public void setValue(double jul1970_seconds){
		tsModel.setValue(new Double(jul1970_seconds));
		text.setText(tsModel.toString());
		//selectField();
	}
		
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if(e.widget == tzCombo.getCombo()){
			//  change displayed time, but no need to issue updateData() calls
			((CalendarSpinnerModel)tsModel).setZoneOffset(tzCombo.getZoneOffset());
			text.setText(tsModel.toString());
			//tsModel.selectField(text);
			System.err.println("New timeVal is " + tsModel.getValue());
		}
	}
}
