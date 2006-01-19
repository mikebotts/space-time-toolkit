package org.vast.stt.gui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;

public class StyleOptionChooser {
	Composite optComp;
	OptionControl [] optionControl;
	
	public StyleOptionChooser(Composite parent){
		init(parent);
	}

	public void init(Composite parent){
		optComp = new Composite(parent, SWT.BORDER);
		optComp.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout optLayout = new GridLayout(2, false);
		optComp.setLayout(optLayout);
		GridData optGd = new GridData(GridData.BEGINNING, GridData.FILL, true, false);
		optComp.setLayoutData(optGd);
		buildControls(new PointStyler());
	}
	
	public void buildControls(DataStyler styler){
		if(optionControl != null) {
			removeOldControls();
		}
		if(styler instanceof PointStyler)
			buildPointControls((PointStyler)styler);
		else if (styler instanceof LineStyler)
			buildLineControls((LineStyler)styler);
		else
			System.err.println("Styler not supported yet: " + styler);
		
		optComp.layout(true);
		optComp.redraw();
	}
	
	private void removeOldControls(){
		for(int i=0; i<optionControl.length; i++){
			optionControl[i].dispose();
		}
		optionControl = null;  //  probably not needed
		//optComp.redraw();
	}
	
	public void buildPointControls(PointStyler styler){
		optionControl = new OptionControl[2];
		optionControl[0] = new OptionControl(optComp);
		optionControl[0].createSpinner("Point Size:", 1, 10);
		optionControl[1] = new OptionControl(optComp);
		optionControl[1].createButton("Point Color", "...");
	}
	
	public void buildLineControls(LineStyler styler){
		optionControl = new OptionControl[3];
		optionControl[0] = new OptionControl(optComp);
		optionControl[0].createSpinner("LineWidth:", 1, 10);
		optionControl[1] = new OptionControl(optComp);
		optionControl[1].createButton("Line Color", "...");
		optionControl[2] = new OptionControl(optComp);
		optionControl[2].createCombo("Line Test:", new String [] { "aaa", "bbb", "ccc" } );
	}
	
	class OptionControl{
		Composite parent;
		Label label;
		Control control;
		
		public OptionControl(Composite parent){
			this.parent = parent;
		}
		
		//  return Label so caller can modify layoutData, if desired
		private Label createLabel(String text){
			label = new Label(optComp, 0x0);
			label.setText(text);
			label.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GREEN));
			GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			gd.widthHint = 90;
			label.setLayoutData(gd);
			
			return label;
		}
		
		public Spinner createSpinner(String labelTxt, int min, int max){
			createLabel(labelTxt);
			control = new Spinner(parent, 0x0);
			Spinner spinner = (Spinner)control;
			spinner.setMinimum(min);
			spinner.setMaximum(max);
			GridData gd = new GridData(SWT.RIGHT, SWT.FILL, true,true);
			spinner.setLayoutData(gd);
			
			return spinner;
		}
		
		public Combo createCombo(String labelTxt, String [] opts){
			createLabel(labelTxt);
			control = new Combo(parent, 0x0);
			Combo combo = (Combo)control;
			combo.setItems(opts);

			GridData gd = new GridData(SWT.RIGHT, SWT.FILL, true,true);
			combo.setLayoutData(gd);
			return combo;
		}
		
		public Button createButton(String labelTxt, String text) { // sellistener
			createLabel(labelTxt);
			control = new Button(parent, SWT.PUSH);
			Button button = (Button)control;
			button.setText(text);

			GridData gd = new GridData(SWT.RIGHT, SWT.FILL, true,true);
			button.setLayoutData(gd);
			return button;
		}
		public void dispose(){
			label.dispose();
			control.dispose();
			label = null;
			control = null;
		}
	}
	

}
