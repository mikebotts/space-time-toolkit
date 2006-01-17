package org.vast.stt.gui.widgets;

import java.awt.Checkbox;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
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
public class StyleWidget implements ICheckStateListener, ISelectionChangedListener
		
{ // implements MouseListener{

	private Table table;
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
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		addButton.setText("+");

		final Button delBtn = new Button(stylesLabelComp, SWT.NONE);
		delBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.err.println(e);
				removeStyle(null);  //  remove currently selected row   
			}
		});
		delBtn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		delBtn.setText("-");

		//  CheckboxTableViewer for styles
		final CheckboxTableViewer checkboxTableViewer = CheckboxTableViewer.newCheckList(mainGroup, SWT.BORDER); 
		checkboxTableViewer.addCheckStateListener(this);
		checkboxTableViewer.addSelectionChangedListener(this);
		table = checkboxTableViewer.getTable();
		table.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final GridData tableGd = new GridData(GridData.BEGINNING, GridData.FILL, true, true);
		tableGd.minimumWidth = 125;
		tableGd.minimumHeight = 105;
		tableGd.widthHint = 125;
		tableGd.heightHint = 75;
		table.setLayoutData(tableGd);
		
		StyleTableContentProvider tableContentProv = new StyleTableContentProvider();
		StyleTableLabelProvider tableLabelProv = new StyleTableLabelProvider();
		checkboxTableViewer.setContentProvider(tableContentProv);
		checkboxTableViewer.setLabelProvider(tableLabelProv);
		checkboxTableViewer.setInput(new Object());
		
		final Label optLabel = new Label(mainGroup, SWT.NONE);
		optLabel.setText("Options:");
		final GridData gridData_4 = 
			new GridData(GridData.BEGINNING, GridData.END, false, false);
		gridData_4.verticalIndent = 7;
		gridData_4.heightHint = 16;
		optLabel.setLayoutData(gridData_4);

		//	  Options TableViewer for style options/combos/spinners
		final TableViewer optionsTV = new TableViewer(mainGroup, SWT.BORDER); 
		//optionsTV.addSelectionChangedListener(this);
		Table optTable = optionsTV.getTable();
		table.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final GridData optGD = new GridData(GridData.BEGINNING, GridData.FILL, true, true);
		optGD.minimumWidth = 125;
		optGD.minimumHeight = 105;
		optGD.widthHint = 125;
		optGD.heightHint = 75;
		optTable.setLayoutData(tableGd);
		
		OptTableContentProvider optContentProv = new OptTableContentProvider();
		OptTableLabelProvider optLabelProv = new OptTableLabelProvider();
		optionsTV.setContentProvider(optContentProv);
		optionsTV.setLabelProvider(optLabelProv);
		//  setInput inits state of Table here
		optionsTV.setInput(new Object());

		final Button advancedBtn = new Button(mainGroup, SWT.NONE);
		advancedBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.err.println(e);
			
			};
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
		
		//  Add Checkbox to styles Map and rerender Table
		//stylesMap.add(styler);
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
				//stylesMap.remove(st);  // swap out with method arh 
				break;
			}
		}
	}

	public void setActiveStyle(){
	}
	
	public void enableStyle(){
	}

	//  enabling checkbox causes ckState AND selChanged events
	public void checkStateChanged(CheckStateChangedEvent e) {
		// TODO Auto-generated method stub
		System.err.println(e);
		//  set enable on styler
		
		//  swap out optTable contents???
	}

	//  Selecting label causes ONLY selChanged event
	public void selectionChanged(SelectionChangedEvent e) {
		// TODO Auto-generated method stub
		System.err.println(e);
		//  Swap out optTable contents
	}
	
}

class StyleTableContentProvider implements IStructuredContentProvider{

	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return new String [] { "points", "lines", "morePts",
								"raster", "polygon", "whatece"};
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}
	
}

class StyleTableLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Object element) {
		return element.toString();
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
}

class OptTableContentProvider implements IStructuredContentProvider{

	public Object[] getElements(Object inputElement) {
		//  switch on inputElement to detrmine which Contents to show
		
		//  Subclass ContProv for all different styler types with options
		
		return new String [] { "pointSize", "pointColor"};
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}
	
}

class OptTableLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Object element) {
		return element.toString();
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
}