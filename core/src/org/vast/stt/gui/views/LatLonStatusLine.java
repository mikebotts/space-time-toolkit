package org.vast.stt.gui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class LatLonStatusLine extends WorkbenchWindowControlContribution {
	public static final String ID = "STT.control4";
	private Label llLabel;

	public LatLonStatusLine(){
	}

	protected Control createControl(Composite parent) {
		// Create a composite to place the label in
		Composite comp = new Composite(parent, SWT.NONE);

		// Give some room around the control
		FillLayout layout = new FillLayout();
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		comp.setLayout(layout);
		llLabel = new Label(comp, SWT.BORDER | SWT.CENTER);
		llLabel.setText(" Lat: xxx.xxxxxx  Lon: xxxx.xxxxxxx ");
		comp.setVisible(true);
		
		return comp;
	}
	
	public void setText(String s){
		llLabel.setText(s);
		llLabel.redraw();
	}
	
	public boolean isDynamic(){
		return true;
	}

}

