package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
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
}

