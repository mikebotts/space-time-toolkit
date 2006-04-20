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

import java.util.*;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IPartListener;
import org.vast.stt.apps.STTConfig;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataEntryList;
import org.vast.stt.scene.Scene;


public class SceneTreeView extends ViewPart implements IPartListener
{
	public static final String ID = "STT.SceneTreeView";
	private TreeViewer sceneTree;
	private Image itemImg, folderImg;
	private Font treeFont;
	private Object[] expandedItems;
	
	
	// Label + Image provider
	class TreeLabelProvider extends LabelProvider
	{
		@Override
		public Image getImage(Object element)
		{
			if (element instanceof DataEntryList)
			{
				if (sceneTree.getExpandedState(element))
					return folderImg;
				
				return folderImg;
			}		
			else
				return itemImg;
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
			if (parentElement instanceof DataEntryList)
			{
				return ((DataEntryList)parentElement).toArray();
			}
			return null;
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			if (element instanceof DataEntryList)
				return true;
			else
				return false;
		}

		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Scene)
			{
				return new Object[] {((Scene)inputElement).getDataList()};
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
		//sceneTree.getTree().setFont(treeFont);
		getSite().setSelectionProvider(sceneTree);
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
		ImageDescriptor descriptor;
		descriptor = STTPlugin.getImageDescriptor("icons/item.gif");
		itemImg = descriptor.createImage();
		descriptor = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		//STTPlugin.getImageDescriptor("icons/group.gif");
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
		
		// register listener
		getSite().getPage().addPartListener(this);
	}
    
    
    public void clear()
    {
        sceneTree.setInput(null);
    }
    
    
    public void refresh()
    {
        if (sceneTree == null)
            return;
        
//        Runnable updateTree = new Runnable()
//        {
//            public void run()
//            {
                int sceneID = 0;
                ArrayList<Scene> sceneList = STTConfig.getInstance().getCurrentProject().getSceneList();
                Scene currentScene = sceneList.get(sceneID);
                
                // save previous expanded state
                expandedItems = sceneTree.getVisibleExpandedElements();
                
                // load new data
                sceneTree.setInput(currentScene);
                
                // restore expanded elements
                for (int i=0; i<expandedItems.length; i++)
                    sceneTree.expandToLevel(expandedItems[i], 1);
//            }
//        };
//        
//        sceneTree.getTree().getDisplay().asyncExec(updateTree);
    }
	
	
	public void partActivated(IWorkbenchPart part)
	{
        if (part instanceof SceneView)
            refresh();
	}
	
	
	@Override
	public void dispose()
	{
		getSite().getPage().removePartListener(this);
		itemImg.dispose();
		folderImg.dispose();
		treeFont.dispose();
	}
	
	
	@Override
	public void setFocus()
	{
	
	}


	public void partBroughtToTop(IWorkbenchPart part)
	{
	}


	public void partClosed(IWorkbenchPart part)
	{
	}


	public void partDeactivated(IWorkbenchPart part)
	{
	}


	public void partOpened(IWorkbenchPart part)
	{
	}


	public void widgetDefaultSelected(SelectionEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}