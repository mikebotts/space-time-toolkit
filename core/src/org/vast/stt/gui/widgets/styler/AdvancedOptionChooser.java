package org.vast.stt.gui.widgets.styler;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.LineStyler;

public class AdvancedOptionChooser {

	Composite parent;
	Group mainGroup;
	
	public AdvancedOptionChooser(Composite parent){
		this.parent = parent;
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
		//  Call AdvancedOptionChooser here
		if(styler instanceof LineStyler) {
			AdvancedLineController alc = new AdvancedLineController();
			alc.setStyler((LineStyler)styler);
			alc.buildAdvancedControls(mainGroup);
		} else {
			
		}
	}
	
	public Group getGroup(){
		return mainGroup;
	}
}
