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
import org.vast.ows.sas.SASLayerCapabilities;

/**
 * <p>
 * <b>Title:</b> SAS Subscription ChooserPage
 * </p>
 * 
 * <p>
 * <b>Description:</b><br/> 
 *    
 * </p>
 * 
 * <p>
 * Copyright (c) 2007
 * </p>
 * 
 * @author Tony Cook
 * @date Mar 14, 2007
 * @version 1.0
 */

public class SASSubscriptionChooserPage extends WizardPage 
	implements SelectionListener, ICheckStateListener
{
	Table table;
	SASLayerCapabilities caps;
	private Button selBtn;
	private Button deselBtn;
	private CheckboxTableViewer checkboxTableViewer;
	
	public SASSubscriptionChooserPage(SASLayerCapabilities caps){
		super("SAS Subscription Chooser");
		this.caps = caps;
		setTitle("Subscription Offering Chooser");
		setDescription("Select one or more offerings you wish to subscribe to");
	}
	
	public void createControl(Composite parent){
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		comp.setLayout(layout);
		
		checkboxTableViewer = CheckboxTableViewer
						.newCheckList(comp, SWT.BORDER | SWT.MULTI);
		checkboxTableViewer.setLabelProvider(new LabelProvider());
		checkboxTableViewer.setContentProvider(new SubscriptionContentProvider());
		String procs = caps.getSubscriptionOfferingID();
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
		SASMappingPage mappingPage = ((AddSASItemWizard)this.getWizard()).mappingPage;
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

class SubscriptionContentProvider implements IStructuredContentProvider
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


