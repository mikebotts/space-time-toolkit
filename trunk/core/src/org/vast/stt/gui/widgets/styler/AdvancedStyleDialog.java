package org.vast.stt.gui.widgets.styler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.vast.stt.data.DataNode;
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
 * TODO:  Fix NPEs for non DataItems that try to instantiate AdvancedStyleDialog (this 
 * 		  will need to be done upstream somewhere)
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

	private Button okButton;

	//  This dialog's dataItem cannot change, unlike StyleWidget
	//  However, it's list of styles can change if a style is added to the 
	//  DataItem via the 'add' button, or the 'add' button on the 
	//  StyleWidget
	//  NOTE:  For now, if anything causes Dialog to fail, throw exception
	public AdvancedStyleDialog(DataItem item, DataStyler activeStyler, OptionListener ol) throws Exception {
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
		shell.setMinimumSize(new Point(400,250));
		shell.setLayout(new GridLayout(1, false));
		shell.setSize(600,350);
		shell.setText("Advanced Style Options");
		
		//  Top composite for top row
		final Composite topComp = new Composite(shell, SWT.NONE);
		topComp.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		//  GridLayout w/5 columns
		final GridLayout topLayout = new GridLayout(5, false);
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
		midComp.setLayout(new GridLayout(3, true));

		//  Tab Folder (left 2 columns of midComp)
		final TabFolder tabFolder = new TabFolder(midComp, SWT.NONE);
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
		
//		//  Bottom composite for OK/Cancel Btns 
		final Composite bottomComp = new Composite(shell, SWT.NONE);
		bottomComp.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		bottomComp.setLayout(new GridLayout(2, true));
		
		okButton = new Button(bottomComp, SWT.CENTER);
		GridData gd =  new GridData(SWT.END, SWT.CENTER, false, false);
		gd.widthHint = 60;
		okButton.setLayoutData(gd);
		okButton.setText("OK");
		okButton.addSelectionListener(this);

		closeButton = new Button(bottomComp, SWT.CENTER);
		gd =  new GridData(GridData.END, GridData.CENTER, false, false);
		gd.widthHint = 60;
		closeButton.setLayoutData(gd);
		closeButton.setText("Close");
		closeButton.addSelectionListener(this);
		
		shell.layout();
	}
	
	/**
	 * @return - the mappable property names for this dataItem
	 */
	protected String [] getMappableItems(){
		DataNode node = dataItem.getDataProvider().getDataNode();
		if(node == null) {
			System.err.println("ASD.getMappables():  Node is still null (probably not yet enabled.");
			return null;
		}
        List<String> mappingList = node.getPossibleScalarMappings();	
		return mappingList.toArray(new String[0]);
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
		System.err.println("ASD.setActiveStyler():  activeStyler NOT in styleAL");
		System.err.println("NOW WHAT???");
	}
	
	protected void setActiveStyler(int index){
		activeStyler = stylerAL.get(index);
		advGraphicsTab.setActiveStyler(activeStyler);
		advGeomTab.setActiveStyler(activeStyler);
		//graphicTabItem.setControl()
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
