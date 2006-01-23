package org.vast.stt.gui.widgets.time;

import org.eclipse.swt.custom.StyledText;

/**
 * <p><b>Title:</b><br/>
 *  SpinnerModel
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Simple interface for implementing Spinner Models
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 21, 2005
 * @version 1.0
 * 
 * TODO:  Use generics to make get/set value typesafe
 * TODO:  selectField is UI and really should not be here
 */

public interface SpinnerModel {
	public Object getValue();
	public void setValue(Object value);
	public void increment();
	public void decrement();
	public void selectField(StyledText text);
}
