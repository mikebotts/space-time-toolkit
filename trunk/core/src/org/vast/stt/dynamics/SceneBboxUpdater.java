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

package org.vast.stt.dynamics;

import org.eclipse.ui.PlatformUI;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.STTSpatialExtent;


/**
 * <p><b>Title:</b>
 * SceneBboxUpdater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to update provide using the current scene visible bbox
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 11, 2006
 * @version 1.0
 */
public class SceneBboxUpdater extends SpatialExtentUpdater implements STTEventListener
{
    private WorldScene scene;
    private int tileSizeX, tileSizeY;
    //private long lastUpdateTime = -1;
        
    
    public SceneBboxUpdater(STTSpatialExtent spatialExtent)
    {
        spatialExtent.setUpdater(this);
        ScenePageInput input = (ScenePageInput)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getInput();
        scene = (WorldScene)input.getScene();
        scene.getViewSettings().addListener(this);
    }
    
    
    public SceneBboxUpdater(STTSpatialExtent spatialExtent, int tileSizeX, int tileSizeY)
    {
        this(spatialExtent);
        spatialExtent.setTilingEnabled(true);
        this.tileSizeX = tileSizeX;
        this.tileSizeY = tileSizeY;
    }
    
    
    public void updateBbox()
    {
        scene.getViewSettings().getProjection().fitBboxToView(spatialExtent, scene);
        spatialExtent.setXTiles(scene.getRenderer().getViewWidth() / tileSizeX);
        spatialExtent.setYTiles(scene.getRenderer().getViewHeight() / tileSizeY);
    }
    
    
    public void handleEvent(STTEvent e)
    {
        //long currentTime = System.currentTimeMillis();
        
        //if (currentTime - lastUpdateTime > 1000) // limit updates to 1 per 100ms
        //{
            //lastUpdateTime = currentTime;
            switch (e.type)
            {
                case SCENE_VIEW_CHANGED:
                    updateBbox();
                    spatialExtent.dispatchEvent(new STTEvent(spatialExtent, EventType.PROVIDER_SPATIAL_EXTENT_CHANGED));
                    break;
            }
        //}
    }
}
