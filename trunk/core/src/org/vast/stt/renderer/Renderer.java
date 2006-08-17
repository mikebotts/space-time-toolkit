
package org.vast.stt.renderer;

import org.eclipse.swt.widgets.Canvas;
import org.vast.math.Vector3d;
import org.vast.stt.project.DataStyler;
import org.vast.stt.project.Scene;
import org.vast.stt.project.SceneItem;
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
    protected Scene scene;


    public abstract void init();
    
    
    public abstract void drawScene();
    
    
    public abstract void cleanup(DataStyler styler, CleanupSection section);
    
    
    public abstract void cleanup(DataStyler styler, Object[] objects, CleanupSection section);


    public abstract void project(double worldX, double worldY, double worldZ, Vector3d viewPos);


    public abstract void unproject(double viewX, double viewY, double viewZ, Vector3d worldPos);


    public abstract void resizeView(int width, int height);


    public abstract void setupView();
    
    
    public abstract void dispose();
    
    
    public abstract void drawItem(SceneItem item);
       
    
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


    public Scene getScene()
    {
        return scene;
    }


    public void setScene(Scene scene)
    {
        this.scene = scene;
    }
}
