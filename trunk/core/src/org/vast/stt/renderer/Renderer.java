
package org.vast.stt.renderer;

import org.eclipse.swt.widgets.Canvas;
import org.vast.math.Vector3D;
import org.vast.stt.scene.*;
import org.vast.stt.style.DataStyler;
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


	public abstract void init();
	public abstract void dispose();
	public abstract void project(double worldX, double worldY, double worldZ, Vector3D viewPos);
	public abstract void unproject(double viewX, double viewY, double viewZ, Vector3D worldPos);
	public abstract void resizeView(int width, int height);
	protected abstract void setupView(ViewSettings view);	
	protected abstract void swapBuffers();
	
	
	public void drawScene(Scene scene)
	{
		setupView(scene.getViewSettings());
		
		Iterator<DataItem> it = scene.getDataList().getItemIterator();
		
		while (it.hasNext())
		{
			DataItem item = it.next();
			
			if (!item.isEnabled())
				continue;
            
            //if (item.getDataProvider().isUpdating())
            //    continue;
			
			DataStyler styler = item.getStyler();
			if ((styler != null) && styler.isEnabled())
				styler.accept(this);
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
}
