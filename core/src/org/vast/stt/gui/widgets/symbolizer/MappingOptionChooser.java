package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.widgets.Composite;
import org.vast.ows.sld.MappingFunction;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.gui.widgets.OptionParams;

/**
 * @author Tony Cook
 *
 */
public class MappingOptionChooser extends OptionChooser
{
	OptionControl [] optionControls;
	MappingFunction func;
	
	public MappingOptionChooser(Composite parent){
		super(parent);
		buildControls(null);
	}
	
	public void buildControls(Object mappingFn){
		OptionParams[] params =	{
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Gain:", "1.2345"),	
			new OptionParams(OptionControl.ControlType.NUMERIC_TEXT, "Offset:",	"-12")
		};
		
		optionControls = OptionControl.createControls(optComp, params);
	}
	
	public void setMappingFunction(MappingFunction func){
		
	}
}

