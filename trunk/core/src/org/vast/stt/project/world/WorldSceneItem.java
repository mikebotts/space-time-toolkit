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

import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.Projection;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.renderer.SceneRenderer.CleanupSection;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b>
 * World Scene Item
 * </p>
 *
 * <p><b>Description:</b><br/>
 * World entry carrying a DataItem and its stylers
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 12, 2006
 * @version 1.0
 */
public class WorldSceneItem extends SceneItem
{
    
    public WorldSceneItem(WorldScene scene)
    {
        super(scene);
    }    
        
    
    protected void prepareStyler(DataStyler styler)
    {
        styler.setProjection(((WorldScene)parentScene).getViewSettings().getProjection());
    }
    
    
    /**
     * Sets the new projection on all stylers and make sure
     * cached geometry is cleaned up
     * @param projection
     */
    public void setProjection(Projection projection)
    {
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler nextStyler = stylers.get(i);
            parentScene.getRenderer().cleanup(stylers.get(i), CleanupSection.GEOMETRY);
            nextStyler.setProjection(projection);
        }
    }
}
