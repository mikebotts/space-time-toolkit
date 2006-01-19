package org.vast.stt.gui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Graphic;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.ScalarParameter;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;

/**
 * <p><b>Title:</b><br/>
 * StyleOptionChooser
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	StyleOptionChooser is a composite that holds label/control pairs for 
 *  selecting options for a particular Styler type.   
 *  Currently supports the following Control types:
 *  	Spinner, Button, Combo    
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 18, 2006
 * @version 1.0
 * 
 */

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
		optGd.minimumHeight = 100;
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
		final Spinner spinner = optionControl[0].createSpinner("Point Size:", 1,10);
		spinner.setData(styler);
		spinner.addSelectionListener(
			new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					System.err.println(e);
					System.err.println("Sel is " + spinner.getSelection());
					PointStyler styler = (PointStyler)spinner.getData();
					//Graphic graphic = (Graphic)spinner.getData();
					Graphic graphic = styler.getSymbolizer().getGraphic();
					ScalarParameter size = new ScalarParameter();
					size.setConstantValue(new Integer(spinner.getSelection()));
					graphic.setSize(new ScalarParameter());
				}
			}
		);

		optionControl[1] = new OptionControl(optComp);
		final Button button = optionControl[1].createButton("Point Color", "...");
		button.addSelectionListener(
			new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					System.err.println(e);
					ColorDialog colorChooser = new ColorDialog(spinner.getShell());
					RGB color = colorChooser.getRGB();
					Graphic graphic = (Graphic)spinner.getData();
//					if(graphic instanceof GraphicMark) {
//						GraphicMark gm = (GraphicMark)graphic;
//						Fill = gm.getFill();
//						
//					}
				}
			}
		);
	}
	
	public void buildLineControls(LineStyler styler){
		optionControl = new OptionControl[3];
		optionControl[0] = new OptionControl(optComp);
		Spinner spinner = optionControl[0].createSpinner("LineWidth:", 1, 10);
		
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
		
		/**
		 * creates a spinner with the specified label, min and max
		 * 
		 * @param labelTxt
		 * @param min - note min must be > 0
		 * @param max
		 * @return the created Spinner
		 */
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
