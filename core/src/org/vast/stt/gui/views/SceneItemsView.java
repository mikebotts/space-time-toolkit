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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.STTDndTransfer;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.SceneItem;


public class SceneItemsView extends SceneView<Scene> implements IDoubleClickListener
{
	public static final String ID = "STT.SceneItemsView";
	private TreeViewer sceneTree;
	private Image itemVisImg, itemHidImg;
	private Font treeFont;
    private ISelection selectedItem;
	
	
	// Label + Image provider
	class TreeLabelProvider extends LabelProvider
	{        
        @Override
		public Image getImage(Object element)
		{
			SceneItem item = (SceneItem)element;
            if (item.isVisible())
                return itemVisImg;
            else
                return itemHidImg;
		}

		@Override
		public String getText(Object element)
		{
			return ((SceneItem)element).getName();
		}		
	}
	
	
	// Content provider
	class TreeContentProvider implements ITreeContentProvider
	{
		public void dispose()
		{						
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		public Object[] getChildren(Object parentElement)
		{
			return null;
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			return false;
		}

		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Scene)
			{
				return ((Scene)inputElement).getSceneItems().toArray();
			}
			return null;
		}		
	}


	@Override
	public void createPartControl(Composite parent)
	{
	    // setup tree viewer and its providers
        TreeLabelProvider labelProvider = new TreeLabelProvider();
        TreeContentProvider contentProvider = new TreeContentProvider();
        sceneTree = new TreeViewer(parent, SWT.SINGLE);
        sceneTree.setLabelProvider(labelProvider);
        sceneTree.setContentProvider(contentProvider);
        
        // listen to double clicks (toggle visibility)
        sceneTree.addDoubleClickListener(this);
        
        // listen to part cycle events for enabling/disabling refresh when view is hidden
        getSite().getPage().addPartListener(this);
        
        // register as selection provider to send selection to other views
        getSite().setSelectionProvider(sceneTree);
        
        // add drag & drop support for reordering items
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer [] dropfers = new Transfer[] { STTDndTransfer.getInstance()};        
        sceneTree.addDragSupport(0, dropfers, new SceneItemsDragListener(sceneTree));
        sceneTree.addDropSupport(ops, dropfers, new SceneItemsDropListener(sceneTree));        
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
		ImageDescriptor descriptor;
		descriptor = STTPlugin.getImageDescriptor("icons/itemVis.gif");
		itemVisImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/itemHid.gif");
        itemHidImg = descriptor.createImage();
        
		treeFont = new Font (PlatformUI.getWorkbench().getDisplay(), "Tahoma", 7, SWT.NORMAL);
	}
    
    
    @Override
    protected void assignScene()
    {
        ScenePageInput pageInput = (ScenePageInput) getSite().getPage().getInput();
        if (pageInput != null)
            setScene((Scene)pageInput.getScene());
        else
            setScene(null);
    }
	
	
	@Override
	public void dispose()
	{
        itemVisImg.dispose();
        itemHidImg.dispose();
		treeFont.dispose();
        super.dispose();
	}
    
    
    @Override
    public void handleEvent(STTEvent e)
    {       
        switch (e.type)
        {
            case SCENE_OPTIONS_CHANGED:            
            case ITEM_VISIBILITY_CHANGED:
                refreshViewAsync();
        }
    }
    
    
    public void updateView()
    {       
        // save previous expanded/selected elements
        selectedItem = sceneTree.getSelection();
        
        // load new data in tree
        sceneTree.setInput(scene);
        
        // restore selected elements
        sceneTree.setSelection(selectedItem);
    }


    public void clearView()
    {
        sceneTree.setInput(null);
    }


    public void doubleClick(DoubleClickEvent event)
    {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        SceneItem sceneItem = (SceneItem)selection.getFirstElement();        
        sceneItem.setVisible(!sceneItem.isVisible());        
        updateView();
        scene.dispatchEvent(new STTEvent(this, EventType.ITEM_VISIBILITY_CHANGED), false);
    }
}