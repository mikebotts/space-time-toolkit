package org.vast.stt.gui.widgets.styler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.vast.stt.gui.widgets.OptionControl;
import org.vast.stt.style.LineStyler;

public class AdvancedLineController implements SelectionListener {
	
	LineOptionHelper optionHelper;
	private OptionControl[] optionControl;
	private LineStyler styler;
	
	public AdvancedLineController(){
		
	}
	
	public void setStyler(LineStyler styler){
		//  Move this to constructor 
		this.styler = styler;
		optionHelper = new LineOptionHelper(styler);
	}
	
	public void buildAdvancedControls(Composite parent){
		optionControl = new OptionControl[2];
		optionControl[0] = new OptionControl(parent);
		Spinner advWidthSpinner = optionControl[0].createSpinner("LineWidth:", 1, 10);
		advWidthSpinner.setSelection((int)optionHelper.getLineWidth());
		advWidthSpinner.addSelectionListener(this);
		//  add other controls
		addMappingControls(parent);
		
		optionControl[1] = new OptionControl(parent);
		Button advColorButton = 
			optionControl[1].createColorButton("Line Color:", optionHelper.getLineColor());
		advColorButton.addSelectionListener(this);
		addMappingControls(parent);
	}
	
	//  Always the same: MapTo Label, MapCombo, LUT Button
	private void addMappingControls(Composite parent){
		final Label mapToLabel = new Label(parent, SWT.NONE);
		mapToLabel.setText("Map To:");

		Combo mappingCombo = new Combo(parent, SWT.READ_ONLY);
		final GridData gridData = new GridData(GridData.END, GridData.CENTER, true, false);
		gridData.widthHint = 53;
		mappingCombo.addSelectionListener(this);

		Button lutButton = new Button(parent, SWT.PUSH);
		lutButton.setText("LUT");
		lutButton.addSelectionListener(this);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
