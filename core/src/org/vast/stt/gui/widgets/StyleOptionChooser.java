package org.vast.stt.gui.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;
import org.vast.ows.sld.Graphic;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.GraphicSource;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Stroke;
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
 *  Currently supports the following Styler types
 *  	Point, Line    
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 18, 2006
 * @version 1.0
 * 
 * TODO  add chooser/mapping widget when user selects + (add) style
 * TODO  add support for other Stylers - this may be best done by splitting each
 * 		 styler into separate "ControlProvider" classes that provide the options
 *       they allow the user to change and the actionListeneres for changes. 
 * TODO  support advanced options      
 */

public class StyleOptionChooser {
	Composite optComp;
	OptionControl [] optionControl;
	
	public StyleOptionChooser(Composite parent){
		init(parent);
	}

	public void init(Composite parent){
		ScrolledComposite optScr = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		optScr.setExpandHorizontal(true);
		optScr.setExpandVertical(true);
		optComp = new Composite(optScr, SWT.NONE);
		optScr.setContent(optComp);

		GridData scrollerGD = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true);
		scrollerGD.heightHint = 80;
		scrollerGD.widthHint = 125;
		optScr.setLayoutData(scrollerGD);
		
		GridLayout optLayout = new GridLayout(2, false);
		optComp.setLayout(optLayout);
		optComp.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		//buildControls(new LineStyler());
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));	
		//  initial controls should come from DataItem (vie StyleWidget)
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
					PointStyler styler = (PointStyler)spinner.getData();
					//  uncomment when Stylers are working
//					Graphic graphic = styler.getSymbolizer().getGraphic();
//					ScalarParameter size = new ScalarParameter();
//					size.setConstantValue(new Integer(spinner.getSelection()));
//					graphic.setSize(new ScalarParameter());
				}
			}
		);

		optionControl[1] = new OptionControl(optComp);
		final Button button = optionControl[1].createButton("Point Color:", "...");
		button.setData(styler);
		button.addSelectionListener(
			new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					ColorDialog colorChooser = new ColorDialog(button.getShell());
					RGB rgb = colorChooser.open();
					//  uncomment when Stylers are working
//					PointStyler styler = (PointStyler)button.getData();
//					Graphic graphic = styler.getSymbolizer().getGraphic();
//					List graphicSourceList = graphic.getImages();
//					if(graphicSourceList == null)  return;
//					GraphicSource graphicSource = (GraphicSource)graphicSourceList.get(0);
//					if(graphicSource instanceof GraphicMark) {
//						GraphicMark gm = (GraphicMark)graphicSource;
//						ScalarParameter newColor = new ScalarParameter();
//						newColor.setConstantValue(rgb);
//						gm.getFill().setColor(newColor);
//					}
				}
			}
		);
	}
	
	public void buildLineControls(LineStyler styler){
		//optionControl = new OptionControl[7];
		optionControl = new OptionControl[2];
		optionControl[0] = new OptionControl(optComp);
		final Spinner spinner = optionControl[0].createSpinner("LineWidth:", 1, 10);
		spinner.setData(styler);
		spinner.addSelectionListener(
			new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					LineStyler styler = (LineStyler)spinner.getData();
					//  uncomment when Stylers are working
//					Stroke stroke = styler.getSymbolizer().getStroke();
//					ScalarParameter width = new ScalarParameter();
//					width.setConstantValue(new Integer(spinner.getSelection()));
//					stroke.setWidth(width);
				}
			}
		);
			
		optionControl[1] = new OptionControl(optComp);
		final Button button = optionControl[1].createButton("Line Color:", "...");
		button.setData(styler);
		button.addSelectionListener(
			new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					ColorDialog colorChooser = new ColorDialog(button.getShell());
					RGB rgb = colorChooser.open();
					//  uncomment when Stylers are working
//					LineStyler styler = (LineStyler)button.getData();
//					Stroke stroke = styler.getSymbolizer().getStroke();
//					ScalarParameter newColor = new ScalarParameter();
//					newColor.setConstantValue(rgb);
//					stroke.setColor(newColor);
				}
			}
		);
		//  add a bunch of controls to test scrolling
//		optionControl[2] = new OptionControl(optComp);
//		optionControl[2].createCombo("Line Test:", new String [] { "aaa", "bbb", "ccc" } );
//		optionControl[3] = new OptionControl(optComp);
//		optionControl[3].createSpinner("LineWidth:", 1, 10);
//		optionControl[4] = new OptionControl(optComp);
//		optionControl[4].createSpinner("LineWidth:", 1, 10);
//		optionControl[5] = new OptionControl(optComp);
//		optionControl[5].createSpinner("LineWidth:", 1, 10);
//		optionControl[6] = new OptionControl(optComp);
//		optionControl[6].createSpinner("LineWidth:", 1, 10);
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
			//label.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GREEN));
			GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			gd.widthHint = 65;
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
			GridData gd = new GridData(SWT.RIGHT, SWT.FILL, true,false);
			spinner.setLayoutData(gd);
			return spinner;
		}
		
		public Combo createCombo(String labelTxt, String [] opts){
			createLabel(labelTxt);
			control = new Combo(parent, 0x0);
			Combo combo = (Combo)control;
			combo.setItems(opts);

			GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
			combo.setLayoutData(gd);
			return combo;
		}
		
		public Button createButton(String labelTxt, String text) { // sellistener
			createLabel(labelTxt);
			control = new Button(parent, SWT.PUSH);
			Button button = (Button)control;
			button.setText(text);

			GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true,false);
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
