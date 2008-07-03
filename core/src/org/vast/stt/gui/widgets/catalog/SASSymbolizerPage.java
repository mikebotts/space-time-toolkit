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

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.style.SymbolizerFactory;

/**
 * <p><b>Title:</b>
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Mar 21, 2007
 * @version 1.0
 */

public class SASSymbolizerPage extends WizardPage 
	implements IStructuredContentProvider, ICheckStateListener 
{
	String [] symTypes;
	private CheckboxTableViewer checkboxTableViewer;
	
	public SASSymbolizerPage(){
		super("Graphic Type Selection");
		setTitle("Select Graphic Types");
		setDescription("Select Graphic Types for rendering Offerings");
		symTypes = SymbolizerFactory.getSymbolizerTypeNames();
	}
	
	public void createControl(Composite parent){
		//  Mapping GUI thingy
		checkboxTableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.MULTI);
		checkboxTableViewer.setLabelProvider(new LabelProvider());
		checkboxTableViewer.setContentProvider(this);
		checkboxTableViewer.setInput(symTypes);
		checkboxTableViewer.addCheckStateListener(this);
		
		setControl(checkboxTableViewer.getTable());
	}
	
	public String [] getSelectedSymbolizerTypes(){
		Object [] checked = checkboxTableViewer.getCheckedElements();
		//  convert to String []
		String [] selSymTypes = new String[checked.length];
		for(int i=0; i<checked.length; i++){
			selSymTypes[i] = (String)checked[i];
		}
		return selSymTypes;
	}
	
	public void checkStateChanged(CheckStateChangedEvent event) {
		Object [] checked = checkboxTableViewer.getCheckedElements();
		if(checked != null && checked.length > 0)
			((AddSOSItemWizard)this.getWizard()).setCanFinish(true);
		else
			((AddSOSItemWizard)this.getWizard()).setCanFinish(false);
		this.getWizard().getContainer().updateButtons();
	}
	
	public Object[] getElements(Object inputElement) {
		return (String []) inputElement;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}


