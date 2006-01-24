package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.events.SelectionListener;

public interface OptionController extends SelectionListener {
	public void buildBasicOptions();
	public void buildAdvancedOptions();
}
