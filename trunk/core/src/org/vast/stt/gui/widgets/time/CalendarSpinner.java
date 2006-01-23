package org.vast.stt.gui.widgets.time;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.widgets.Composite;

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

	public CalendarSpinner(Composite parent, String label) {
		super();
		tsModel = new CalendarSpinnerModel("MMM dd, yyyy HH:mm:ss");
		initGui(parent, label);

		//  Should really generate initial data from formatStr, but I don't 
		//  think it will use this anyway.  In practice, setValue(double) will
		//  be called at initialization
		//text.setText("Jan 15, 1998 12:24:56");
		//  Test Calendar
//		Calendar cal = new GregorianCalendar();
//		cal.set(1998, 1, 23, 12, 23, 45);
//		this.setValue(cal);
		this.setValue(9.995e8);
		text.setCaretOffset(13);
		tsModel.selectField(text);
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
