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
import org.vast.stt.event.EventType;
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
    
    
    public void setItemVisibility(DataItem dataItem, boolean visible)
    {
        boolean found = false;
        
        // try to find entry in sceneItems list
        for (int i=0; i<sceneItems.size(); i++)
        {
            SceneItem nextItem = sceneItems.get(i);
            if (nextItem.getDataItem() == dataItem)
            {
                nextItem.setVisible(visible);
                found = true;
            }
        }
        
        // if not found create a new SceneItem + prepare all stylers
        if (!found)
        {
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
        
        // register scene as a listener to the item
        if (visible)
            dataItem.addListener(this);
        else
            dataItem.removeListener(this);
    }
    
    
    public void setItemVisibility(DataFolder folder, boolean visible)
    {
        Iterator<DataItem> it = folder.getItemIterator();        
        while (it.hasNext())
            setItemVisibility(it.next(), visible);
    }
    
    
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
    
    
    public boolean isItemVisible(DataFolder folder)
    {
        Iterator<DataItem> it = folder.getItemIterator();
        while (it.hasNext())
            if (isItemVisible(it.next()))
                return true;
        return false;
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
        // case of data removed from a DataNode
        if (event.type == EventType.PROVIDER_DATA_REMOVED)
        {
            
        }
        
        // if dataItem event, forward only if item is visible
        else if (event.producer instanceof DataItem)
        {
            DataItem srcItem = (DataItem)event.producer;
            if (isItemVisible(srcItem))
                dispatchEvent(event.copy());
        }
        else
        {
            // simply forward the event to scene listeners
            dispatchEvent(event.copy());
        }
    }
}
