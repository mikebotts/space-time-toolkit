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
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.style.LineStyler;

/**
 * <p><b>Title:</b><br/>
 * AdvancedLineController
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Feb 6, 2006
 * @version 1.0
 * 
 */
public class AdvancedLineController extends OptionController 
	implements SelectionListener {
	private Composite parent;
	private LineOptionHelper lineOptionHelper;
	
	public AdvancedLineController(Composite parent, LineStyler styler){
		this.parent = parent;
		this.styler = styler;
		
		lineOptionHelper = new LineOptionHelper(this);
		buildControls();
	}
	
	
	public void buildControls(){
		optionControls = new OptionControl[2];
		optionControls[0] = new OptionControl(parent, 0x0);
		Spinner advWidthSpinner = optionControls[0].createSpinner("LineWidth:", 1, 10);
		advWidthSpinner.setSelection((int)lineOptionHelper.getLineWidth());
		//  add other controls
		addMappingControls(parent);
		
		optionControls[1] = new OptionControl(parent, 0x0);
		optionControls[1].createColorButton("Line Color:", lineOptionHelper.getLineColor());

		addMappingControls(parent);

		addSelectionListener(lineOptionHelper);
	}
	
	//  Always the same: MapTo Label, MapCombo, LUT Button
	private void addMappingControls(Composite parent){
		Combo mappingCombo = new Combo(parent, SWT.READ_ONLY);
		final GridData gridData = new GridData(GridData.END, GridData.CENTER, true, false);
		gridData.widthHint = 53;
		mappingCombo.addSelectionListener(this);

		Button lutButton = new Button(parent, SWT.PUSH);
		lutButton.setText("LUT");
		lutButton.addSelectionListener(this);
	}

	public void widgetDefaultSelected(SelectionEvent e){
		
	}
	
	/**
	 * This widgetSelected is for events coming from the additional
	 * mapping controls (LUT and Combo) on the advancedDialog.
	 * OptionControl events still handled through LineOptionHelper class.  
	 */
	public void widgetSelected(SelectionEvent e) {
		System.err.println(e);
	}
}
