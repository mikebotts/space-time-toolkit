package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;

public class AdvancedOptionChooser {

	Composite parent;
	Group mainGroup;
	OptionController optionController;
	private OptionListener optListener;
	
	public AdvancedOptionChooser(Composite parent, OptionListener ol){
		this.parent = parent;
		this.optListener = ol;
		init();
	}
	
	public void init(){
		mainGroup = new Group(parent, 0x0);
		mainGroup.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GREEN));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		mainGroup.setLayout(gridLayout);
	}
	
	public void buildControls(DataStyler styler){
		if(styler instanceof PointStyler){
			optionController = new AdvancedPointController(mainGroup, (PointStyler)styler);
			optionController.addSelectionListener(optListener);
			optListener.setAdvancedController(optionController);
		} else if(styler instanceof LineStyler) {
			optionController = new AdvancedLineController(mainGroup, (LineStyler)styler);
			optionController.addSelectionListener(optListener);
			optListener.setAdvancedController(optionController);
		} else {
			
		}
		mainGroup.layout();
		mainGroup.redraw();
	}
	
	public Group getGroup(){
		return mainGroup;
	}
	
	public void close(){
		optListener.setAdvancedController(null);
//		optListener.setAdvancedStyler(null);
//		optListener.setAdvancedControls(null);
	}
	
}
