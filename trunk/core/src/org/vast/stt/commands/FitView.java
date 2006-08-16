
package org.vast.stt.commands;

import org.vast.math.Vector3d;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.Projection;
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
        Projection proj = view.getProjection();
        Vector3d center = bbox.getCenter();
        view.setTargetPos(center);
        
        double dist = bbox.getDiagonalDistance();
        Vector3d camera = proj.getDefaultCameraLookDirection();
        camera.scale(-dist*10);
        camera.add(center);
        view.setCameraPos(camera);
        
        view.setUpDirection(proj.getDefaultCameraUpDirection());
        
        double dx = bbox.getMaxX() - bbox.getMinX();
        double dy = bbox.getMaxY() - bbox.getMinY();
        double viewAspectRatio = (double)view.getViewWidth() / (double)view.getViewHeight();
        double bboxAspectRatio = dx / dy;
        
        if (bboxAspectRatio >= viewAspectRatio)
            view.setOrthoWidth(dx);
        else
            view.setOrthoWidth(dy * viewAspectRatio);
        
        if (adjustZRange)
        {
            view.setFarClip(dist*20);
            view.setNearClip(dist);
        }
        
        view.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
    }


    public void execute()
    {
        SpatialExtent bbox = null;
        
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
