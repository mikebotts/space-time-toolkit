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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.vast.stt.project.chart.ChartScene;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.table.TableScene;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;


public class SceneTreeView extends SceneView<WorldScene> implements ISelectionChangedListener, IDoubleClickListener
{
	public static final String ID = "STT.SceneTreeView";
	private TreeViewer sceneTree;
	private Image itemVisImg, itemHidImg, itemErrImg, folderVisImg, folderHidImg;
    private Image maskVisImg, maskHidImg, tableImg, chartImg, globeImg;
    private ImageDescriptor fitSceneImg, fitItemImg;
	private Font treeFont;
	private Object[] expandedItems;
    private ISelection selectedItem;
    private List<Scene> allScenes;
    	
    
	// Label + Image provider
	class TreeLabelProvider extends LabelProvider
	{        
        @Override
		public Image getImage(Object element)
		{
            Scene parentScene = findParentScene((DataEntry)element);
            
            if (element instanceof DataFolder)
			{
                if (parentScene.isItemVisible((DataFolder)element))
                    return folderVisImg;
                else
                    return folderHidImg;
			}		
			else if (element instanceof DataItem)
            {
                DataItem item = (DataItem)element;
                
                if (item.getDataProvider() == null || item.getDataProvider().hasError())
                    return itemErrImg;
                
                if (item.getOptions().get(DataEntry.MASK) != null)
                {
                    if (parentScene.isItemVisible(item))
                        return maskVisImg;
                    else
                        return maskHidImg;
                }
                else
                {
                    if (parentScene.isItemVisible(item))
                        return itemVisImg;
                    else
                        return itemHidImg;
                }
            }
            else if (element instanceof Scene)
            {
                if (element instanceof WorldScene)
                {
                    return globeImg;
                }
                else if (element instanceof ChartScene)
                {
                    return chartImg;
                }
                else if (element instanceof TableScene)
                {
                    return tableImg;
                } 
                else
                {
                    return folderVisImg;
                }
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
			if (parentElement instanceof Scene)
			{
				allScenes.add((Scene)parentElement);
                return (((Scene)parentElement).getDataTree()).toArray();
			}
            else if (parentElement instanceof DataFolder)
            {
                return ((DataFolder)parentElement).toArray();
            }
            else if (parentElement instanceof DataItem)
            {
                return (((DataItem)parentElement).getMasks()).toArray();
            }
            else
                return null;
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			if (element instanceof Scene)
				return true;
            else if (element instanceof DataFolder)
                return true;
            else if (element instanceof DataItem)
                return (((DataItem)element).hasMask());
			else
				return false;
		}

		public Object[] getElements(Object inputElement)
		{
			return new Object[] {scene};
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
        
        // listen to selection to update scene selection
        sceneTree.addSelectionChangedListener(this);
        
        // listen to part cycle events for enabling/disabling refresh when view is hidden
        getSite().getPage().addPartListener(this);
        
        // register as selection provider to send selection to other views
        getSite().setSelectionProvider(sceneTree);
        
		// add drop support for adding new items
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
	    Transfer [] dropfers = new Transfer[] { LayerTransfer.getInstance()};
	    sceneTree.addDropSupport(ops, dropfers, new SceneTreeDropListener(sceneTree));        
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
        allScenes = new ArrayList<Scene>(1);
        
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
        descriptor = STTPlugin.getImageDescriptor("icons/maskVis.gif");
        maskVisImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/maskHid.gif");
        maskHidImg = descriptor.createImage();        
        descriptor = STTPlugin.getImageDescriptor("icons/table.gif");
        tableImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/chart.gif");
        chartImg = descriptor.createImage();
        descriptor = STTPlugin.getImageDescriptor("icons/globe.gif");
        globeImg = descriptor.createImage();   
        
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
                        SceneItem sceneItem = scene.findItem((DataItem)selectedEntry);
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
        tableImg.dispose();
        chartImg.dispose();
		treeFont.dispose();
        super.dispose();
	}
    
    
    @Override
    protected void assignScene()
    {
        ScenePageInput pageInput = (ScenePageInput) getSite().getPage().getInput();
        if (pageInput != null)
            setScene((WorldScene)pageInput.getScene());
        else
            setScene(null);
    }
    
    
    @Override
    public void setScene(WorldScene sc)
    {
        super.setScene(sc);
        expandedItems = new Object[0];
        
        // refresh display
        refreshViewAsync();
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
        sceneTree.setInput(scene.getDataTree());
        
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
    
    
    protected Scene findParentScene(DataEntry dataEntry)
    {
        for (int i=0; i<allScenes.size(); i++)
        {
            Scene nextScene = allScenes.get(i);
            if (nextScene.getDataTree().containsRecursively(dataEntry))
                return nextScene;
        }
        
        return null;
    }
    
    
    public void selectionChanged(SelectionChangedEvent event)
    {
        ISelection selection = event.getSelection();
        
        // clear selected items in all scenes
        for (int i=0; i<allScenes.size(); i++)
            allScenes.get(i).getSelectedItems().clear();
        
        // if selection is not null
        if (selection != null)
        {        
            // iterate through selection items
            Iterator<?> iterator = ((IStructuredSelection)selection).iterator();
            while (iterator.hasNext())
            {
                DataEntry selectedEntry = (DataEntry)iterator.next();
                if (selectedEntry instanceof DataItem)
                {
                    Scene sc = findParentScene(selectedEntry);
                    SceneItem item = sc.findItem((DataItem)selectedEntry);
                    
                    // add scene item to selected list only if visible
                    if (item != null && item.isVisible())
                    	sc.getSelectedItems().add(item);                    
                }
            }
        }
        
        // send redraw events to all affected scenes
        for (int i=0; i<allScenes.size(); i++)
            allScenes.get(i).dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
    }


    public void doubleClick(DoubleClickEvent event)
    {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        DataEntry selectedEntry = (DataEntry)selection.getFirstElement();
        Scene parentScene = findParentScene(selectedEntry);
        
        // if it's a list change visibility for all descendants
        if (selectedEntry instanceof DataFolder)
        {
            DataFolder folder = (DataFolder)selectedEntry;
            boolean visibility = parentScene.isItemVisible(folder);
            parentScene.setItemVisibility(folder, !visibility);
            sceneTree.refresh(folder, true);
            parentScene.dispatchEvent(new STTEvent(this, EventType.ITEM_VISIBILITY_CHANGED));
        }
        
        // if it's a single item, change its visibility
        else if (selectedEntry instanceof DataItem)
        {
            DataItem item = (DataItem)selectedEntry;
            boolean visible = parentScene.isItemVisible(item);
            parentScene.setItemVisibility(item, !visible);
            sceneTree.refresh(item, true);
            sceneTree.setSelection(sceneTree.getSelection());
            parentScene.dispatchEvent(new STTEvent(this, EventType.ITEM_VISIBILITY_CHANGED));
        }
        
        // if it's a World, open WorldView
        else if (selectedEntry instanceof WorldScene)
        {
            WorldScene world = (WorldScene)selectedEntry;
            OpenView openView = new OpenView();
            openView.setViewID(WorldView.ID);
            openView.setData(world);
            openView.execute();
        }
        
        // if it's a Table, open TableView
        else if (selectedEntry instanceof TableScene)
        {
            TableScene table = (TableScene)selectedEntry;
            OpenView openView = new OpenView();
            openView.setViewID(TableView.ID);
            openView.setData(table);
            openView.execute();
        }
        
        // if it's a Chart, open ChartView
        else if (selectedEntry instanceof ChartScene)
        {
            ChartScene chart = (ChartScene)selectedEntry;
            OpenView openView = new OpenView();
            openView.setViewID(ChartView.ID);
            openView.setData(chart);
            openView.execute();
        }
    }
}