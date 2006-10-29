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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.vast.stt.commands.FitView;
import org.vast.stt.commands.OpenView;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.catalog.LayerTransfer;
import org.vast.stt.gui.widgets.catalog.SceneTreeDropListener;
import org.vast.stt.project.table.TableItem;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.tree.WorldItem;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.SceneItem;


public class SceneTreeView extends SceneView implements ISelectionChangedListener, IDoubleClickListener
{
	public static final String ID = "STT.SceneTreeView";
	private TreeViewer sceneTree;
	private Image itemVisImg, itemHidImg, itemErrImg, folderVisImg, folderHidImg, tableImg;
    private ImageDescriptor fitSceneImg, fitItemImg;
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
                if (scene.isItemVisible((DataFolder)element))
                    return folderVisImg;
                else
                    return folderHidImg;
			}		
			else if (element instanceof DataItem)
            {
                DataItem item = (DataItem)element;
                
                if (item.getDataProvider() == null || item.getDataProvider().hasError())
                    return itemErrImg;
                
                if (element instanceof WorldItem)
                {
                    if (scene.isItemVisible((WorldItem)item))
                        return itemVisImg;
                    else
                        return itemHidImg;
                }
                else if (element instanceof TableItem)
                {
                    return tableImg;
                }
                else
                    return itemVisImg;
            }            
            else
				return null;
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
		//  add SceneTreeView as a LayerTree Drop listener
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
	    Transfer [] dropfers = new Transfer[] { LayerTransfer.getInstance()};
	    sceneTree.addDropSupport(ops, dropfers, new SceneTreeDropListener(sceneTree));
        sceneTree.addSelectionChangedListener(this);

		getSite().setSelectionProvider(sceneTree);
        super.createPartControl(parent);
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
        // load tree images
		ImageDescriptor descriptor;
		descriptor = STTPlugin.getImageDescriptor("icons/itemVis.gif");
		itemVisImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/itemHid.gif");
        itemHidImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/itemErr.gif");
        itemErrImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/folderVis.gif");
        folderVisImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/folderHid.gif");
        folderHidImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/table.gif");
        tableImg = descriptor.createImage();
        
        // load menu images
        fitSceneImg = STTPlugin.getImageDescriptor("icons/fitScene.gif");
        fitItemImg = STTPlugin.getImageDescriptor("icons/fitItem.gif");
        
        // load tree font
        treeFont = new Font (PlatformUI.getWorkbench().getDisplay(), "Tahoma", 7, SWT.NORMAL);
        
        // add fit to scene action to toolbar
        IAction Fit2SceneAction = new Action()
        {
            public void run()
            {
                if (scene != null)
                {
                    FitView cmd = new FitView(scene);
                    cmd.execute();
                }
            }
        };
        Fit2SceneAction.setImageDescriptor(fitSceneImg);
        Fit2SceneAction.setToolTipText("Fit View To Scene");
        site.getActionBars().getToolBarManager().add(Fit2SceneAction);
		
		// add fit to item action to toolbar
        IAction Fit2ItemAction = new Action()
        {
            public void run()
            {
                if (scene != null)
                {
                    ISelection selection = sceneTree.getSelection();
                    DataEntry selectedEntry = (DataEntry)((IStructuredSelection)selection).getFirstElement();
                    if (selectedEntry instanceof DataItem)
                    {
                        SceneItem sceneItem = scene.findItem((WorldItem)selectedEntry);
                        if (sceneItem != null && sceneItem.isVisible())
                        {
                            FitView cmd = new FitView(scene, sceneItem);
                            cmd.execute();
                        }
                    }
                }
            }
        };
        Fit2ItemAction.setImageDescriptor(fitItemImg);
        Fit2ItemAction.setToolTipText("Fit View To Item");
        site.getActionBars().getToolBarManager().add(Fit2ItemAction);
	}
	
	
	@Override
	public void dispose()
	{
        itemVisImg.dispose();
        itemHidImg.dispose();
        itemErrImg.dispose();
        folderVisImg.dispose();
        folderHidImg.dispose();
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
        switch (e.type)
        {
            case SCENE_OPTIONS_CHANGED:            
            case SCENE_TREE_CHANGED:
            case ITEM_VISIBILITY_CHANGED:
                refreshViewAsync();
                break;
        }
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
    
    
    public void selectionChanged(SelectionChangedEvent event)
    {
        ISelection selection = event.getSelection();
        scene.getSelectedItems().clear();
        
        // handle case of null selection
        if (selection == null)
            return;
        
        Iterator iterator = ((IStructuredSelection)selection).iterator();
        while (iterator.hasNext())
        {
            DataEntry selectedEntry = (DataEntry)iterator.next();
            if (selectedEntry instanceof WorldItem)
            {
                SceneItem item = scene.findItem((WorldItem)selectedEntry);
                if (item != null && item.isVisible())
                    scene.getSelectedItems().add(item);
            }
        }
    }


    public void doubleClick(DoubleClickEvent event)
    {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        DataEntry selectedEntry = (DataEntry)selection.getFirstElement();
        
        // if it's a list change visibility for all descendants
        if (selectedEntry instanceof DataFolder)
        {
            DataFolder folder = (DataFolder)selectedEntry;
            boolean visibility = scene.isItemVisible(folder);
            scene.setItemVisibility(folder, !visibility);
        }
        
        // if it's a single item, change its visibility
        else if (selectedEntry instanceof WorldItem)
        {
            DataItem item = (WorldItem)selectedEntry;
            boolean visible = scene.isItemVisible((WorldItem)item);
            scene.setItemVisibility((WorldItem)item, !visible);
        }
        
        // if it's a DataTable, open TableView
        else if (selectedEntry instanceof TableItem)
        {
            TableItem table = (TableItem)selectedEntry;
            OpenView openView = new OpenView();
            openView.setViewID(TableView.ID);
            openView.setData(table);
            openView.execute();
        }
        
        updateView();
        scene.dispatchEvent(new STTEvent(this, EventType.ITEM_VISIBILITY_CHANGED));
    }
}