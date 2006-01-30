package org.vast.stt.gui.widgets;

import org.eclipse.swt.events.SelectionListener;

public interface OptionController extends SelectionListener {
	public void buildBasicOptions();
	public void buildAdvancedOptions();
}
