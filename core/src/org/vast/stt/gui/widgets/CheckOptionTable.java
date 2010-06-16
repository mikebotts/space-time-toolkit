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

package org.vast.stt.gui.widgets;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b><br/>
 * CheckOptionTable 
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Abstract "double table" widget, with top panel being checkboxTable, 
 *  and bottom panel being a list of OptionControls.  Not sure what I 
 *  should call it.     
 *  
 *  Concrete subclasses include SymbolizerWidget and DataProviderWidget
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Jan 26, 2006
 * @version 1.0
 * 
 */

abstract public class CheckOptionTable implements ICheckStateListener, ISelectionChangedListener, SelectionListener
{
    protected DataItem dataItem;
    protected Table table;
    protected Group mainGroup;
    protected Composite optionsComp;
    protected ScrolledComposite stylesSC;
    protected CheckboxTableViewer checkboxTableViewer;
    protected OptionChooser optionChooser;
    protected String checkboxTableLabel;

    protected boolean allowAddRemove = true;
    protected boolean showImagePopup = true;
   
	protected Button deleteButton;
    protected Button addButton;
    protected Button enabledButton;
    protected Button advancedButton;
    protected int span = 3;
	protected Button popupImageBtn;

    abstract public OptionChooser createOptionChooser(Composite parent);

    public void setDataItem(DataItem item)
    {
        dataItem = item;
        mainGroup.setText(item.getName());
        enabledButton.setData(dataItem);
        enabledButton.setSelection(dataItem.isEnabled());
        //System.err.println("COT.setDI(): " + dataItem.getName());
        //  Need a NULL check here, now, because setDataItem is being called before 
        //  optionController gets instantiated the first time.  (Fix it...)
//        if(optionChooser.optionController != null)
//        	optionChooser.optionController.loadFields();
    }


    public void init(Composite parent)
    {
        //  Check for DataItem, OptionChooser
        final ScrolledComposite mainSC = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        mainSC.setExpandVertical(true);
        mainSC.setExpandHorizontal(true);
        //  START HERE:  Try this with OptSCroller
        mainSC.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

        mainGroup = new Group(mainSC, SWT.NONE);
        mainGroup.setText("Item Name");
        final GridLayout gridLayout = new GridLayout(span, false);
        mainGroup.setLayout(gridLayout);
        mainGroup.setLocation(0, 0);

        // Enabled Button
        enabledButton = new Button(mainGroup, SWT.CHECK);
        enabledButton.setData(dataItem);
        GridData gridData = new GridData();
        gridData.verticalIndent = 7;
        gridData.horizontalAlignment = GridData.BEGINNING;
       	gridData.horizontalSpan = span;
        enabledButton.setLayoutData(gridData);
        enabledButton.setText("enabled");

       
        
        // Styles Label
        final Label stylesLabel = new Label(mainGroup, SWT.RIGHT);
        stylesLabel.setText(checkboxTableLabel);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = span - (allowAddRemove ? 2 : 0);
        // gridData.widthHint = 55;
        stylesLabel.setLayoutData(gridData);

        // Add Style Button
        if (allowAddRemove)
        {
            addButton = new Button(mainGroup, SWT.NONE);
            final GridData gridData_4 = new GridData();
            //gridData_4.horizontalAlignment = GridData.FILL;
            gridData_4.horizontalAlignment = GridData.END;
            gridData_4.grabExcessHorizontalSpace = false;
            //gridData_4.grabExcessHorizontalSpace = true;
            gridData_4.horizontalSpan = 1;
            gridData_4.widthHint = 30;
            addButton.setLayoutData(gridData_4);
            addButton.setText("+");
            addButton.setToolTipText("Add Graphic");

            deleteButton = new Button(mainGroup, SWT.NONE);
            final GridData gridData_5 = new GridData();
            //gridData_5.horizontalAlignment = GridData.FILL;
            gridData_5.horizontalAlignment = GridData.END;
            //gridData_5.grabExcessHorizontalSpace = true;
            gridData_5.grabExcessHorizontalSpace = false;
            gridData_5.horizontalSpan = 1;
            gridData_5.widthHint = 30;
            deleteButton.setLayoutData(gridData_5);
            deleteButton.setText("-");
            deleteButton.setToolTipText("Delete styler");
        }

        //  CheckboxTableViewer for styles
        checkboxTableViewer = CheckboxTableViewer.newCheckList(mainGroup, SWT.BORDER | SWT.SINGLE);
        table = checkboxTableViewer.getTable();
        table.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
        final GridData tableGd = new GridData(GridData.FILL, GridData.FILL, true, true);

        tableGd.minimumHeight = 75;
        //tableGd.widthHint = 125;
        tableGd.heightHint = 55;
        tableGd.horizontalSpan = span;
        tableGd.grabExcessVerticalSpace = true;
        table.setLayoutData(tableGd);

        // Options Label
        final Label optLabel = new Label(mainGroup, SWT.NONE);
        final GridData gridData_6 = new GridData();
        gridData_6.horizontalSpan = span;
        gridData_6.horizontalAlignment = GridData.BEGINNING;
        optLabel.setLayoutData(gridData_6);
        optLabel.setText("Options:");

        // OptionsChooser
        optionChooser = createOptionChooser(mainGroup);

        if(showImagePopup) {
        	popupImageBtn = new Button(mainGroup, SWT.PUSH);
        	gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        	gridData.horizontalSpan = 1;
        	popupImageBtn.setLayoutData(gridData);
        	popupImageBtn.setText("Image Viewer");
        	popupImageBtn.setToolTipText("Popup Image Viewer for this item");
        	popupImageBtn.addSelectionListener(this);
        }
        
        // Advanced Button
        advancedButton = new Button(mainGroup, SWT.NONE);
        final GridData gridData_7 = new GridData();
        if(showImagePopup)
        	gridData_7.horizontalSpan = span-1;
        else 
        	gridData_7.horizontalSpan = span;
        gridData_7.horizontalAlignment = GridData.END;
        advancedButton.setLayoutData(gridData_7);
        advancedButton.setText("Advanced...");

        mainSC.setContent(mainGroup);
        mainSC.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        //  init Listeners
        addSelectionListener(this);
        addCheckboxTableListener(this, this);
    }


    public void addSelectionListener(SelectionListener sl)
    {
        if (allowAddRemove)
        {
            addButton.addSelectionListener(sl);
            deleteButton.addSelectionListener(sl);
        }
        enabledButton.addSelectionListener(sl);
        advancedButton.addSelectionListener(sl);
    }


    public void removeSelectionListener(SelectionListener sl)
    {
        if (allowAddRemove)
        {
            addButton.removeSelectionListener(sl);
            deleteButton.removeSelectionListener(sl);
        }
        enabledButton.removeSelectionListener(sl);
        advancedButton.removeSelectionListener(sl);
    }


    public void addCheckboxTableListener(ICheckStateListener checkStateListener, ISelectionChangedListener selectionChangedListener)
    {
        checkboxTableViewer.addCheckStateListener(checkStateListener);
        checkboxTableViewer.addSelectionChangedListener(selectionChangedListener);
    }


    public void removeCheckboxTableListener(ICheckStateListener checkStateListener, ISelectionChangedListener selectionChangedListener)
    {
        checkboxTableViewer.removeCheckStateListener(checkStateListener);
        checkboxTableViewer.removeSelectionChangedListener(selectionChangedListener);
    }


    public void setCheckboxTableContentProvider(IContentProvider tableContentProv)
    {
        checkboxTableViewer.setContentProvider(tableContentProv);
    }


    public void setCheckboxTableLabelProvider(ILabelProvider tableLabelProv)
    {
        checkboxTableViewer.setLabelProvider(tableLabelProv);
    }
    
    public void setShowImagePopup(boolean showImagePopup) {
		this.showImagePopup = showImagePopup;
	}
}
