package org.vast.stt.gui.widgets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * <p><b>Title:</b><br/>
 * CalendarSpinnerModel
 * </p>
 *
 * <p><b>Description:</b><br/>
 * The Model for controlling the behavior of the CalendarSpinner and widget.
 * 
 * Even though this model uses a Calendar object internally, most of the functionality is 
 * still the same as the base TimeSpinnerModel, so I just extended it here.
 * 
 *  NOTE:  getValue() returns a Double, whereas setValue() accepts a Calendar object.
 * 
 * </p>
 *
 *
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 * 
 *   TODO:  support TimeZone
 */
public class CalendarSpinnerModel extends TimeSpinnerModel {
	
	SimpleDateFormat dateFormat;
	
	public CalendarSpinnerModel(String formatStr){
		super(formatStr);
		dateFormat = new SimpleDateFormat(formatStr);
	}
	
	public String toString(){
		// set up the calendar
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(0);  // fseconds
		calendar.set(years, months, days, hours, minutes, seconds);			
		//calendar.setTimeZone(TimeZone.getTimeZone("GMT-5")); //  see DateTimeFormat
		return dateFormat.format(calendar.getTime());
	}

	/**
     * Sets the spinner value based on number of seconds
     * @param sec - seconds since 1Jan1970
     */
    public void setValue(Object calObj){
    	if(!(calObj instanceof Calendar)) {
    		//  Check for correct class cast- remove when implementing with generics
    		System.err.println("CurrentTimeSpinnerModel requires Calendar object for setValue(Calendar cal)");
    		return;
    	}
    	Calendar cal = (Calendar)calObj;
        //  clear all fields
    	years = cal.get(Calendar.YEAR);
    	months = cal.get(Calendar.MONTH);  // watch for 11/12 thing
    	days = cal.get(Calendar.DAY_OF_MONTH);
    	hours = cal.get(Calendar.HOUR);
    	minutes = cal.get(Calendar.MINUTE);
    	seconds = cal.get(Calendar.SECOND);
    	//  fseconds = ...
    }
    
    public void increment(){
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(0);  // fseconds
		calendar.set(years, months, days, hours, minutes, seconds);
		stepCurrentField(calendar, true);
    }
    
    public void decrement(){
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(0);  // add fseconds, but BEWEARE fracSecond bug in Calendar
		calendar.set(years, months, days, hours, minutes, seconds);
		stepCurrentField(calendar, false);
    }
    
	private void stepCurrentField(Calendar cal, boolean up){
		//  Roll the appropriate field
		if(currentField == YEAR){
			cal.roll(Calendar.YEAR, up);
		} else if(currentField == MONTH){
			cal.roll(Calendar.MONTH, up);
		} else if(currentField == DAY){
			cal.roll(Calendar.DAY_OF_MONTH, up);
		} else if(currentField == HOUR){
			cal.roll(Calendar.HOUR, up);
		} else if(currentField == MIN) {
			cal.roll(Calendar.MINUTE, up);
		} else if(currentField == SEC) {
			cal.roll(Calendar.SECOND, up);
		} else if(currentField == FSEC) {
			;// changeFsecond(step);
		} else 
			System.err.println(" *** currentField unknown.  Blimey!!!!");
			
		setValue(cal);
	}

    public Object getValue(){
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(0);  // add fseconds, but BEWEARE fracSecond bug in Calendar
		calendar.set(years, months, days, hours, minutes, seconds);
		return new Double(((double)calendar.getTimeInMillis()) / 1000.0); // + fseconds;
    }
    
}
