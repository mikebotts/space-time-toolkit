
package org.vast.stt.commands;

import org.vast.math.Vector3d;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.Projection;
import org.vast.stt.project.Scene;
import org.vast.stt.project.SceneItem;
import org.vast.stt.project.SpatialExtent;
import org.vast.stt.project.ViewSettings;
import org.vast.stt.renderer.Renderer;


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
        
        // compute bbox 3D diagonal distance
        double dist = bbox.getDiagonalDistance();
        
        // change camera target to center of bbox
        ViewSettings view = scene.getViewSettings();
        Projection proj = view.getProjection();
        Vector3d center = bbox.getCenter();
        view.setTargetPos(center);
        
        // change camera pos
        Vector3d camera = proj.getDefaultCameraLookDirection(center);
        camera.scale(-dist*10);
        camera.add(center);
        view.setCameraPos(camera);
        
        // change camera up direction
        view.setUpDirection(proj.getDefaultCameraUpDirection(center));
        
        // adjust z range        
        if (adjustZRange)
        {
            view.setFarClip(dist*20);
            view.setNearClip(dist);
        }
        
        // now use renderer to find projection of bbox on screen
        Renderer renderer = scene.getRenderer();
        renderer.setupView();
        Vector3d winPoint1 = new Vector3d();
        Vector3d winPoint2 = new Vector3d();
        renderer.project(bbox.getMinX(), bbox.getMinY(), bbox.getMinZ(), winPoint1);
        renderer.project(bbox.getMaxX(), bbox.getMaxY(), bbox.getMaxZ(), winPoint2);
                
        // get dimensions of projection of bbox
        double dx = Math.abs(winPoint1.x - winPoint2.x);
        double dy = Math.abs(winPoint1.y - winPoint2.y);
        
        // set new orthowidth
        double viewWidth = (double)renderer.getViewWidth();
        double viewHeight = (double)renderer.getViewHeight();
        double viewAspectRatio = viewWidth / viewHeight;
        double bboxAspectRatio = dx / dy;
        double oldWidth = view.getOrthoWidth();
        
        if (bboxAspectRatio >= viewAspectRatio)
            view.setOrthoWidth(oldWidth * dx / viewWidth);
        else
            view.setOrthoWidth(oldWidth * dy / viewHeight);
        
        // send event to redraw
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
