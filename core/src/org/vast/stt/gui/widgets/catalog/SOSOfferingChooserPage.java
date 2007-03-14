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

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sos.SOSLayerCapabilities;

/**
 * <p>
 * <b>Title:</b> TODO: Add Title
 * </p>
 * 
 * <p>
 * <b>Description:</b><br/> TODO: Add Description
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
{
	Table table;
	SOSLayerCapabilities caps;
	
	public SOSOfferingChooserPage(SOSLayerCapabilities caps){
		super("SOS Offering Chooser");
		this.caps = caps;
		setTitle("Observation Offering Chooser");
		setDescription("Select one or more offerings you wish to import");
	}
	
	public void createControl(Composite parent){
		 CheckboxTableViewer checkboxTableViewer = 
			 CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.SINGLE);

		 setControl(checkboxTableViewer.getTable());
		 checkboxTableViewer.setLabelProvider(new OfferingLabelProvider());
		 checkboxTableViewer.setContentProvider(new OfferingContentProvider());
		 
		 List<String> procs = caps.getProcedureList();
		 checkboxTableViewer.setInput(procs);
	}
	
	public void createControl(Composite parent, int shit) {
	    table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | 
	                      SWT.V_SCROLL | SWT.H_SCROLL | SWT.CHECK);
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    TableColumn tc = new TableColumn(table, SWT.LEFT);
	    tc.setText("Offering ID");
	    tc.setResizable(true);
	    
	    tc.setWidth(100);
	    List<String> procs = caps.getProcedureList();
	    String [] items = procs.toArray(new String[]{});
	    addTableContents(items);
	    setControl(table);
	}
	
	protected void addTableContents(String[] items) {
	    for (int i = 0; i < items.length; i++) {
	        TableItem ti = new TableItem(table, SWT.NONE);
	        ti.setText(items[i]);
	    }
	}
	
		public boolean canFlipToNextPage()
		{
// if (getErrorMessage() != null) return false;
// if (isTextNonEmpty(fromText)
// && isTextNonEmpty(toText) &&
// (planeButton.getSelection() || carButton.getSelection())
// && isReturnDateSet())
// return true;
			return false;
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

class OfferingLabelProvider extends LabelProvider
{

    public Image getImage(Object element)
    {
        return null;
    }


    public String getText(Object element)
    {
        return element.toString();
    }
}

