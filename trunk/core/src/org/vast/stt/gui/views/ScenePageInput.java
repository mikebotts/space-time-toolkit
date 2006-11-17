package org.vast.stt.gui.views;

import org.eclipse.core.runtime.IAdaptable;
import org.vast.stt.project.scene.Scene;


public class ScenePageInput implements IAdaptable
{
    protected Scene<?> scene;
    
    
    public ScenePageInput(Scene<?> scene)
    {
        this.scene = scene;
    }
    
    
    public Object getAdapter(Class adapter)
    {
        return null;
    }


    public Scene<?> getScene()
    {
        return scene;
    }


    public void setScene(Scene<?> scene)
    {
        this.scene = scene;
    }	
}
