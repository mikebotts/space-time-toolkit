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
    Alexandre Robin <robin@nsstc.uah.edu>
    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.DataProcess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.stt.data.DataException;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.sml.SMLProvider;


/**
 * <p><b>Title:</b><br/>
 * DataProcesWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Widget for controlling DataProcess options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date May 10, 2006
 * @version 1.0
 * 
 */
public class DataProcessWidget implements ISelectionChangedListener, SelectionListener
{ 
    protected DataItem dataItem;
    protected Table table;
    protected Group mainGroup;
    protected Composite optionsComp;
    protected ScrolledComposite stylesSC;
    protected ListViewer listViewer;
    protected OptionChooser optionChooser;
    protected String checkboxTableLabel;

    protected boolean allowAddRemove = true;
    protected Button deleteButton;
    protected Button addButton;
    protected Button enabledButton;
    protected Button updateButton;
    protected int span = 3;
    java.util.List<DataProcess> processAL;
    DataProcess activeProcess;
    
    public DataProcessWidget(Composite parent){
        processAL  = new ArrayList<DataProcess>(2);
        checkboxTableLabel = "Providers:";
        allowAddRemove = false;
        init(parent);
    }
    
    public void init(Composite parent) {
        // Check for DataItem, OptionChooser
        final ScrolledComposite mainSC = new ScrolledComposite(parent,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        mainSC.setExpandVertical(true);
        mainSC.setExpandHorizontal(true);
        // START HERE: Try this with OptSCroller
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
        enabledButton.addSelectionListener(this);

        // Styles Label
        final Label stylesLabel = new Label(mainGroup, SWT.RIGHT);
        stylesLabel.setText(checkboxTableLabel);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = span - (allowAddRemove ? 2 : 0);
        // gridData.widthHint = 55;
        stylesLabel.setLayoutData(gridData);

        // CheckboxTableViewer for styles
        listViewer = new ListViewer(mainGroup,SWT.SINGLE);
        listViewer.setLabelProvider(new ProcessLabelProvider());
        listViewer.setContentProvider(new ProcessContentProvider());
        listViewer.addSelectionChangedListener(this);

        final GridData tableGd = new GridData(GridData.FILL, GridData.FILL, true, true);
        tableGd.minimumHeight = 75;
        tableGd.heightHint = 45;
        tableGd.horizontalSpan = span;
        tableGd.grabExcessVerticalSpace = false;
        listViewer.getControl().setLayoutData(tableGd);

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
        updateButton = new Button(mainGroup, SWT.NONE);
        final GridData gridData_7 = new GridData();
        gridData_7.horizontalSpan = span;
        gridData_7.horizontalAlignment = GridData.END;
        updateButton.setLayoutData(gridData_7);
        updateButton.setText("Send New Request");
        updateButton.addSelectionListener(this);

        mainSC.setContent(mainGroup);
        mainSC.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    public OptionChooser createOptionChooser(Composite parent){
        return new DataProcessOptionChooser(parent);
    }

    public void setDataItem(DataItem item) {
        dataItem = item;
        mainGroup.setText(item.getName());
        enabledButton.setData(dataItem);
        enabledButton.setSelection(dataItem.isEnabled());
        DataProvider prov = item.getDataProvider();
        if (prov == null)
            return;
        // Provider should be a SensorMLProvider
        if (!(prov instanceof SMLProvider)) {
            System.err.println("SWE Provider not yet supported");
            return;
        }

        setProvider((SMLProvider) prov);
    }
    
    /**
     * Make this DataProvider the currently active Provider
     * 
     * @param newProv
     */
    public void setProvider(SMLProvider newProv){       
        DataProcess process = ((SMLProvider)newProv).getProcess();
        //  The provider needs to be propagated down so updateData() can be called
        //  when options are changed
        ((DataProcessOptionChooser)optionChooser).setProvider(newProv);
        processAL.clear();
        if(process instanceof ProcessChain){
            //  Hack for now to test WMS Options
            List procList = ((ProcessChain)process).getProcessList();
            for(int i=0; i<procList.size(); i++){
                DataProcess nextProcess = (DataProcess)procList.get(i);
                //System.err.println("NextProc is " + nextProcess.getType());
                processAL.add(nextProcess);
            }
        } else {
            processAL.add(process);
        }

        //  Display first process options initially
        optionChooser.buildControls(processAL.get(0));
        //  Change listViewer contents
        listViewer.setInput(processAL); 
    }

    //  Selecting label causes ONLY selChanged event
    public void selectionChanged(SelectionChangedEvent e) {
        System.err.println("sel source is" + e.getSource());
        StructuredSelection selection = (StructuredSelection)e.getSelection();
        DataProcess proc = (DataProcess)selection.getFirstElement();
        //  Check for empty selection (happens when buildControls() is called)
        if(proc == null) {
            Iterator it = processAL.iterator();
            if(!it.hasNext()){
                //  stylerSet is currently empty
                optionChooser.removeOldControls();
                return;
            }
            //  Reset selected to first in Table
//          checkboxTableViewer.getTable().setSelection(0);         
//          selection = (StructuredSelection)checkboxTableViewer.getSelection();
//          proc = (DataProcess)selection.getFirstElement();
        }
        //  Check to see if selected Styler has really changed
        if(proc == activeProcess){
            System.err.println("Selection not really changed");
            return;
        }
        System.err.println("Selection CHANGED");
        activeProcess = proc;
        optionChooser.buildControls(proc);
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        // TODO Auto-generated method stub
        Control control = (Control)e.getSource();
        if (control == enabledButton){
            dataItem.setEnabled(enabledButton.getSelection());
        } else if (control == updateButton){
            try {
                DataProvider provider = dataItem.getDataProvider();
                //  NEED TO READ w/h fields and reset them here before requesting data!!!
                // 
                dataItem.getDataProvider().updateData();
            } catch (DataException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    
    public void close(){
        //  TODO: dispose of any resources
    }   
}

class ProcessContentProvider implements IStructuredContentProvider
{
    DataProcess[] process;
    
    public Object [] getElements (Object inputElement){
        ArrayList<DataProcess> procs = (ArrayList<DataProcess>)inputElement;
        DataProcess [] procArr = procs.toArray(new DataProcess[]{});
        return procArr;
    }
        
    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        //System.err.println("Input changed is " + );
    }
}

class ProcessLabelProvider extends LabelProvider {

    public Image getImage(Object element) {
        return null;
    }

    public String getText(Object element) {
        if(element == null)
            System.err.println("???");
        DataProcess proc = (DataProcess)element;
        return proc.getName();
    }
}
