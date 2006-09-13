
package org.vast.stt.commands;

import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.scene.ViewSettings;
import org.vast.stt.provider.STTSpatialExtent;


/**
 *  FitView either to an entire scene or a single DataItem
 *   
 * @author tcook
 * @date Feb 27, 2006
 * 
 * TODO:  - Need to convert from SpatialExtent to ViewSettings   
 *          before this will actually work    
 */

public class FitView implements Command
{
    private Scene scene;
    private SceneItem item;


    public FitView(Scene scene, SceneItem item)
    {
        this.scene = scene;
        this.item = item;
    }


    public FitView(Scene scene)
    {
        this.scene = scene;
    }
    
    
    protected void fit(STTSpatialExtent bbox, boolean adjustZRange)
    {
        if (bbox == null || bbox.isNull())
            return;
        
        ViewSettings view = scene.getViewSettings();
        view.getProjection().fitViewToBbox(bbox, scene, adjustZRange);
        
        // send event to redraw
        view.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
    }


    public void execute()
    {
        STTSpatialExtent bbox = null;
        
        if (item == null)
        {
            // fit to whole scene
            bbox = scene.getBoundingBox();
            fit(bbox, true);
        }
        else
        {
            // fit to one item
            bbox = item.getBoundingBox();
            fit(bbox, false);
        }
    }
    
    
    public void unexecute()
    {
    }


    public boolean isUndoAvailable()
    {
        return false;
    }


    public void setScene(Scene scene)
    {
        this.scene = scene;
    }


    public void setItem(SceneItem item)
    {
        this.item = item;
    }
}
