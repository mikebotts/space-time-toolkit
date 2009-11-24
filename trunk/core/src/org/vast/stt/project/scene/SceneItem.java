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
import java.util.Hashtable;
import java.util.List;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.Projection;
import org.vast.stt.provider.STTSpatialExtent;
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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Jul 12, 2006
 * @version 1.0
 */
public class SceneItem implements STTEventListener
{
    protected Scene parentScene;
    protected DataItem dataItem;
    protected List<DataStyler> stylers;    
    protected List<SceneItem> maskItems;
    protected Hashtable<Symbolizer, DataStyler> stylerTable;
    protected boolean visible;


    public SceneItem(Scene scene)
    {
        stylers = new ArrayList<DataStyler>(1);
        maskItems = new ArrayList<SceneItem>(1);
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


    public List<DataStyler> getStylers()
    {
        return stylers;
    }
    
    
    public List<SceneItem> getMaskItems()
    {
        return maskItems;
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
            DataStyler styler = stylers.get(i);
            parentScene.getRenderer().cleanupSync(styler, null, CleanupSection.GEOMETRY);
            styler.setProjection(projection);
        }
        
        // also reproject all masks
        for (int i = 0; i < maskItems.size(); i++)
            maskItems.get(i).setProjection(projection);
    }
    
    
    /**
     * Clears all rendering cache related to this item
     */
    public void cleanup()
    {
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler styler = stylers.get(i);
            parentScene.getRenderer().cleanupAsync(styler, null, CleanupSection.ALL);
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
            parentScene.getRenderer().cleanupAsync(styler, deletedObjects, CleanupSection.ALL);
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
     * Update this styler after a change/addition/deletion
     * For deletion, symbolizer must first be disabled.
     * @param sym
     */
    protected void updateStyler(Symbolizer sym)
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
            // clear cached rendering data
            parentScene.getRenderer().cleanupAsync(styler, null, CleanupSection.ALL);
            
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
                if (dataItem.isEnabled())
                    updateStyler(symbolizer);
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
