/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.symbolizer;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.DataNode;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.tree.DataItem;

/**
 * <p>
 * <b>Title:</b> Advanced Symbolizer Dialog
 * </p>
 * 
 * <p>
 * <b>Dialog for controlling Advanced parameters for Symbolizers,
 *    including mapping functions.</b><br/> 
 * </p>
 * 
 * <p>
 * Copyright (c) 2007
 * </p>
 * 
 * @author Tony Cook
 * @date Jul 12, 2006
 * @version 1.0
 * 
 * TODO: Don't allow multiple advancedStyleDialog for a dataStyler - otherwise,
 * communicating between them will get nasty 
 * TODO: Fix NPEs for non DataItems
 * that try to instantiate AdvancedStyleDialog (this will need to be done
 * upstream somewhere) 
 * TODO: Have added symbolizers from AddSymbolizerDialog be
 * added to this widget also
 */
public class AdvancedSymbolizerDialog implements SelectionListener, STTEventListener
{
	private Shell shell;
	private DataItem dataItem;
	protected AdvancedGraphicsTab advGraphicsTab;
	private AdvancedGeometryTab advGeomTab;
	DataStructureTreeViewer dataStructureTree;
	private TabItem graphicTabItem;
	private Button addButton;
	private Button removeButton;
	private Button renameButton;
	private Combo symCombo;
	private Symbolizer activeSymbolizer;
	private List<Symbolizer> symbolizerList;
	protected Button closeButton;
	private Button applyBtn;
	private Font labelFont;
	
	// This dialog's dataItem cannot change, unlike SymbolizerWidget
	// However, it's list of symbolizers can change if a style is added to the
	// DataItem via the 'add' button, or the 'add' button on the
	// symbolizerWidget
	// NOTE: For now, if anything causes Dialog to fail, throw exception
	public AdvancedSymbolizerDialog(DataItem item, Symbolizer activeSymbolizer)
			throws Exception {

		this.dataItem = item;
		// init GUI components
		init();
		// Set initial state of tabs and tree based on dataItem's styles
		this.activeSymbolizer = activeSymbolizer;
		setSymbolizers(dataItem.getSymbolizers());
		dataStructureTree.setInput(item.getDataProvider().getDataNode());
		shell.open();
		//  register for DataItem events
		dataItem.addListener(this);
	}

	/**
	 * Init new dialog shell and its contents
	 */
	private void init() {
		shell = new Shell();
		//shell.setMinimumSize(new Point(400, 250));
		shell.setLayout(new GridLayout(1, false));
		shell.setSize(500,520);
		shell.setText(dataItem.getName() + "- Advanced Style Options");

		//  Label for item
		Label itemLabel = new Label(shell, SWT.LEAD);
		itemLabel.setText(dataItem.getName());
		GridData gd = new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1);
		itemLabel.setLayoutData(gd);
		//  Create label for Font - make sure to free it on close (and switch 
		//  to application-wide FontRegistry at some point)
		FontData fd = new FontData("Arial", 12, SWT.BOLD);
		labelFont = new Font(shell.getDisplay(), fd);
		itemLabel.setFont(labelFont);
		
			// Top composite for top row
		final Composite topComp = new Composite(shell, SWT.NONE);
		topComp.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER,
				false, false));
		// GridLayout w/5 columns
		final GridLayout topLayout = new GridLayout(5, false);
		topComp.setLayout(topLayout);
	
		// Top "Row" of combo and buttons
		final Label sLabel = new Label(topComp, SWT.NONE);
		sLabel.setText("Styles:");

		symCombo = new Combo(topComp, SWT.READ_ONLY);
		symCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false));
		// enable when I fix stylesChanged
		// stylesCombo.setEnabled(false);

		symCombo.addSelectionListener(this);

		addButton = new Button(topComp, SWT.NONE);
		addButton.setText("Add");
		addButton.addSelectionListener(this);

		removeButton = new Button(topComp, SWT.NONE);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(this);

		renameButton = new Button(topComp, SWT.NONE);
		renameButton.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));
		renameButton.setText("Rename");
		renameButton.addSelectionListener(this);

		// middle composite for Tabbed Folder
		final Composite midComp = new Composite(shell, SWT.NONE);
		midComp.setLayout(new GridLayout(1, true));
		gd = new GridData(GridData.FILL, GridData.FILL,true, true);
		midComp.setLayoutData(gd);
		
		// Tab Folder (left 2 columns of midComp)
		final TabFolder tabFolder = new TabFolder(midComp, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));//, 2, 1));

		// Graphic TabItem
		graphicTabItem = new TabItem(tabFolder, SWT.NONE);
		graphicTabItem.setText("Graphic");

		// AdvanceGraphicTab
		advGraphicsTab = new AdvancedGraphicsTab(tabFolder, dataItem);
		String[] mappableItems = getMappableItems();
		advGraphicsTab.setMappableItems(mappableItems);
		graphicTabItem.setControl(advGraphicsTab);

		// Geom tab
		final TabItem geometryTabItem = new TabItem(tabFolder, SWT.NONE);
		geometryTabItem.setText("Geometry");

		// AdvanceGeomTab
		advGeomTab = new AdvancedGeometryTab(tabFolder);
		advGeomTab.setMappableItems(mappableItems);
		geometryTabItem.setControl(advGeomTab);

		//  Bottom comp for mapping function options and DataStructureTree Viewer
		final Composite bottomComp = new Composite(shell, SWT.NONE);
		bottomComp.setLayout(new GridLayout(2, true));
		gd = new GridData(GridData.FILL, GridData.FILL,true, true);
		bottomComp.setLayoutData(gd);

		//  MappingFunctionOpts Editor (left Item of bottomComp)
		Group mappingGroup = new Group(bottomComp, 0x0);
		gd = new GridData(GridData.FILL, GridData.FILL,true, true);
		mappingGroup.setLayoutData(gd);
		mappingGroup.setLayout(new GridLayout(3, false));
		mappingGroup.setText("Mapping Function Options:");
		MappingOptionChooser moc = new MappingOptionChooser(mappingGroup);
		
		// DataStructure TreeViewer (right item of bottomComp)
		Group dstGroup = new Group(bottomComp, 0x0);
		gd = new GridData(GridData.FILL, GridData.FILL,true, true);
		dstGroup.setLayoutData(gd);

		dstGroup.setLayout(new FillLayout());
		dstGroup.setText("Data Structure");
		dataStructureTree = new DataStructureTreeViewer(dstGroup, SWT.BORDER);
		
		// Bottom composite for OK/Cancel Btns
		final Composite buttonComp = new Composite(shell, SWT.NONE);
		buttonComp.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		buttonComp.setLayout(new GridLayout(2, true));

		applyBtn = new Button(buttonComp, SWT.CENTER);
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		gd.widthHint = 60;
		applyBtn.setLayoutData(gd);
		applyBtn.setText("Apply");
		applyBtn.addSelectionListener(this);

		closeButton = new Button(buttonComp, SWT.CENTER);
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		gd.widthHint = 60;
		closeButton.setLayoutData(gd);
		closeButton.setText("Close");
		closeButton.addSelectionListener(this);

		shell.layout();
	}

	/**
	 * @return - the mappable property names for this dataItem
	 */
	protected String[] getMappableItems() {
		DataNode node = dataItem.getDataProvider().getDataNode();
		if (node == null) {
			System.err
					.println("ASD.getMappables():  Node is still null (probably not yet enabled.");
			return null;
		}
		List<String> mappingList = node.getPossibleScalarMappings();
		return mappingList.toArray(new String[0]);
	}

	/**
	 */
	public void setSymbolizers(List<Symbolizer> symbolizerList) {
		this.symbolizerList = symbolizerList;
		// Remove old symbolizers
		// symCombo.removeAll();
		int numStyles = symbolizerList.size();
		for (int i = 0; i < numStyles; i++) {
			Symbolizer symTmp = symbolizerList.get(i);
			symCombo.add(symTmp.getName());
		}
		int activeIndex = symbolizerList.indexOf(activeSymbolizer);
		if (activeIndex == -1)
			symCombo.select(0);
		else
			symCombo.select(activeIndex);
		setActiveSymbolizer(symCombo.getSelectionIndex());
	}

	protected void closeDialog(){
		advGraphicsTab.close(); // why close tab?
		shell.close();
	}
	
	protected void setActiveSymbolizer(int index) {
		activeSymbolizer = symbolizerList.get(index);
		advGraphicsTab.setActiveSymbolizer(activeSymbolizer);
		advGeomTab.setActiveSymbolizer(activeSymbolizer);
		// graphicTabItem.setControl()
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		System.err.println(e);
		if (e.widget == addButton) {

		} else if (e.widget == removeButton) {

		} else if (e.widget == renameButton) {

		} else if (e.widget == symCombo) {
			setActiveSymbolizer(symCombo.getSelectionIndex());
		} else if (e.widget == applyBtn) {
			//  apply mappings
		} 
		else if (e.widget == closeButton){
			//  de-register as DataItem listener
			dataItem.removeListener(this);
			labelFont.dispose();
			closeDialog();
		}
	}
	
	public void handleEvent(STTEvent e) {
		switch (e.type)
		{
		case ITEM_OPTIONS_CHANGED:
		case ITEM_SYMBOLIZER_CHANGED:
			System.err.println("AdvOptContr.handleEvt = " + e);
			//  Need to refresh all tabs/windows in this dialog (including mapping stuff)
			advGraphicsTab.optionController.loadFields();
		}		
	}	
	
}
