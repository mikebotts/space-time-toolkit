package org.vast.stt.gui.widgets;

import org.vast.stt.gui.widgets.OptionControl.ControlType;

public class OptionParams {
	private ControlType type;
	private String label;
	private Object data;
	
	public OptionParams(ControlType type, String label, Object data){
		this.type = type;
		this.label = label;
		this.data = data;
	}

	public ControlType getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public Object getData() {
		return data;
	}
}
