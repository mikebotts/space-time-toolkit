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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
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
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 * 
 *   TODO:  support TimeZone
 */
public class CalendarSpinnerModel extends TimeSpinnerModel {
	
	SimpleDateFormat dateFormat;
	int zoneOffset;
	//  Enforce min and max to keep the widget from changing width.
	//  This could actually be an issue for geologic datasets.  The widget
	//  will need to be modified to handle the AD/BC crossover (or whatever
	//  the kids are calling BC and AD these days).
	private static final double MIN_DATE = -6.21357696E10;  //  Jan 1, 0001 00:00:00
	private static final double MAX_DATE = 2.53402300799E11;  //  Dec 31, 9999 23:59:59
	
	public CalendarSpinnerModel(String formatStr){
		super(formatStr);
		dateFormat = new SimpleDateFormat(formatStr, Locale.US);
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
		//  NOTE:  this will cause the widget to fail on Jan 1, 10000.  
		//  The dreaded Y10K bug...  TC
		if(jtime > MAX_DATE)
			jtime = MAX_DATE;
		if(jtime < MIN_DATE)
			jtime = MIN_DATE;
		//  Account for timeZone
		jtime += zoneOffset*3600.0;
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

	}

	/**
     * Sets the spinner value based on Calendar object
     * @param sec - seconds since 1Jan1970
     */
    private void setValue(Calendar cal){
    	//  Ignore timeZone here
    	//  This is only setting the text fields
    	
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
		double time = ((double)calendar.getTimeInMillis()) / 1000.0;
		return new Double(time); // + fseconds;
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
