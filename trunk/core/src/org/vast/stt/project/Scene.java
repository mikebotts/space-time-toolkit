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

package org.vast.stt.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;
import org.vast.stt.renderer.Renderer;
import org.vast.stt.renderer.opengl.JOGLRenderer;
import org.vast.stt.style.StylerFactory;


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
public class Scene implements STTEventProducer, STTEventListener
{
	protected String name;
    protected DataTree dataTree;
    protected Renderer renderer;
	protected ViewSettings viewSettings;
	protected TimeSettings timeSettings;
    protected STTEventListeners listeners;
    protected ArrayList<SceneItem> sceneItems;
    protected StylerFactory stylerFactory;


    public Scene()
    {
        renderer = new JOGLRenderer();
        renderer.setScene(this);
        stylerFactory = new StylerFactory();
        listeners = new STTEventListeners(2);
        sceneItems = new ArrayList<SceneItem>();
    }


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}
    

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


	public ViewSettings getViewSettings()
	{
		return viewSettings;
	}


	public void setViewSettings(ViewSettings viewSettings)
	{
        if (this.viewSettings != viewSettings)
        {
            if (this.viewSettings != null)
                this.viewSettings.removeListener(this);
            
            this.viewSettings = viewSettings;
            
            if (this.viewSettings != null)
                this.viewSettings.addListener(this);
        }
	}


	public TimeSettings getTimeSettings()
	{
		return timeSettings;
	}


	public void setTimeSettings(TimeSettings timeSettings)
	{
        if (this.timeSettings != timeSettings)
        {
            if (this.timeSettings != null)
                this.timeSettings.removeListener(this);
            
            this.timeSettings = timeSettings;
            
            if (this.timeSettings != null)
                this.timeSettings.addListener(this);
        }
	}
    
    
    public Renderer getRenderer()
    {
        return renderer;
    }


    public void setRenderer(Renderer renderer)
    {
        this.renderer = renderer;
    }
    
    
    public List<SceneItem> getSceneItems()
    {
        return this.sceneItems;
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
        }
        
        return null;
    }
    
    
    /**
     * Removes an item from the scene and cleanup all cached data
     * @param dataItem
     */
    public void removeItem(DataFolder folder, DataItem dataItem)
    {
        SceneItem sceneItem = findItem(dataItem);
        
        // cleanup SceneItem as well
        if (sceneItem != null)
        {
            //this.renderer.cleanup(RendererInfo)
        }
        
        folder.remove(dataItem);
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
            // if not found create a new SceneItem + prepare all stylers
            SceneItem newSceneItem = new SceneItem();
            newSceneItem.setDataItem(dataItem);
            newSceneItem.setVisible(visible);
            List<Symbolizer> symbolizers = dataItem.getSymbolizers();
            for (int i=0; i<symbolizers.size(); i++)
            {
                DataStyler styler = stylerFactory.createStyler(symbolizers.get(i));
                styler.setDataItem(dataItem);
                newSceneItem.getStylers().add(styler);
            }
            sceneItems.add(newSceneItem);
        }
        
        // register/unregister scene as a listener to the item
        if (visible)
            dataItem.addListener(this);
        else
            dataItem.removeListener(this);
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
            setItemVisibility(it.next(), visible);
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
            SceneItem nextItem = sceneItems.get(i);
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
            if (isItemVisible(it.next()))
                return true;
        return false;
    }
    
    
    /**
     * Computes scene bounding box in world coordinate and return it
     * @return
     */
    public SpatialExtent getBoundingBox()
    {
        SpatialExtent bbox = null;
        
        // compute smallest bbox containing all children bbox
        for (int i = 0; i < sceneItems.size(); i++)
        {
            SceneItem nextItem = sceneItems.get(i);
            
            if (!nextItem.isVisible())
                continue;
            
            SpatialExtent childBox = nextItem.getBoundingBox();
            
            if (i == 0)
                bbox = childBox.copy();
            else
                bbox.add(childBox);
        }
        
        return bbox;
    }


    public void addListener(STTEventListener listener)
    {
        listeners.add(listener);
    }


    public void removeListener(STTEventListener listener)
    {
        listeners.remove(listener);     
    }


    public void removeAllListeners()
    {
        listeners.clear();        
    }


    public void dispatchEvent(STTEvent event)
    {
        event.producer = this;
        listeners.dispatchEvent(event);
    }
    

    public void handleEvent(STTEvent event)
    {
        switch (event.type)
        {
            case ITEM_STYLE_CHANGED:
            case PROVIDER_DATA_CHANGED:
            case PROVIDER_ERROR:            
                DataItem srcItem = (DataItem)event.producer;
                if (isItemVisible(srcItem))
                    dispatchEvent(event.copy());
                break;
                
            case SCENE_VIEW_CHANGED:
                dispatchEvent(event.copy());
                break;
        }
    }
}
