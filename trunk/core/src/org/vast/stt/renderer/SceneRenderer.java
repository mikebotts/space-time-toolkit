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

package org.vast.stt.renderer;

import org.eclipse.swt.widgets.Composite;
import org.vast.math.Vector3d;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b><br/>
 * Renderer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Common interface for all scene renderers.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public abstract class SceneRenderer<SceneType extends Scene>
{
    public enum CleanupSection
    {
        ALL,
        GEOMETRY,
        TEXTURES,
    }    
    
    protected Composite composite;
    protected int viewWidth, viewHeight;
    

    public abstract void init();
    
    
    public abstract void drawScene(SceneType scene);
       
    
    public abstract void cleanup(DataStyler styler, CleanupSection section);
    
    
    public abstract void cleanup(DataStyler styler, Object[] objects, CleanupSection section);


    public abstract void project(double worldX, double worldY, double worldZ, Vector3d viewPos);


    public abstract void unproject(double viewX, double viewY, double viewZ, Vector3d worldPos);
    
    
    public abstract PickedObject pick(SceneType scene, PickFilter filter);


    public abstract void setupView(ViewSettings viewSettings);
    
    
    public abstract void dispose();
    
    
    public void resizeView(int width, int height)
    {
        this.viewHeight = height;
        this.viewWidth = width;
    }


    public int getViewWidth()
    {
        return viewWidth;
    }


    public int getViewHeight()
    {
        return viewHeight;
    }
    
    
    public double getAspectRatio()
    {
        return (double)viewWidth/(double)viewHeight;
    }
    
    
    public Composite getParent()
    {
        return composite;
    }


    public void setParent(Composite composite)
    {
        this.composite = composite;
    }
}
