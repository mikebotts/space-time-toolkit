package org.vast.stt.gui.widgets;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;
 
/**
 * <p><b>Title:</b><br/>
 * StyleWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling styler options for a DataItem.  Note that I 
 *  used a trial version of SWTDesigner to build portions of this widget.     
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
{ 
	private Table table;
	Group mainGroup;
	Composite optionsComp;
	ScrolledComposite stylesSC;
	CheckboxTableViewer checkboxTableViewer;
	StyleOptionChooser optChooser;
	Set<DataStyler> stylers;
	DataStyler activeStyler;
	
	public StyleWidget(Composite parent){
		//  Get stylers from DataItem.  For now, just put in some dummy data
		stylers  = new HashSet<DataStyler>();
		PointStyler ptStyler = new PointStyler();
		ptStyler.setName("Points0");
		LineStyler lineStyler = new LineStyler();
		lineStyler.setName("Lines0");
		stylers.add(ptStyler);
		stylers.add(lineStyler);
		
		init(parent);
	}
	
	public void init(Composite parent) {
		final ScrolledComposite mainSC = new ScrolledComposite(parent,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		mainSC.setExpandVertical(true);
		mainSC.setExpandHorizontal(true);
		//  START HERE:  Try this with OptSCroller
		mainSC.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		mainGroup = new Group(mainSC, SWT.NONE);
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
//			  TODO:  popup "styler type" dialog
				PointStyler styler = new PointStyler();
				styler.setName("more pts");
				addStyle(styler);  
			}
		});
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		addButton.setText("+");

		final Button delBtn = new Button(stylesLabelComp, SWT.NONE);
		delBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(activeStyler != null)
				removeStyle(activeStyler);  //  remove currently selected row   
			}
		});
		delBtn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		delBtn.setText("-");

		//  CheckboxTableViewer for styles
		checkboxTableViewer = CheckboxTableViewer.newCheckList(mainGroup, SWT.BORDER); 
		checkboxTableViewer.addCheckStateListener(this);
		checkboxTableViewer.addSelectionChangedListener(this);
		table = checkboxTableViewer.getTable();
		table.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final GridData tableGd = new GridData(GridData.BEGINNING, GridData.FILL, true, true);
		tableGd.minimumWidth = 125;
		tableGd.minimumHeight = 75;
		tableGd.widthHint = 125;
		tableGd.heightHint = 55;
		table.setLayoutData(tableGd);
		
		StyleTableContentProvider tableContentProv = new StyleTableContentProvider();
		StyleTableLabelProvider tableLabelProv = new StyleTableLabelProvider();
		checkboxTableViewer.setContentProvider(tableContentProv);
		checkboxTableViewer.setLabelProvider(tableLabelProv);
		checkboxTableViewer.setInput(stylers);
		
		final Label optLabel = new Label(mainGroup, SWT.NONE);
		optLabel.setText("Options:");
		final GridData gridData_4 = 
			new GridData(GridData.BEGINNING, GridData.END, false, false);
		gridData_4.verticalIndent = 7;
		gridData_4.heightHint = 16;
		optLabel.setLayoutData(gridData_4);

		//	  OptionsChooser
		optChooser = new StyleOptionChooser(mainGroup);
		
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
	
	public void setStylers(DataStyler [] newStylers){
		stylers = new HashSet<DataStyler>();
		for(int i=0;i<newStylers.length;i++)
			stylers.add(newStylers[i]);
		//  Change options panel to show Point options
		checkboxTableViewer.setInput(stylers);		
	}
	
	//  TODO  Will this method be polymorhped to accept different stylers?
	private void addStyle(PointStyler styler){
		//  Add Checkbox to stylers Set and rerender Table
		stylers.add(styler);
		activeStyler = styler;
		//  Change options panel to show Point options
		checkboxTableViewer.setInput(stylers);		
	}
	
	//  FOR test, just take first style out of map
	private void removeStyle(DataStyler styler){
		stylers.remove(styler);
		//  reset activeStyler
		// ...
		checkboxTableViewer.setInput(stylers);
	}

	public void setText(String itemName){
		mainGroup.setText(itemName);
	}
	
	//  enabling checkbox causes ckState AND selChanged events
	public void checkStateChanged(CheckStateChangedEvent e) {
		// TODO Auto-generated method stub
		//  e.getElement returns checked Styler
		//System.err.println(e.getElement() + ", checked = " + e.getChecked());
		//  set enable on styler
		DataStyler styler = (DataStyler)e.getElement();
		styler.setEnabled(e.getChecked());
	}
	
	//  Selecting label causes ONLY selChanged event
	public void selectionChanged(SelectionChangedEvent e) {
		StructuredSelection selection = (StructuredSelection)e.getSelection();
		DataStyler styler = (DataStyler)selection.getFirstElement();
		//  Check to see if selected Styler has really changed
		if(styler == activeStyler){
			//System.err.println("Selection not really changed");
			return;
		}
		System.err.println("Selection CHANGED");
		activeStyler = styler;
		optChooser.buildControls(styler);
	}
	
}

class StyleTableContentProvider implements IStructuredContentProvider{

	DataStyler[] stylers;
	
	public Object [] getElements (Object inputElement){
		HashSet stylers = (HashSet)inputElement;
		DataStyler [] stylerArr = (DataStyler [])stylers.toArray(new DataStyler[]{});
		return stylerArr;
	}
		
	public void dispose() {
		// TODO Auto-generated method stub
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		//System.err.println("Input changed for " + viewer);
	}
	
}

class StyleTableLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Object element) {
//		return element.toString();
		DataStyler styler = (DataStyler)element;
		return styler.getName();
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
