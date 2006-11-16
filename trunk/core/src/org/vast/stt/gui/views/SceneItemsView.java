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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.WorldScene;


public class SceneItemsView extends SceneView implements IDoubleClickListener
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
			if (inputElement instanceof WorldScene)
			{
				return ((WorldScene)inputElement).getSceneItems().toArray();
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
        super.createPartControl(parent);
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
        scene.dispatchEvent(new STTEvent(this, EventType.ITEM_VISIBILITY_CHANGED));
    }  
}