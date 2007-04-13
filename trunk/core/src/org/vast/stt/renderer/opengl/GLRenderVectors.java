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
import org.vast.stt.style.VectorGraphic;
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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 12, 2007
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
        VectorGraphic vector;
        boolean begin = false;
        float oldWidth = -1.0f;
        int count = 0;
        
        // loop and draw all vectors
        do
        {
            while ((vector = styler.nextVector()) != null)
            {
                LinePointGraphic point1 = vector.point1;
                LinePointGraphic point2 = vector.point2;
                
                if (point1.width != oldWidth)
                {
                    if (begin)
                        gl.glEnd();

                    gl.glLineWidth(point1.width);
                    oldWidth = point1.width;
                    gl.glBegin(GL.GL_LINES);
                    begin = true;
                }                
                
                gl.glColor4f(point1.r, point1.g, point1.b, point1.a);
                gl.glVertex3d(point1.x, point1.y, point1.z);
                gl.glVertex3d(point2.x, point2.y, point2.z);
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
