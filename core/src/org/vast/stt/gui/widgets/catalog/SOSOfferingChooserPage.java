/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.gui.widgets.catalog;

import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.vast.ows.sos.SOSLayerCapabilities;

/**
 * <p>
 * <b>Title:</b> SOSOfferingChooserPage
 * </p>
 * 
 * <p>
 * <b>Description:</b><br/> 
 *    
 * </p>
 * 
 * <p>
 * Copyright (c) 2006
 * </p>
 * 
 * @author Tony Cook
 * @date Mar 14, 2007
 * @version 1.0
 */

public class SOSOfferingChooserPage extends WizardPage 
	implements SelectionListener, ICheckStateListener
{
	Table table;
	SOSLayerCapabilities caps;
	private Button selBtn;
	private Button deselBtn;
	private CheckboxTableViewer checkboxTableViewer;
	
	public SOSOfferingChooserPage(SOSLayerCapabilities caps){
		super("SOS Offering Chooser");
		this.caps = caps;
		setTitle("Observation Offering Chooser");
		setDescription("Select one or more offerings you wish to import");
	}
	
	public void createControl(Composite parent){
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		comp.setLayout(layout);
		
		checkboxTableViewer = CheckboxTableViewer
						.newCheckList(comp, SWT.BORDER | SWT.MULTI);
		checkboxTableViewer.setLabelProvider(new LabelProvider());
		checkboxTableViewer.setContentProvider(new OfferingContentProvider());
		List<String> procs = caps.getObservableList();
		checkboxTableViewer.setInput(procs);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		checkboxTableViewer.getTable().setLayoutData(gd);
		checkboxTableViewer.addCheckStateListener(this);
		// sel/desel all buttons
		// NOTE that I had to put in a new composite to get buttons right aligned
		// (How do you rt align 2 btns without this extra step??? I give up...)
		Composite btnComp = new Composite(comp, 0x0);
		btnComp.setLayout(new GridLayout(2, false));
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		gd.horizontalSpan = 2;
		btnComp.setLayoutData(gd);
		selBtn = new Button(btnComp, SWT.PUSH);
		selBtn.setText("Select All");
		selBtn.addSelectionListener(this);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		selBtn.setLayoutData(gd);
		deselBtn = new Button(btnComp, SWT.PUSH);
		deselBtn.setText("Deselect All");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		deselBtn.setLayoutData(gd);
		deselBtn.addSelectionListener(this);
		
		setControl(comp);
	}
	
	protected void addTableContents(String[] items) {
	    for (int i = 0; i < items.length; i++) {
	        TableItem ti = new TableItem(table, SWT.NONE);
	        ti.setText(items[i]);
	    }
	}
	
	public boolean canFlipToNextPage() {
		Object [] checked = checkboxTableViewer.getCheckedElements();
		if(checked != null && checked.length > 0)
			return true;
		return false;
	}

	public IWizardPage getNextPage(){
		SOSMappingPage mappingPage = ((AddSOSItemWizard)this.getWizard()).mappingPage;
		String [] offerings = getSelectedOfferings();
		mappingPage.setOfferings(offerings);
		return mappingPage;
	}
	
	public String [] getSelectedOfferings(){
		Object [] offerings = checkboxTableViewer.getCheckedElements();
		String [] offStr = new String[offerings.length];
		for(int i=0; i<offerings.length; i++)
			offStr[i] = (String)offerings[i];
		return offStr;
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if(e.widget == selBtn){
			checkboxTableViewer.setAllChecked(true);
		} else if (e.widget == deselBtn) {
			checkboxTableViewer.setAllChecked(false);
		}
		this.getWizard().getContainer().updateButtons();
	}

	public void checkStateChanged(CheckStateChangedEvent event) {
		this.getWizard().getContainer().updateButtons();
	}
}

class OfferingContentProvider implements IStructuredContentProvider
{
    public Object[] getElements(Object inputElement)
    {
        List offerings = (List)inputElement;
        return offerings.toArray();
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }
}


