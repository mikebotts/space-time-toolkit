
package org.vast.stt.renderer;

import org.eclipse.swt.widgets.Canvas;
import org.vast.math.Vector3D;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataStylerList;
import org.vast.stt.project.Scene;
import org.vast.stt.style.StylerVisitor;
import java.util.*;


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
	protected Canvas canvas;
    protected Scene scene;


	public abstract void init();
	public abstract void dispose();
	public abstract void project(double worldX, double worldY, double worldZ, Vector3D viewPos);
	public abstract void unproject(double viewX, double viewY, double viewZ, Vector3D worldPos);
	public abstract void resizeView(int width, int height);
	protected abstract void setupView();	
	protected abstract void swapBuffers();
	
	
	public void drawScene()
	{
		setupView();
		List<DataItem> items = scene.getVisibleItems();
        
		for (int i=0; i<items.size(); i++)
		{
			DataItem item = items.get(i);
			
			if (!item.isEnabled())
				continue;
            
            //if (item.getDataProvider().isUpdating())
            //    continue;
			
			DataStylerList stylerList = item.getStylerList();
			stylerList.accept(this);
		}

		swapBuffers();
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
