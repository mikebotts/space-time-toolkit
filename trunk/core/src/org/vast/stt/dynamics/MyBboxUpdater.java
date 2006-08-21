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
import org.vast.stt.project.Scene;
import org.vast.stt.project.SpatialExtent;
import org.vast.stt.project.ViewSettings;
import org.vast.stt.renderer.Renderer;


/**
 * <p><b>Title:</b>
 * MyBboxUpdater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO MyBboxUpdater type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 11, 2006
 * @version 1.0
 */
public class MyBboxUpdater extends SpatialExtentUpdater implements STTEventListener
{
    private final static double RTD = 180 / Math.PI;
    private Renderer renderer;
    private ViewSettings view;
    
    
    public MyBboxUpdater(SpatialExtent spatialExtent)
    {
        setSpatialExtent(spatialExtent);
        ScenePageInput input = (ScenePageInput)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getInput();
        Scene scene = input.getScene();
        renderer = scene.getRenderer();
        view = scene.getViewSettings();
        view.addListener(this);
    }
    
    
    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case SCENE_VIEW_CHANGED:
                double centerX = view.getTargetPos().x * RTD;
                double centerY = view.getTargetPos().y * RTD;
                double dX = view.getOrthoWidth()/2 * RTD;
                double dY = dX * renderer.getViewHeight() / renderer.getViewWidth();
                
                spatialExtent.setMinX(Math.max(centerX - dX, -180));
                spatialExtent.setMaxX(Math.min(centerX + dX, +180));
                spatialExtent.setMinY(Math.max(centerY - dY, -90));
                spatialExtent.setMaxY(Math.min(centerY + dY, +90));
                
                spatialExtent.dispatchEvent(new STTEvent(spatialExtent, EventType.PROVIDER_SPATIAL_EXTENT_CHANGED));
                break;
        }        
    }
}
