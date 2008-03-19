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
import org.vast.stt.project.scene.SceneItem;
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
 * <p>Copyright (c) 2007</p>
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
    public void refreshView()
    {
        if (this.item != null)
            updateView();
        else
            clearView();
    }
    
    
    public void refreshViewAsync()
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
        // listen to selection events from both tree views
        getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);
        getSite().getPage().addPostSelectionListener(SceneItemsView.ID, this);
        
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
		if (part != null)
		{
		    // handle case of null selection
            if (selection == null)
            {
                item = null;
                clearView();
            }
            
            Object selectedObj = ((IStructuredSelection)selection).getFirstElement();
			if (selectedObj instanceof DataItem)
            {
			    DataItem selectedItem = (DataItem)selectedObj;
                setDataItem(selectedItem);
                refreshView();
            }
			else if (selectedObj instanceof SceneItem)
            {
                DataItem selectedItem = ((SceneItem)selectedObj).getDataItem();
                setDataItem(selectedItem);
                refreshView();
            }
            else
            {
                item = null;
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