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

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.symbolizer.SymbolizerOptionChooser;
import org.vast.stt.style.SymbolizerFactory;
import org.vast.stt.style.SymbolizerFactory.SymbolizerType;

/**
 * <p><b>Title:</b>
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Mar 21, 2007
 * @version 1.0
 */

public class SOSSymbolizerPage extends WizardPage 
	implements IStructuredContentProvider, ICheckStateListener 
{
	String [] symTypes;
	private CheckboxTableViewer checkboxTableViewer;
	
	public SOSSymbolizerPage(){
		super("Graphic Type Selection");
		setTitle("Select Graphic Types");
		setDescription("Select Graphic Types for rendering Offerings");
		symTypes = SymbolizerFactory.getSymbolizerTypes();
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


