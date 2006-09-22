package org.vast.stt.gui.widgets.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 * 
 *   TODO:  support TimeZone
 */
public class CalendarSpinnerModel extends TimeSpinnerModel {
	
	SimpleDateFormat dateFormat;
	int zoneOffset;
	
	public CalendarSpinnerModel(String formatStr){
		super(formatStr);
		dateFormat = new SimpleDateFormat(formatStr);
		//   TODO  add mechanism to set initial zoneOffset
		zoneOffset = 0;
	}
	
	public String toString(){
		// set up the calendar
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(0);  // fseconds
		calendar.set(years, months, days, hours + zoneOffset, minutes, seconds);
		return dateFormat.format(calendar.getTime());
	}

	public void setValue(Object jtimeObj){
		double jtime = ((Double)jtimeObj).doubleValue();
    	Calendar cal = getGoodCalendar();
		cal.setTimeInMillis((long)jtime*1000l);
    	//  set all fields from cal object's values
		years = cal.get(Calendar.YEAR);
    	months = cal.get(Calendar.MONTH);  // watch for 11/12 thing
    	days = cal.get(Calendar.DAY_OF_MONTH);
    	hours = cal.get(Calendar.HOUR_OF_DAY);
    	minutes = cal.get(Calendar.MINUTE);
    	seconds = cal.get(Calendar.SECOND);
    	//  fseconds = ...

//		String sval = tsModel.toString();
//		System.err.println("sval = " + sval);
//		System.err.println("jtime = " + jtime);
//		System.err.println("Mod jtime = " + tsModel.getValue());
	}

	/**
     * Sets the spinner value based on number of seconds
     * @param sec - seconds since 1Jan1970
     */
    private void setValue(Calendar cal){
        //  reset all fields
    	years = cal.get(Calendar.YEAR);
    	months = cal.get(Calendar.MONTH);  // watch for 11/12 thing
    	days = cal.get(Calendar.DAY_OF_MONTH);
    	hours = cal.get(Calendar.HOUR_OF_DAY);
    	minutes = cal.get(Calendar.MINUTE);
    	seconds = cal.get(Calendar.SECOND);
    	//  fseconds = ...
    }
    
    public Object getValue(){
    	Calendar calendar = getGoodCalendar();
		calendar.set(years, months, days, hours-zoneOffset, minutes, seconds);
		return new Double(((double)calendar.getTimeInMillis()) / 1000.0); // + fseconds;
    }

    public void increment(int field){
    	Calendar calendar = getGoodCalendar();
		calendar.set(years, months, days, hours, minutes, seconds);
		stepCurrentField(calendar, field, 1);
    }
    
    public void decrement(int field){
    	Calendar calendar = getGoodCalendar();
    	calendar.set(years, months, days, hours, minutes, seconds);
		stepCurrentField(calendar, field, -1);
    }
    
	private void stepCurrentField(Calendar cal, int currentField, int step){
		//  Roll the appropriate field
		if(currentField == YEAR){
			cal.add(Calendar.YEAR, step);
		} else if(currentField == MONTH){
			cal.add(Calendar.MONTH, step);
		} else if(currentField == DAY){
			cal.add(Calendar.DAY_OF_MONTH, step);
		} else if(currentField == HOUR){
			cal.add(Calendar.HOUR_OF_DAY, step);
		} else if(currentField == MIN) {
			cal.add(Calendar.MINUTE, step);
		} else if(currentField == SEC) {
			cal.add(Calendar.SECOND, step);
		} else if(currentField == FSEC) {
			;// changeFsecond(step);
		} else 
			System.err.println(" *** currentField unknown.  Blimey!!!!");
			
		setValue(cal);
	}

	/**
	 * 
	 * @return a new Calendar Object with current TimeZone offset and 
	 * 	       fracSecond bug all accounted for
	 */
	private Calendar getGoodCalendar(){
		TimeZone tz = TimeZone.getTimeZone("GMT");
		tz.setRawOffset(-zoneOffset*CalendarSpinnerModel.SECONDS_PER_HOUR*1000);
		Calendar calendar = new GregorianCalendar(tz);
		calendar.setTimeInMillis(0);  // add fseconds, but BEWEARE fracSecond bug in Calendar
		return calendar;
	}
	
	public void setZoneOffset(int zoneOffset) {
		this.zoneOffset = zoneOffset;
	}

}
