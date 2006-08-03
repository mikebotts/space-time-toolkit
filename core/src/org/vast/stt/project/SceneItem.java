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

import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;


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
    protected Scene parentScene;
    protected DataItem dataItem;
    protected DataStylerList stylers;
    protected boolean visible;


    public SceneItem(Scene scene)
    {
        stylers = new DataStylerList();
        parentScene = scene;
    }
    
    
    public DataItem getDataItem()
    {
        return dataItem;
    }


    public void setDataItem(DataItem item)
    {
        if (this.dataItem != null)
            this.dataItem.removeListener(this);
        
        this.dataItem = item;
        
        this.dataItem.addListener(this);
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
        this.visible = visible;
    }


    public DataStylerList getStylers()
    {
        return stylers;
    }
    
    
    /**
     * Computes item bounding box in world coordinate and return it
     * @return
     */
    public SpatialExtent getBoundingBox()
    {
        SpatialExtent bbox = null;
        
        // compute smallest bbox containing all children bbox
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler nextStyler = stylers.get(i);
            
            if (!nextStyler.getSymbolizer().isEnabled())
                continue;
            
            SpatialExtent childBox = nextStyler.getBoundingBox();
            
            if (i == 0)
                bbox = childBox.copy();
            else
                bbox.add(childBox);
        }
        
        return bbox;
    }


    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case ITEM_STYLE_CHANGED:            
                for (int i = 0; i < stylers.size(); i++)
                    stylers.get(i).updateDataMappings();                                
                break;
            
            case PROVIDER_DATA_CHANGED:
                for (int i = 0; i < stylers.size(); i++)
                    stylers.get(i).updateBoundingBox();
                break;
                
            case PROVIDER_DATA_CLEARED:
                parentScene.getRenderer().cleanup(this);
                break;
        }
    }
}
