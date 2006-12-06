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

package org.vast.stt.project.scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.AbstractDisplay;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataTree;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.renderer.SceneRenderer;


/**
 * <p><b>Title:</b><br/>
 * Scene Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current state of a scene (graphic view)
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public abstract class Scene<ItemType extends SceneItem<?>> extends AbstractDisplay
{
	protected DataTree dataTree;
    protected SceneRenderer<Scene<ItemType>> renderer; 
    protected ArrayList<ItemType> sceneItems;
    protected ArrayList<ItemType> selectedItems;
    

    public Scene()
    {
        sceneItems = new ArrayList<ItemType>();
        selectedItems = new ArrayList<ItemType>(1);
    }
    
    
    protected abstract ItemType createNewItem();


	public DataTree getDataTree()
    {
        return dataTree;
    }


    public void setDataTree(DataTree dataTree)
    {
        if (this.dataTree != dataTree)
        {
            if (this.dataTree != null)
                this.dataTree.removeListener(this);
            
            this.dataTree = dataTree;
            
            if (this.dataTree != null)
                this.dataTree.addListener(this);
        }
    }
    
    
    public SceneRenderer<Scene<ItemType>> getRenderer()
    {
        return renderer;
    }


    public void setRenderer(SceneRenderer<Scene<ItemType>> renderer)
    {
        this.renderer = renderer;
    }
    
    
    public List<ItemType> getSceneItems()
    {
        return this.sceneItems;
    }
    
    
    public List<ItemType> getSelectedItems()
    {
        return selectedItems;
    }
    
    
    /**
     * Finds the SceneItem object associated with this dataItem
     * @param dataItem
     * @return
     */
    public ItemType findItem(DataItem dataItem)
    {
        // try to find entry in sceneItems list
        for (int i=0; i<sceneItems.size(); i++)
        {
            ItemType nextItem = sceneItems.get(i);
            if (nextItem.getDataItem() == dataItem)
                return nextItem;
        }
        
        return null;
    }
    
    
    /**
     * Removes an item from the scene and cleanup all cached data
     * @param dataItem
     */
    public void removeItem(DataFolder folder, DataItem dataItem)
    {
        folder.remove(dataItem);
        SceneItem<?> sceneItem = findItem(dataItem);        
        
        // if SceneItem was created, cleanup as well
        if (sceneItem != null)
        {
            dataItem.removeListener(sceneItem);
            sceneItems.remove(sceneItem);
            sceneItem.cleanup();
        }
    }
    
    
    /**
     * Sets item visibility to the specified value
     * Also creates a SceneItem if not created yet
     * @param dataItem
     * @param visible
     */
    public void setItemVisibility(DataItem dataItem, boolean visible)
    {       
        SceneItem sceneItem = findItem(dataItem);
        
        if (sceneItem != null)
        {
            sceneItem.setVisible(visible);
        }
        else
        {
            // if not found create a new SceneItem
            ItemType newSceneItem = createNewItem();
            newSceneItem.setDataItem(dataItem);
            newSceneItem.setVisible(visible);
            
            // prepare all stylers
            List<Symbolizer> symbolizers = dataItem.getSymbolizers();
            for (int i=0; i<symbolizers.size(); i++)
                newSceneItem.updateSymbolizer(symbolizers.get(i));
            
            // add new scene items to rendering list
            sceneItems.add(newSceneItem);
        }
    }
    
    
    /**
     * Sets folder visibility recursively
     * @param folder
     * @param visible
     */
    public void setItemVisibility(DataFolder folder, boolean visible)
    {
        Iterator<DataItem> it = folder.getItemIterator();        
        while (it.hasNext())
        {
            DataItem nextItem = it.next();
            setItemVisibility(nextItem, visible);
        }
    }
    
    
    /**
     * Determine item visibility
     * @param dataItem
     * @return true if item is visible, false otherwise
     */
    public boolean isItemVisible(DataItem dataItem)
    {
        for (int i=0; i<sceneItems.size(); i++)
        {
            SceneItem<?> nextItem = sceneItems.get(i);
            if (nextItem.getDataItem() == dataItem && nextItem.isVisible())
                return true;
        }
        
        return false;
    }
    
    
    /**
     * Determine folder visibility
     * @param folder
     * @return true if at least one subitem is visible, false otherwise
     */
    public boolean isItemVisible(DataFolder folder)
    {
        Iterator<DataItem> it = folder.getItemIterator();
        while (it.hasNext())
        {
            DataItem nextItem = it.next();
            if (isItemVisible(nextItem))
                return true;
        }
        return false;
    }
    
    
    /**
     * Computes scene bounding box in world coordinate and return it
     * @return
     */
    public STTSpatialExtent getBoundingBox()
    {
        STTSpatialExtent bbox = new STTSpatialExtent();
        
        // compute smallest bbox containing all children bbox
        for (int i = 0; i < sceneItems.size(); i++)
        {
            SceneItem<?> nextItem = sceneItems.get(i);
            
            if (!nextItem.isVisible())
                continue;
            
            STTSpatialExtent nextBox = nextItem.getBoundingBox();
            bbox.add(nextBox);
        }
        
        return bbox;
    }
    

    public void handleEvent(STTEvent event)
    {
        switch (event.type)
        {
            case SCENE_VIEW_CHANGED:
                dispatchEvent(event.copy());
                break;
                
            case SCENE_TIME_CHANGED:
                dispatchEvent(event.copy());
                break;
        }
    }
}
