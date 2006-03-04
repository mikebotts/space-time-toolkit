package org.vast.stt.gui.widgets.styler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.ogc.cdm.common.DataComponent;
import org.vast.data.ScalarIterator;
import org.vast.stt.scene.DataItem;
import org.vast.stt.style.CompositeStyler;
import org.vast.stt.style.DataStyler;

/**
 * 
 * @author tcook
 * @date 2/1/06
 *
 * TODO:  Don't allow multiple advancedStyleDialog for a dataStyler - otherwise,
 * 		  communicating between them will get nasty
 * 	
 */
public class AdvancedStyleDialog implements SelectionListener 
{
	private Shell shell;

	private DataItem dataItem;
	private AdvancedGraphicsTab advGraphicsTab;
	private AdvancedGeometryTab advGeomTab;
	DataStructureTreeViewer dataStructureTree;
	private TabItem graphicTabItem;
	private Button addButton;
	private Button removeButton;
	private Button renameButton;
	private Combo stylesCombo;
	private DataStyler activeStyler;
	private List<DataStyler> stylerAL;

	private Button closeButton;

	//  This dialog's dataItem cannot change, unlike StyleWidget
	//  However, it's list of styles can change if a style is added to the 
	//  DataItem via the 'add' button, or the 'add' button on the 
	//  StyleWidget
	public AdvancedStyleDialog(DataItem item, DataStyler activeStyler, OptionListener ol) {
		this.dataItem = item;
		//  init GUI components
		init(ol);
		//  Set initial state of tabs and tree based on dataItem's styles
		setStyler(dataItem.getStyler());
		//  set the currently active non-composite DataStyler based on 
		//  what was selected in StyleWidget wheb "advanced" button was pressed 
		dataStructureTree.setInput(item.getDataProvider().getDataNode());
		setActiveStyler(activeStyler);
		//  open the dialog
		shell.open();
	}
	
	/**
	 * Init new dialog shell and its contents
	 */
	private void init(OptionListener ol) {
		shell = new Shell();
		shell.setMinimumSize(new Point(480,350));
		final GridLayout gridLayout_1 = new GridLayout();
		shell.setLayout(gridLayout_1);
		shell.setSize(480,350);
		shell.setText("Advanced Style Options");

		//  Top composite for top row
		final Composite topComp = new Composite(shell, SWT.NONE);
		topComp.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		final GridLayout topLayout = new GridLayout();
		//  GridLayout w/5 columns
		topLayout.numColumns = 5;
		topComp.setLayout(topLayout);

		//  Top "Row" of combo and buttons
		final Label sLabel = new Label(topComp, SWT.NONE);
		sLabel.setText("Styles:");

		stylesCombo = new Combo(topComp, SWT.READ_ONLY);
		stylesCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		//  enable when I fix stylesChanged 
		//stylesCombo.setEnabled(false);
		
		stylesCombo.addSelectionListener(this);
		
		addButton = new Button(topComp, SWT.NONE);
		addButton.setText("Add");
		addButton.addSelectionListener(this);

		removeButton = new Button(topComp, SWT.NONE);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(this);

		renameButton = new Button(topComp, SWT.NONE);
		renameButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		renameButton.setText("Rename");
		renameButton.addSelectionListener(this);

		//  middle composite for tab and treeView 
		final Composite midComp = new Composite(shell, SWT.NONE);
		final GridData gridData_1 = new GridData(GridData.FILL, GridData.FILL, true, true);
		midComp.setLayoutData(gridData_1);
		final GridLayout midLayout = new GridLayout();
		midLayout.makeColumnsEqualWidth = true;
		//  GridLayout w/2 columns
		midLayout.numColumns = 3;
		midComp.setLayout(midLayout);

		//  Tab Folder (left item of midComp)
		final TabFolder tabFolder = new TabFolder(midComp, SWT.NONE);
		//tabFolder.setLayoutData(new GridData(277, 209));
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		//  Graphic TabItem
		graphicTabItem = new TabItem(tabFolder, SWT.NONE);
		graphicTabItem.setText("Graphic");

		// AdvanceGraphicTab
		advGraphicsTab = new AdvancedGraphicsTab(tabFolder, dataItem, ol);
		String [] mappableItems = getMappableItems();
		advGraphicsTab.setMappableItems(mappableItems);
		graphicTabItem.setControl(advGraphicsTab);
		
		//  Geom tab
		final TabItem geometryTabItem = new TabItem(tabFolder, SWT.NONE);
		geometryTabItem.setText("Geometry");

		// AdvanceGeomTab
		advGeomTab = new AdvancedGeometryTab(tabFolder);
		advGeomTab.setMappableItems(mappableItems);
		geometryTabItem.setControl(advGeomTab);

		//  DataStructure TreeViewer (right item of midComp)
		final Group dataStructureGroup = new Group(midComp, SWT.NONE);
		//dataStructureGroup.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		dataStructureGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dataStructureGroup.setText("Data Structure");
		dataStructureGroup.setLayout(new FillLayout());
		dataStructureTree = new DataStructureTreeViewer(dataStructureGroup, SWT.BORDER);
		
		closeButton = new Button(shell, SWT.NONE);
		closeButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		closeButton.setText("Close");
		closeButton.addSelectionListener(this);
		
		shell.layout();
	}
	
	/**
	 * 
	 * @return - the mappable property names for this dataItem
	 */
	protected String [] getMappableItems(){
		Set<String> mappableSet = new HashSet<String>();
		ScalarIterator it = 
			new ScalarIterator(dataItem.getDataProvider().getDataNode().getComponent(0));
		DataComponent dataCompTmp; 
		String name;
		String fullName;
		while(it.hasNext()){
			dataCompTmp = it.next();
			name = dataCompTmp.getName();
			//fullName = it.nextString();
			//  HACK to filter out segmentSize
			if(!name.equals("segmentSize")) 
				mappableSet.add(name);
		}
		
		return mappableSet.toArray(new String[]{});
	}
	
	/**
	 * Make this DataStyler the currently active Styler in the StyleWidget - 
	 * can be a compositeStyler
	 * @param newStyler
	 */
	public void setStyler(DataStyler newStyler){
		//  Check for CompositeStyler first...
		if(newStyler instanceof CompositeStyler) 
			stylerAL = ((CompositeStyler)newStyler).getStylerList();
		else {  // this is a single DataStyler
			stylerAL = new ArrayList<DataStyler>();
			stylerAL.add(newStyler);
		}
		setStylesComboItems();
	}

	//  NOTE:  styler should NOT be a composite styler here
	protected void setActiveStyler(DataStyler styler){
		//  Change DataTree/MappingTab contents
		if(activeStyler == null)
			activeStyler = stylerAL.get(0);
		if(activeStyler == null)
			return;
		activeStyler = styler;
		///  Update Graphic and Geom tab
		//advGraphicsTab.buildControls(activeStyler);
		advGraphicsTab.setActiveStyler(activeStyler);
		advGeomTab.setActiveStyler(activeStyler);
		//  Try setting index to activeStyler's pos in ArrayList
		Iterator<DataStyler> it = stylerAL.iterator();
		DataStyler styleTmp = null;
		int index = 0;
		while(it.hasNext()){
			styleTmp = it.next();
			if(styleTmp == activeStyler) {
				stylesCombo.select(index);
				return;
			}
			index++;
		}
		//System.err.println("ASD.setActiveStyler():  activeStyler NOT in styleAL");
		//System.err.println("NOW WHAT???");
	}
	
	protected void setActiveStyler(int index){
		activeStyler = stylerAL.get(index);
		advGraphicsTab.setActiveStyler(activeStyler);
		advGeomTab.setActiveStyler(activeStyler);
	}
	
	protected void setStylesComboItems(){
		stylesCombo.removeAll();
		int numStyles = stylerAL.size();
		for (int i=0; i<numStyles; i++){
			DataStyler stylerTmp = stylerAL.get(i);
			stylesCombo.add(stylerTmp.getName());
			if(i==0)
				activeStyler = stylerTmp;
		}
		stylesCombo.select(0);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		System.err.println(e);
		if(e.widget == addButton){
			
		} else if (e.widget == removeButton) {
			
		} else if (e.widget == renameButton) {
			
		} else if (e.widget == stylesCombo) {
			setActiveStyler(stylesCombo.getSelectionIndex());
		} else if (e.widget == closeButton){
			//  Remove OptLstnr
			advGraphicsTab.close();
			shell.close();
		}
	}
}
