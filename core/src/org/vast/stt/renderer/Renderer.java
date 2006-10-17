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

import org.eclipse.swt.widgets.Canvas;
import org.vast.math.Vector3d;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.scene.ViewSettings;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.StylerVisitor;


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
public abstract class Renderer implements StylerVisitor
{
    public enum CleanupSection
    {
        ALL,
        GEOMETRY,
        TEXTURES,
    }
    
    
    protected Canvas canvas;


    public abstract void init();
    
    
    public abstract void drawScene(Scene scene);
    
    
    public abstract void drawItem(SceneItem item);
    
    
    public abstract void cleanup(DataStyler styler, CleanupSection section);
    
    
    public abstract void cleanup(DataStyler styler, Object[] objects, CleanupSection section);


    public abstract void project(double worldX, double worldY, double worldZ, Vector3d viewPos);


    public abstract void unproject(double viewX, double viewY, double viewZ, Vector3d worldPos);
    
    
    public abstract PickedObject[] pick(Scene scene, double x, double y, double z, double dX, double dY, int dZ);


    public abstract void resizeView(int width, int height);


    public abstract void setupView(ViewSettings viewSettings);
    
    
    public abstract void dispose();
    
    
    public int getViewWidth()
    {
        return canvas.getClientArea().width;
    }
    
    
    public int getViewHeight()
    {
        return canvas.getClientArea().height;
    }


    public Canvas getCanvas()
    {
        return canvas;
    }


    public void setCanvas(Canvas canvas)
    {
        this.canvas = canvas;
    }
}
