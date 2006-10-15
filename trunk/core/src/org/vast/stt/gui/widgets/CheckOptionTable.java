
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
 *  Concrete subclasses include StyleWidget and DataProviderWidget
 * </p>
 *
 * <p>Copyright (c) 2006</p>
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
    protected Button deleteButton;
    protected Button addButton;
    protected Button enabledButton;
    protected Button advancedButton;
    protected int span = 3;


    abstract public OptionChooser createOptionChooser(Composite parent);


    public void setDataItem(DataItem item)
    {
        dataItem = item;
        mainGroup.setText(item.getName());
        enabledButton.setData(dataItem);
        enabledButton.setSelection(dataItem.isEnabled());
        //System.err.println("COT.setDI(): " + dataItem.getName());
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

        // Advanced Button
        advancedButton = new Button(mainGroup, SWT.NONE);
        final GridData gridData_7 = new GridData();
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
}
