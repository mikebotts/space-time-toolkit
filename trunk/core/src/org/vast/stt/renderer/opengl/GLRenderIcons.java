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

package org.vast.stt.renderer.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.vast.math.Vector3d;
import org.vast.stt.renderer.IconManager;
import org.vast.stt.renderer.IconManager.Icon;
import org.vast.stt.style.PointGraphic;


/**
 * <p><b>Title:</b>
 * GLRenderIcons
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Runnable for rendering icons.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 2, 2006
 * @version 1.0
 */
public class GLRenderIcons extends GLRenderPoints
{
    private JOGLRenderer mainRenderer;
    private IconManager iconManager = new IconManager();
    private Vector3d iconPos;
    
    
    public GLRenderIcons(GL gl, GLU glu, JOGLRenderer mainRenderer)
    {
        super(gl, glu);
        this.mainRenderer = mainRenderer;
        this.iconPos = new Vector3d();
    }

    
    @Override
    public void run()
    {
        PointGraphic point;
        int count = 0;
        
        // loop and draw all points
        do
        {
            while ((point = styler.nextPoint()) != null)
            {
                Icon icon = iconManager.getIcon(point.iconUrl);
                
                if (icon != null)
                {
                    mainRenderer.project(point.x, point.y, point.z, iconPos);
                    iconPos.x += point.iconOffsetX - icon.width * point.size / 2;
                    iconPos.y += point.iconOffsetY + icon.height * point.size / 2;
                    gl.glWindowPos3d(iconPos.x, iconPos.y, 0);
                    gl.glPixelZoom(point.size, -point.size);
                    gl.glDrawPixels(icon.width, icon.height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, icon.data);
                }
                else
                {
                    // default to white dot
                    gl.glPointSize(20.0f);
                    gl.glBegin(GL.GL_POINTS);
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    gl.glVertex3d(point.x, point.y, point.z);
                    gl.glEnd();
                    
                    gl.glPointSize(10.0f);
                    gl.glBegin(GL.GL_POINTS);
                    gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
                    gl.glVertex3d(point.x, point.y, point.z);
                    gl.glEnd();
                }
            }
            
            count++;
            if (count == blockCount)
                break;
        }
        while (styler.nextBlock() != null);

        blockCount = count;        
        gl.glEnd();
    }
}
