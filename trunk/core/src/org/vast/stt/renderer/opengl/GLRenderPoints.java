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

import org.vast.stt.style.PointGraphic;
import org.vast.stt.style.PointStyler;


/**
 * <p><b>Title:</b>
 * GLRenderPoints
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Runnable for rendering points.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 2, 2006
 * @version 1.0
 */
public class GLRenderPoints extends GLRunnable
{
    protected PointStyler styler;
    
    
    public GLRenderPoints(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public void setStyler(PointStyler styler)
    {
        this.styler = styler;        
    }

    
    @Override
    public void run()
    {
        PointGraphic point;
        boolean begin = false;
        float oldSize = -1.0f;
        int count = 0;
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_POINT);
        
        // loop and draw all points
        do
        {
            while ((point = styler.nextPoint()) != null)
            {
                // hack to allow changing point size
                if (point.size != oldSize)
                {
                    if (begin)
                        gl.glEnd();
                    gl.glPointSize(point.size);
                    oldSize = point.size;
                    gl.glBegin(GL.GL_POINTS);
                    begin = true;
                }

                if (point.a == 0)
                    continue;
                
                gl.glColor4f(point.r, point.g, point.b, point.a);
                gl.glVertex3d(point.x, point.y, point.z);
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
