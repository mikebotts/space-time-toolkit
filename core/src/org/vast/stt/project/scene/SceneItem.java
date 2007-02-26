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
import java.util.Hashtable;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.Projection;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.renderer.SceneRenderer;
import org.vast.stt.renderer.SceneRenderer.CleanupSection;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.StylerFactory;


/**
 * <p><b>Title:</b>
 * Scene Item
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Scene entry carrying a DataItem and its stylers
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 12, 2006
 * @version 1.0
 */
public class SceneItem implements STTEventListener
{
    protected Scene<? extends SceneRenderer> parentScene;
    protected DataItem dataItem;
    protected ArrayList<DataStyler> stylers;
    protected Hashtable<Symbolizer, DataStyler> stylerTable;
    protected boolean visible;


    public SceneItem(Scene<? extends SceneRenderer> scene)
    {
        stylers = new ArrayList<DataStyler>(1);
        stylerTable = new Hashtable<Symbolizer, DataStyler>();
        parentScene = scene;
    }
        
    
    public DataItem getDataItem()
    {
        return dataItem;
    }


    public void setDataItem(DataItem dataItem)
    {
        if (this.dataItem != dataItem)
        {
            if (this.dataItem != null)
                this.dataItem.removeListener(this);
            
            this.dataItem = dataItem;
            
            if (visible)
                this.dataItem.addListener(this);
        }
    }
    
    
    public String getName()
    {
        return dataItem.getName();
    }


    public void setName(String name)
    {
        dataItem.setName(name);
    }


    public boolean isVisible()
    {
        return visible;
    }


    public void setVisible(boolean visible)
    {
        if (this.dataItem != null)
        {
            if (visible)
                this.dataItem.addListener(this);
            else
                this.dataItem.removeListener(this);
            
            // TODO check if dataItem is not visible in another view!
            this.dataItem.setEnabled(visible);
        }
        
        this.visible = visible;
    }


    public ArrayList<DataStyler> getStylers()
    {
        return stylers;
    }
    
    
    /**
     * Computes item bounding box in world coordinate and return it
     * @return
     */
    public STTSpatialExtent getBoundingBox()
    {
        STTSpatialExtent bbox = new STTSpatialExtent();
        
        // compute smallest bbox containing all children bbox
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler nextStyler = stylers.get(i);
            
            if (!nextStyler.getSymbolizer().isEnabled())
                continue;
            
            STTSpatialExtent nextBox = nextStyler.getBoundingBox();
            bbox.add(nextBox);
        }
        
        return bbox;
    }
    
    
    /**
     * Changes projections of all stylers (for map views)
     * @param projection
     */
    public void setProjection(Projection projection)
    {
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler nextStyler = stylers.get(i);
            parentScene.getRenderer().cleanup(stylers.get(i), CleanupSection.GEOMETRY);
            nextStyler.setProjection(projection);
        }
    }
    
    
    /**
     * Clears all rendering cache related to this item
     */
    public void cleanup()
    {
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler styler = stylers.get(i);
            parentScene.getRenderer().cleanup(styler, CleanupSection.ALL);
            styler.resetBoundingBox();
        }
    }
    
    
    /**
     * Clears all rendering cache associated to the given array of objects
     */
    public void cleanup(Object[] deletedObjects)
    {
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler styler = stylers.get(i);
            parentScene.getRenderer().cleanup(styler, deletedObjects, CleanupSection.ALL);
            styler.resetBoundingBox();
        }
    }
    
    
    /**
     * Resets bounding box of all stylers
     */
    protected void resetBoundingBox()
    {
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler styler = stylers.get(i);
            styler.resetBoundingBox();
        }
    }
    
    
    /**
     * Update this symbolizer after a change/addition/deletion
     * For deletion, styler must first be disabled.
     * @param sym
     */
    protected void updateSymbolizer(Symbolizer sym)
    {
        // try to find corresponding styler
        DataStyler styler = stylerTable.get(sym);
        
        // create new one if not found
        if (styler == null)
        {
            if (!sym.isEnabled())
                return;
            
            styler = StylerFactory.createStyler(sym);
            styler.setDataItem(dataItem);
            parentScene.prepareStyler(styler);
            stylers.add(styler);
            stylerTable.put(sym, styler);
        }
        
        // otherwise update existing one
        else
        {
            parentScene.getRenderer().cleanup(styler, CleanupSection.ALL);
            
            if (sym.isEnabled())
            {
                styler.updateDataMappings();
            }
            else
            {
                // completely remove if disabled
                stylers.remove(styler);
                stylerTable.remove(sym);
            }                
        }
        
        styler.resetBoundingBox();
    }


    public void handleEvent(STTEvent event)
    {
        switch (event.type)
        {
            case ITEM_SYMBOLIZER_CHANGED:
                Symbolizer symbolizer = (Symbolizer)event.source;
                updateSymbolizer(symbolizer);
                break;
                            
            case PROVIDER_DATA_CLEARED:
                cleanup();
                break;
                
            case PROVIDER_DATA_REMOVED:
                Object[] deletedItems = (Object[])event.source;
                cleanup(deletedItems);
                break;
                
            case PROVIDER_DATA_CHANGED:
                resetBoundingBox();
                break;
        }
        
        if (visible)
            parentScene.dispatchEvent(new STTEvent(this, EventType.SCENE_ITEM_CHANGED));
    }

}
