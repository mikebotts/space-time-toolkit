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

package org.vast.stt.project.world;

import org.vast.stt.event.STTEvent;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.renderer.opengl.JOGLRenderer;


/**
 * <p><b>Title:</b><br/>
 * World Scene Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current state of a world scene (graphic view)
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class WorldScene extends Scene<WorldSceneItem>
{
    protected ViewSettings viewSettings;
	protected TimeSettings timeSettings;
    protected CameraControl cameraController;
    

    public WorldScene()
    {
        super();
        renderer = new JOGLRenderer();
        cameraController = new CameraControl_Base(this);
    }
    
    
    @Override
    protected WorldSceneItem createNewItem()
    {
        return new WorldSceneItem(this);
    }


	public ViewSettings getViewSettings()
	{
		return viewSettings;
	}


	public void setViewSettings(ViewSettings viewSettings)
	{
        if (this.viewSettings != viewSettings)
        {
            if (this.viewSettings != null)
                this.viewSettings.removeListener(this);
            
            this.viewSettings = viewSettings;
            
            if (this.viewSettings != null)
                this.viewSettings.addListener(this);
        }
	}


	public TimeSettings getTimeSettings()
	{
		return timeSettings;
	}


	public void setTimeSettings(TimeSettings timeSettings)
	{
        if (this.timeSettings != timeSettings)
        {
            if (this.timeSettings != null)
                this.timeSettings.removeListener(this);
            
            this.timeSettings = timeSettings;
            
            if (this.timeSettings != null)
                this.timeSettings.addListener(this);
        }
	}
    
    
    public CameraControl getCameraController()
    {
        return cameraController;
    }


    public void setCameraController(CameraControl cameraController)
    {
        this.cameraController = cameraController;
    }
    
    
    public void handleEvent(STTEvent event)
    {
        switch (event.type)
        {
            case SCENE_PROJECTION_CHANGED:
                for (int i = 0; i < sceneItems.size(); i++)
                    sceneItems.get(i).setProjection(viewSettings.getProjection());
                break;
                
            case SCENE_VIEW_CHANGED:
                dispatchEvent(event.copy());
                break;
                
            case SCENE_TIME_CHANGED:
                dispatchEvent(event.copy());
                break;
        }
    }
}
