
package org.vast.stt.commands;

import java.util.Iterator;

import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataStyler;
import org.vast.stt.project.DataStylerList;
import org.vast.stt.project.Scene;
import org.vast.stt.project.SpatialExtent;


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
    private DataItem item;


    public FitView(DataItem item)
    {
        this.item = item;
    }


    public FitView(Scene scene)
    {
        this.scene = scene;
    }


    public void execute()
    {
        if (scene != null)
            fitScene();
        else if (item != null)
            fitItem();
    }


    public boolean isUndoAvailable()
    {
        return false;
    }


    public void unexecute()
    {
        // TODO Auto-generated method stub
    }


    public void fitScene()
    {
        //SpatialExtent bbox = scene.getRootFolder().getBoundingBox();

        // need to convert bbox to view settings somehow
        //scene.getViewSettings().setBoundingBox(newExtent);
    }


    public void fitItem()
    {
        //SpatialExtent extent = item.getBoundingBox();
        // need to convert bbox to view settings somehow
        //scene.getViewSettings().setSpatitalExtent(extent);
    }


    public void setScene(Scene scene)
    {
        this.scene = scene;
    }


    public void setItem(DataItem item)
    {
        this.item = item;
    }
}
