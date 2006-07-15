package org.vast.stt.gui.widgets.time;

/**
 * @author Tony Cook
 *
 */
public interface TimeListener {
	//  May add more to the arg of timeChanged, if needed
	public void timeChanged(TimeSpinner spinner, double newTime);
}
