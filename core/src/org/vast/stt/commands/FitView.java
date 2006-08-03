
package org.vast.stt.commands;

import org.vast.math.Vector3d;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.Scene;
import org.vast.stt.project.SceneItem;
import org.vast.stt.project.SpatialExtent;
import org.vast.stt.project.ViewSettings;


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
    
    
    protected void fit(SpatialExtent bbox, boolean adjustZRange)
    {
        if (bbox == null || bbox.isNull())
            return;
        
        ViewSettings view = scene.getViewSettings();
        Vector3d center = bbox.getCenter();
        view.setTargetPos(center);
        
        double dist = bbox.getDiagonalDistance();
        Vector3d camera = center.copy();
        camera.add(new Vector3d(0, 0, dist/2));
        view.setCameraPos(camera);
        
        view.setUpDirection(new Vector3d(0, 1, 0));        
        view.setOrthoWidth(bbox.getMaxX() - bbox.getMinX());
        
        if (adjustZRange)
        {
            view.setFarClip(dist);
            view.setNearClip(0);
        }
        
        view.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
    }


    public void execute()
    {
        SpatialExtent bbox = null;
        
        if (item == null)
        {
            bbox = scene.getBoundingBox();
            fit(bbox, false);
        }
        else
        {
            bbox = item.getBoundingBox();
            fit(bbox, true);
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
