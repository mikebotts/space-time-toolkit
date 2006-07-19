
package org.vast.stt.commands;

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
    
    
    protected void fit(SpatialExtent bbox)
    {
        ViewSettings view = scene.getViewSettings();
        bbox.getCenter();
    }


    public void execute()
    {
        SpatialExtent bbox = null;
        
        if (item == null)
            bbox = scene.getBoundingBox();
        else
            bbox = item.getBoundingBox();
        
        fit(bbox);
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
