
package org.vast.stt.renderer;

import java.util.List;
import org.eclipse.swt.widgets.Canvas;
import org.vast.math.Vector3d;
import org.vast.stt.data.BlockInfo;
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


    public abstract void dispose();
    
    
    public abstract void cleanup(DataStyler styler, CleanupSection section);


    public abstract void project(double worldX, double worldY, double worldZ, Vector3d viewPos);


    public abstract void unproject(double viewX, double viewY, double viewZ, Vector3d worldPos);


    public abstract void resizeView(int width, int height);


    protected abstract void setupView();


    protected abstract void swapBuffers();
    
    
    protected abstract void drawItem(SceneItem item);
    
    
    public void drawScene()
    {
        setupView();
        List<SceneItem> sceneItems = scene.getSceneItems();
        
        for (int i = 0; i < sceneItems.size(); i++)
        {
            SceneItem nextItem = sceneItems.get(i);

            if (!nextItem.isVisible())
                continue;
            
            if (!nextItem.getDataItem().isEnabled())
                continue;

            drawItem(nextItem);
        }
        
        swapBuffers();
    }
    
    
    /**
     * Returns true if the block should not be rendered
     */
    public boolean filterBlock(BlockInfo blockInfo)
    {
        return false;
    }
    
    
    /**
     * Takes care of flushing block rendering cache
     * @param blockInfo
     */
    public void deleteBlock(BlockInfo blockInfo)
    {
        
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
