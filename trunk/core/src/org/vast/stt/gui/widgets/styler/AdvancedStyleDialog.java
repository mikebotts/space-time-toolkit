package org.vast.stt.gui.widgets.styler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.vast.stt.scene.DataItem;
import org.vast.stt.style.CompositeStyler;
import org.vast.stt.style.DataStyler;

public class AdvancedStyleDialog implements SelectionListener 
{
	private Shell shell;

	private DataItem dataItem;
	private StyleOptionChooser optionChooser;
	private AdvancedOptionChooser advancedOptChooser;
	DataStructureTreeViewer dataStructureTree;
	private TabItem graphicTabItem;
	private Button addButton;
	private Button removeButton;
	private Button renameButton;
	private Combo stylesCombo;
	private DataStyler activeStyler;
	private List<DataStyler> stylerAL;
	
	public AdvancedStyleDialog(StyleOptionChooser optionChooser, DataItem item) {
		this.optionChooser = optionChooser;
		this.dataItem = item;
		//  init GUI components
		init();
		//  Set initial state of tabs and tree
		setStyler(dataItem.getStyler());
		dataStructureTree.setInput(item.getDataProvider().getDataNode());
		//  open the dialog
		shell.open();
	}
	
	/**
	 * Init new dialog shell and its contents
	 */
	private void init() {
		shell = new Shell();
		shell.setMinimumSize(new Point(400, 300));
		final GridLayout gridLayout_1 = new GridLayout();
		shell.setLayout(gridLayout_1);
		shell.setSize(553, 403);
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

		// AdvancedOptionChooser
		advancedOptChooser = new AdvancedOptionChooser(tabFolder);
		graphicTabItem.setControl(advancedOptChooser.getGroup());
		
		//  Geom tab
		final TabItem geometryTabItem = new TabItem(tabFolder, SWT.NONE);
		geometryTabItem.setText("Geometry");

		final Group geomGroup = new Group(tabFolder, SWT.NONE);
		geometryTabItem.setControl(geomGroup);

		//  DataStructure TreeViewer (right item of midComp)
		final Group dataStructureGroup = new Group(midComp, SWT.NONE);
		dataStructureGroup.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		dataStructureGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dataStructureGroup.setText("Data Structure");
		dataStructureGroup.setLayout(new FillLayout());
		dataStructureTree = new DataStructureTreeViewer(dataStructureGroup, SWT.BORDER);
		
		final Button closeButton = new Button(shell, SWT.NONE);
		closeButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		closeButton.setText("Close");
		closeButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
				shell.dispose();
			}
		});
		
		shell.layout();
	}
	
	/**
	 * Make this DataStyler the currently active Styler in the StyleWidget
	 * 
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
		//  Change DataTree/MappingTab contents
		setStylesComboItems();
		advancedOptChooser.buildControls(activeStyler);
		advancedOptChooser.getGroup().layout();
		advancedOptChooser.getGroup().redraw();
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
			
		}
		
		else if (e.widget == stylesCombo) {
			
		}
//			else if (e.widget == mappingCombo) {
//			
//		} else if (e.widget == lutButton) {
//			
//		}
	}
}
