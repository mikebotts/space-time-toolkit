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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b>
 * Data Item View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for all DataItem Views.
 * This provides event handling and enforce the implementation of 
 * two other methods updateView() and clearView().
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public abstract class DataItemView extends ViewPart implements ISelectionListener, INullSelectionListener, STTEventListener
{
    protected DataItem item;
    protected Runnable runRefresh = new Runnable()
    {
        public void run() {refreshView();}
    };
	

    public abstract void updateView();
    public abstract void clearView();
    
    
    /**
     * Method called when the view needs to be refreshed 
     * (i.e. typically after a "CHANGE" event is received)
     */
    protected void refreshView()
    {
        if (this.item != null)
            updateView();
        else
            clearView();
    }
    
    
    protected void refreshViewAsync()
    {
        getSite().getShell().getDisplay().asyncExec(runRefresh);
    }
    
    
    public void setDataItem(DataItem dataItem)
    {
        if (item != dataItem)
        {
            if (item != null) {
                item.removeListener(this);
            }
            
            item = dataItem;
            
            if (item != null)
                item.addListener(this);
        }
    }
        
    
    @Override
    public void createPartControl(Composite parent)
    {
        getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);
        
        // trigger a selection event so we can update ourselves
        SceneTreeView treeView = (SceneTreeView)getSite().getPage().findView(SceneTreeView.ID);
        if (treeView != null)
        {
            ISelection selection = treeView.getSite().getSelectionProvider().getSelection();
            selectionChanged(treeView, selection);
        }
    }
    
    
    @Override
    public void setFocus()
    {
        
    }
    
    
    @Override
    public void dispose()
    {
        getSite().getPage().removePostSelectionListener(SceneTreeView.ID, this);
        
        if (item != null)
            item.removeListener(this);
        
        super.dispose();
    }
    
    
	/**
     * handle selection changes in scene tree
	 */
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if (part != null && (part instanceof SceneTreeView || part instanceof SceneItemsView))
		{
		    // handle case of null selection
            if (selection == null)
            {
                item = null;
                clearView();
            }
            
            DataEntry selectedEntry = (DataEntry)((IStructuredSelection)selection).getFirstElement();
			if (selectedEntry instanceof DataItem)
            {
			    DataItem selectedItem = (DataItem)selectedEntry;
                setDataItem(selectedItem);
                refreshView();
            }
            else
            {
                setDataItem(null);
                clearView();
            }
		}		
	}
    
    
    /**
     * handle data item events
     */
    public void handleEvent(STTEvent e)
    {       
        refreshViewAsync();
    }
}