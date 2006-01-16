package org.vast.stt.gui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.style.AbstractStyler;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.PointStyler;
import com.swtdesigner.SWTResourceManager;

/**
 * <p><b>Title:</b><br/>
 * StyleWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling styler options for a DataItem.  Note that I 
 *  used a trial version of SWTDesigner to build this.     
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 14, 2006
 * @version 1.0
 * 
 * @TODO  Modify other scrolled widgets to use the convention here.
 * @TODO  Listeners- inline vc. separate?
 * @TODO  Tie actions to DataItem properties
 * TODO  Mod scrolled widgets to size up when parent is sized up
 */
public class StyleWidget {

	Composite stylesComp;  //  Holds style Checkboxes
	Composite optionsComp;
	ScrolledComposite stylesSC;
	Map<Widget,DataStyler> stylesMap;
	
	public StyleWidget(Composite parent){
		init(parent);
		stylesMap = new HashMap<Widget,DataStyler>();
	}
	
	public void init(Composite parent) {
		final ScrolledComposite mainSC = new ScrolledComposite(parent,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		mainSC.setExpandVertical(true);
		mainSC.setExpandHorizontal(true);
		mainSC.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, true, true, 1, 1));

		final Group mainGroup = new Group(mainSC, SWT.NONE);
		mainGroup.setText("Item Name");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainGroup.setLayout(gridLayout);
		mainGroup.setLocation(0, 0);

		final Button enabledButton = new Button(mainGroup, SWT.CHECK);
		enabledButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.err.println("Enable selected");
			}
		});
		final GridData gridData = new GridData();
		gridData.verticalIndent = 7;
		enabledButton.setLayoutData(gridData);
		enabledButton.setText("enabled");

		final Composite stylesLabelComp = new Composite(mainGroup, SWT.NONE);
		final GridData gridData_1 = 
			new GridData(152, 34);
		stylesLabelComp.setLayoutData(gridData_1);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 6;
		gridLayout_2.makeColumnsEqualWidth = true;
		stylesLabelComp.setLayout(gridLayout_2);

		final Label stylesLabel = new Label(stylesLabelComp, SWT.NONE);
		final GridData gridData_3 = 
			new GridData(GridData.BEGINNING, GridData.END, false, false, 3, 1);
		gridData_3.horizontalIndent = -5;
		gridData_3.heightHint = 16;
		stylesLabel.setLayoutData(gridData_3);
		stylesLabel.setText("Styles:");

		final Button addButton = new Button(stylesLabelComp, SWT.NONE);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.err.println(e);
				addStyle(null);  //  TODO:  popup "styler type" dialog
			}
		});
		addButton.setLayoutData(
				new GridData(GridData.FILL, GridData.CENTER, false, false));
		addButton.setText("+");

		final Button delBtn = new Button(stylesLabelComp, SWT.NONE);
		delBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.err.println(e);
				removeStyle(null);  //  remove currently selected row   
			}
		});
		delBtn.setLayoutData(
				new GridData(GridData.FILL, GridData.CENTER, false, false));
		delBtn.setText("-");

		stylesSC = 
			new ScrolledComposite(mainGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		final GridData gd = new GridData(133, 39);
		stylesSC.setLayoutData(gd);
		stylesSC.setMinWidth(100);
		stylesSC.setMinHeight(100);
		stylesSC.setExpandVertical(true);
		stylesSC.setExpandHorizontal(true);

		stylesComp = new Composite(stylesSC, SWT.BORDER);
		stylesComp.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout_1 = new GridLayout(1, false);
		stylesComp.setLayout(gridLayout_1);
		final GridData gd2 = new GridData();
		gd2.horizontalAlignment = SWT.FILL;
		gd2.verticalAlignment = SWT.FILL;
		stylesComp.setLayoutData(gd2);
		//stylesComp.setLocation(0, 0);
		//stylesComp.setSize(196, 84);
		stylesSC.setContent(stylesComp);
		//
		//stylesSC.setMinSize(stylesComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		Button btn = new Button(stylesComp, SWT.CHECK);
		btn.setText("New Style");
		btn.setBackground(SWTResourceManager.getColor(255, 255, 255));

		btn.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) 
			{	
				Button btn = (Button)e.widget;
				btn.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED));
				btn.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
		});

		final Composite composite = new Composite(stylesComp, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(255, 128, 64));
		composite.setLayoutData(new GridData());
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		composite.setLayout(gridLayout_3);

		final Button button = new Button(composite, SWT.CHECK);

		final Label testcbcompLabel = new Label(composite, SWT.NONE);
		testcbcompLabel.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				System.err.println("MD2020");
			}
		});
		testcbcompLabel.setBackground(SWTResourceManager.getColor(255, 128, 0));
		testcbcompLabel.setLayoutData(new GridData(SWT.DEFAULT, 16));
		testcbcompLabel.setText("TestCbComp");

		
		final Label optLabel = new Label(mainGroup, SWT.NONE);
		final GridData gridData_4 = 
			new GridData(GridData.BEGINNING, GridData.END, false, false);
		gridData_4.verticalIndent = 7;
		gridData_4.heightHint = 14;
		optLabel.setLayoutData(gridData_4);
		optLabel.setText("Options:");
		//new Label(mainGroup, SWT.NONE);

		final ScrolledComposite optionsSC = 
			new ScrolledComposite(mainGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		final GridData gridData_5 = 
			new GridData(133, 39);
		optionsSC.setLayoutData(gridData_5);
		optionsSC.setMinWidth(100);
		optionsSC.setMinHeight(100);
		optionsSC.setExpandVertical(true);
		optionsSC.setExpandHorizontal(true);

		optionsComp = new Composite(optionsSC, SWT.NONE);
		optionsComp.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		optionsComp.setLayout(new GridLayout());
		optionsComp.setLocation(0, 0);
		optionsComp.setSize(196, 84);
		optionsSC.setContent(optionsComp);

		final Button advancedBtn = new Button(mainGroup, SWT.NONE);
		advancedBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.err.println(e);
			
			}
		});
		final GridData gridData_6 = 
			new GridData(GridData.END, GridData.CENTER, false, false);
		gridData_6.verticalIndent = 7;
		advancedBtn.setLayoutData(gridData_6);
		advancedBtn.setText("Advanced...");

		mainGroup.setSize(236, 371);
		mainSC.setContent(mainGroup);
		mainSC.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	//  TODO  Will this method be polymorhped to accept different stylers?
	//  
	private void addStyle(PointStyler styler){
		//  TODO:  Get to the actual things we want to change here...
		
		//  Add Checkbox to styles Map and rerender stylesComp
		//stylesMap.add(styler);
		Button btn = new Button(stylesComp, SWT.CHECK);
		btn.setText("PointStyle # " + stylesMap.size());
		
		stylesSC.setContent(stylesComp);
		stylesMap.put(btn, styler);
		
		//  Change options panel to show Point options
	}
	
	//  FOR test, just take first style out of map
	private void removeStyle(DataStyler styler){
		//  Remove the Btn from stylesComp and reset stylesSC content to force redraw
		//Control btn = stylesMap.get(styler);
		Iterator<Widget> keysIt = stylesMap.keySet().iterator();
		Iterator<DataStyler> valuesIt = stylesMap.values().iterator();
		DataStyler stylerTmp;
		Widget btnTmp;
		while(keysIt.hasNext()){
			btnTmp = keysIt.next();
			stylerTmp = valuesIt.next();
			DataStyler st = stylerTmp;
			if(stylerTmp == st) { //  swap out with method arg
				stylesMap.remove(st);  // swap out with method arh 
				btnTmp.dispose();
				btnTmp = null;
				stylesSC.setContent(stylesComp);
				//stylesMap.remove(st);  // swap out with method arh 
				break;
			}
		}
	}
	
}