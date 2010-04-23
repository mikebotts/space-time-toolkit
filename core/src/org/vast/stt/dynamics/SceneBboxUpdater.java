/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.dynamics;

import org.vast.math.Vector3d;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.project.world.WorldScene;


/**
 * <p><b>Title:</b>
 * SceneBboxUpdater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to automatically update provider spatial extent using
 * the current scene visible bbox
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Aug 11, 2006
 * @version 1.0
 */
public class SceneBboxUpdater extends SpatialExtentUpdater implements STTEventListener
{
    private WorldScene scene;
    private int tileSizeX = 0;
    private int tileSizeY = 0;
        
    
    public SceneBboxUpdater(WorldScene scene)
    {
        this.setScene(scene);
    }
    
    
    public void setTilesize(int tileSizeX, int tileSizeY)
    {
        spatialExtent.setTilingEnabled(true);
        this.tileSizeX = tileSizeX;
        this.tileSizeY = tileSizeY;
    }
    
    
    public void update()
    {
        double tiltFactor = 1.;
        
        //synchronized (scene.getViewSettings())
        {
            ViewSettings viewSettings = scene.getViewSettings();
            viewSettings.getProjection().fitBboxToView(spatialExtent, scene);
            
            // compute factor to increase the number of tiles when view is tilted
            Vector3d diff = viewSettings.getCameraPos().copy();
            diff.sub(viewSettings.getTargetPos());
            diff.normalize();
            if (diff.z != 0)
            {
                tiltFactor = 1. / diff.z;
                tiltFactor = Math.min(tiltFactor, 2);
            }
            
            tiltFactor *= 1.1;
        }
        
        if (spatialExtent.isTilingEnabled())
        {
            spatialExtent.setXTiles((float)scene.getRenderer().getViewWidth() / (float)tileSizeX * (float)tiltFactor);
            spatialExtent.setYTiles((float)scene.getRenderer().getViewHeight() / (float)tileSizeY * (float)tiltFactor);
        }
    }
    
    
    public void setScene(WorldScene scene)
    {
        this.scene = scene;        
        scene.getViewSettings().addListener(this);
    }
    
    
    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case SCENE_VIEW_CHANGED:
                if (enabled)
                {
                    update();
                	spatialExtent.dispatchEvent(new STTEvent(spatialExtent, EventType.SPATIAL_EXTENT_CHANGED), true);
                }
                break;
        }
    }
}
