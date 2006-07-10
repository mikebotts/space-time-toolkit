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

package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.project.DataEntry;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataProvider;


public class CustomizerView extends ViewPart implements ISelectionListener
{
	public static final String ID = "STT.CustomizerView";
	private TableViewer paramTable;
	
	
	class TableLabelProvider extends LabelProvider
	{

		@Override
		public Image getImage(Object element)
		{
			return null;
		}

		@Override
		public String getText(Object element)
		{
			return element.toString();
		}
		
	}
	
	
	class TableContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof DataItem)
			{
				DataItem item = (DataItem)inputElement;
				DataProvider prov = item.getDataProvider();
				if (prov != null)
				{
					return new Object[]{prov.getClass().getName()};
				}
			}
			
			return new Object[0];
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}		
	}
	

	public void createPartControl(Composite parent)
	{
		paramTable = new TableViewer(parent, SWT.NONE);
		TableLabelProvider labelProvider = new TableLabelProvider();
		TableContentProvider contentProvider = new TableContentProvider();
		paramTable.setLabelProvider(labelProvider);
		paramTable.setContentProvider(contentProvider);
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);
	}


	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if (part instanceof SceneTreeView)
		{
			DataEntry selectedItem = (DataEntry)((IStructuredSelection)selection).getFirstElement();
			paramTable.setInput(selectedItem);
		}		
	}
	
	
	@Override
	public void setFocus()
	{
		
	}


	@Override
	public void dispose()
	{
		getSite().getPage().removePostSelectionListener(this);
	}
}