package org.vast.stt.gui.widgets;


/**
 * <p><b>Title:</b><br/>
 *  SpinnerModel
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Simple interface for implementing Spinner Models.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 * 
 * TODO:  Use generics to make get/set value typesafe
 */

public interface SpinnerModel {
	public Object getValue();
	public void setValue(Object value);
	public void increment(int field);  //  field is the field to inc/dec in a multi-field model
	public void decrement(int field);  //  ditto
}
