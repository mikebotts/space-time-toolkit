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
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.project.scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.AbstractDisplay;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataTree;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.renderer.SceneRenderer;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b><br/>
 * Scene Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current state of a scene (graphic view)
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public abstract class Scene extends AbstractDisplay
{
	protected DataTree dataTree;
    protected SceneRenderer<?> renderer;
    protected List<SceneItem> sceneItems;
    protected List<SceneItem> selectedItems;
    

    public Scene()
    {
        sceneItems = new ArrayList<SceneItem>();
        selectedItems = new ArrayList<SceneItem>(1);
    }
    
    
    protected abstract void prepareStyler(DataStyler styler);    


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
    
    
    public SceneRenderer<?> getRenderer()
    {
        return renderer;
    }
    
    
    public void setRenderer(SceneRenderer<?> renderer)
    {
        this.renderer = renderer;
    }
        
    
    public List<SceneItem> getSceneItems()
    {
        return this.sceneItems;
    }
    
    
    public List<SceneItem> getSelectedItems()
    {
        return selectedItems;
    }


	/**
     * Finds the SceneItem object associated with this dataItem
     * @param dataItem
     * @return
     */
    public SceneItem findItem(DataItem dataItem)
    {
        // try to find entry in sceneItems list
        for (int i=0; i<sceneItems.size(); i++)
        {
            SceneItem nextItem = sceneItems.get(i);
            if (nextItem.getDataItem() == dataItem)
                return nextItem;
            
            // also look in the masks list
            List<SceneItem> maskItems = nextItem.getMaskItems();
            for (int m=0; m<maskItems.size(); m++)
			{
			   SceneItem maskItem = maskItems.get(m);
			   if (maskItem.getDataItem() == dataItem)
			       return maskItem;
			}
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
        SceneItem sceneItem = findItem(dataItem);        
        
        // if SceneItem was created, cleanup as well
        if (sceneItem != null)
        {
            dataItem.removeListener(sceneItem);
            sceneItems.remove(sceneItem);
            sceneItem.cleanup();
        }
    }
    
    
    /**
     * Looks up for existing SceneItem in table or create a new one if needed
     * Item will be added to maskItems list if mask=true, otherwise to sceneItems list
     * @param dataItem
     * @param mask
     * @return
     */
    protected SceneItem enforceItem(DataItem dataItem)
    {
        SceneItem sceneItem = findItem(dataItem);
        if (sceneItem != null)
            return sceneItem;        
        
        // if not found create a new SceneItem
        SceneItem newSceneItem = new SceneItem(this);
        newSceneItem.setDataItem(dataItem);
        
        // prepare all stylers
        List<Symbolizer> symbolizers = dataItem.getSymbolizers();
        for (int i=0; i<symbolizers.size(); i++)
            newSceneItem.updateStyler(symbolizers.get(i));
        
        // add new scene items to rendering list (but not the masks!)
        if (dataItem.getOptions().get(DataEntry.MASK) == null)
            sceneItems.add(newSceneItem);
        
        // recursively call this to handle mask items
        List<DataItem> maskItems = dataItem.getMasks();
        for (int i=0; i<maskItems.size(); i++)
        {
            DataItem maskDataItem = maskItems.get(i);
            SceneItem maskItem = enforceItem(maskDataItem);
            newSceneItem.getMaskItems().add(maskItem);
            if (maskDataItem.isEnabled())
                maskItem.setVisible(true);
        }        
        
        return newSceneItem;
    }
    
    
    /**
     * Sets item visibility to the specified value
     * Also creates a SceneItem if not created yet
     * @param dataItem
     * @param visible
     */
    public void setItemVisibility(DataItem dataItem, boolean visible)
    {       
        SceneItem sceneItem;
        
        if (visible)
            sceneItem = enforceItem(dataItem);
        else
            sceneItem = findItem(dataItem);
        
        sceneItem.setVisible(visible);
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
        SceneItem item = findItem(dataItem);
        if (item != null && item.isVisible())
            return true;        
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
            SceneItem nextItem = sceneItems.get(i);
            
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
                
            case TIME_EXTENT_CHANGED:
                dispatchEvent(event.copy());
                break;
        }
    }
}
