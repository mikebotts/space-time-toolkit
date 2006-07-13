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

import java.util.Iterator;

import org.eclipse.jface.action.Action;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.DataEntry;
import org.vast.stt.project.DataFolder;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.Scene;


public class SceneTreeView extends SceneView implements IDoubleClickListener
{
	public static final String ID = "STT.SceneTreeView";
	private TreeViewer sceneTree;
	private Image itemVisImg, itemHidImg, folderImg;
	private Font treeFont;
	private Object[] expandedItems;
    private ISelection selectedItem;
	
	
	// Label + Image provider
	class TreeLabelProvider extends LabelProvider
	{        
        @Override
		public Image getImage(Object element)
		{
			if (element instanceof DataFolder)
			{
				if (sceneTree.getExpandedState(element))
					return folderImg;
				
				return folderImg;
			}		
			else if (element instanceof DataItem)
            {
                if (scene.isItemVisible((DataItem)element))
                    return itemVisImg;
                else
                    return itemHidImg;
            }
            else
				return itemVisImg;
		}

		@Override
		public String getText(Object element)
		{
			return ((DataEntry)element).getName();
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
			if (parentElement instanceof DataFolder)
			{
				return ((DataFolder)parentElement).toArray();
			}
			return null;
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			if (element instanceof DataFolder)
				return true;
			else
				return false;
		}

		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Scene)
			{
				return new Object[] {((Scene)inputElement).getDataTree()};
			}
			return null;
		}		
	}


	@Override
	public void createPartControl(Composite parent)
	{
		TreeLabelProvider labelProvider = new TreeLabelProvider();
		TreeContentProvider contentProvider = new TreeContentProvider();
		sceneTree = new TreeViewer(parent, SWT.SINGLE);
		sceneTree.setLabelProvider(labelProvider);
		sceneTree.setContentProvider(contentProvider);
		sceneTree.addDoubleClickListener(this);
		getSite().setSelectionProvider(sceneTree);
        super.createPartControl(parent);
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
		ImageDescriptor descriptor;
		descriptor = STTPlugin.getImageDescriptor("icons/itemVisible.gif");
		itemVisImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/itemNotVisible.gif");
        itemHidImg = descriptor.createImage();
		descriptor = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		folderImg = descriptor.createImage();
		treeFont = new Font (PlatformUI.getWorkbench().getDisplay(), "Tahoma", 7, SWT.NORMAL);
		
		Action action = new Action()
		{
			public void run()
			{
				
			}
		};
		
		action.setText("Scene 001");
		site.getActionBars().getMenuManager().add(action);
	}
	
	
	@Override
	public void dispose()
	{
		itemVisImg.dispose();
		folderImg.dispose();
		treeFont.dispose();
        super.dispose();
	}
    
    
    @Override
    public void setScene(Scene sc)
    {
        super.setScene(sc);
        expandedItems = new Object[0];
    }
    
    
    @Override
    public void handleEvent(STTEvent e)
    {
        if (e.type == EventType.SCENE_DATA_CHANGED)
            super.handleEvent(e);
    }
    
    
    public void updateView()
    {       
        // save previous expanded/selected elements
        expandedItems = sceneTree.getExpandedElements();
        selectedItem = sceneTree.getSelection();
        
        // load new data in tree
        sceneTree.setInput(scene);
        
        // restore expanded/selected elements
        for (int i=0; i<expandedItems.length; i++)
            sceneTree.expandToLevel(expandedItems[i], 1);
        sceneTree.setSelection(selectedItem);
    }


    public void clearView()
    {
        sceneTree.setInput(null);
        expandedItems = new Object[0];
    }


    public void doubleClick(DoubleClickEvent event)
    {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        DataEntry selectedEntry = (DataEntry)selection.getFirstElement();
        
        // if it's a list change visibility for all descendants
        if (selectedEntry instanceof DataFolder)
        {
            DataFolder list = (DataFolder)selectedEntry;
            Iterator<DataItem> it = list.getItemIterator();
            boolean getVis = true;
            boolean visibility = true;
            
            while (it.hasNext())
            {
                DataItem nextItem = it.next();
                
                if (getVis)
                {
                    visibility = !scene.isItemVisible(nextItem);
                    getVis = false;
                }
                
                scene.setItemVisibility(nextItem, visibility);
            }
        }
        
        // if it's a single item, change its visibility
        else if (selectedEntry instanceof DataItem)
        {
            DataItem item = (DataItem)selectedEntry;
            boolean visible = scene.isItemVisible(item);
            scene.setItemVisibility(item, !visible);
        }
        
        updateView();
        scene.dispatchEvent(new STTEvent(this, EventType.SCENE_DATA_CHANGED));
    }  
}