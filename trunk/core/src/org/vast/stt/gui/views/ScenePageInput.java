package org.vast.stt.gui.views;

import org.eclipse.core.runtime.IAdaptable;
import org.vast.stt.project.world.WorldScene;


public class ScenePageInput implements IAdaptable
{
    protected WorldScene scene;
    
    
    public ScenePageInput(WorldScene scene)
    {
        this.scene = scene;
    }
    
    
    public Object getAdapter(Class adapter)
    {
        return null;
    }


    public WorldScene getScene()
    {
        return scene;
    }


    public void setScene(WorldScene scene)
    {
        this.scene = scene;
    }	
}
