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
import org.vast.stt.style.LinePointGraphic;
import org.vast.stt.style.VectorStyler;


/**
 * <p><b>Title:</b>
 * GLRenderVectors
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Runnable for rendering vectors.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 2, 2006
 * @version 1.0
 */
public class GLRenderVectors extends GLRunnable
{
    protected VectorStyler styler;
    
    
    public GLRenderVectors(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public void setStyler(VectorStyler styler)
    {
        this.styler = styler;        
    }

    
    @Override
    public void run()
    {
        LinePointGraphic point;
        boolean begin = false;
        float oldWidth = -1.0f;
        int count = 0;
        
        // loop and draw all points
        do
        {
            while ((point = styler.nextPoint()) != null)
            {
                if (!begin)
                {
                    // enable line smooth if needed        
                    if (point.smooth)
                        gl.glEnable(GL.GL_LINE_SMOOTH);
                    else
                        gl.glDisable(GL.GL_LINE_SMOOTH);
                }
                
                if (point.width != oldWidth)
                {
                    if (begin)
                    {
                        gl.glEnd();
                        begin = false;
                    }
                    gl.glLineWidth(point.width);
                    oldWidth = point.width;
                    gl.glBegin(GL.GL_LINE_STRIP);
                }
    
                if (point.graphBreak && begin)
                {
                    gl.glEnd();
                    gl.glBegin(GL.GL_LINE_STRIP);
                }

                point.graphBreak = false;
                begin = true;
                gl.glColor4f(point.r, point.g, point.b, point.a);
                gl.glVertex3d(point.x, point.y, point.z);
            }
            
            count++;
            if (count == blockCount)
                break;
        }
        while ((styler.nextBlock()) != null);
        
        blockCount = count;
        
        if (begin)
            gl.glEnd();
    }
}
