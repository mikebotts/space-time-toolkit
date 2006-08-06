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
import org.vast.stt.style.PolygonPointGraphic;
import org.vast.stt.style.PolygonStyler;


/**
 * <p><b>Title:</b>
 * GL Render Polygons
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Runnable for rendering polygons.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 2, 2006
 * @version 1.0
 */
public class GLRenderPolygons extends GLRunnable
{
    protected PolygonStyler styler;
    
    
    public GLRenderPolygons(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public void setStyler(PolygonStyler styler)
    {
        this.styler = styler;        
    }

    
    @Override
    public void run()
    {
        PolygonPointGraphic point;
        boolean begin = false;
        int count = 0;
        
        // setup polygon offset
        gl.glPolygonOffset(offset, 1.0f);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        
        gl.glBegin(GL.GL_POLYGON);

        do
        {
            while ((point = styler.nextPoint()) != null)
            {
                if (point.polyBreak && begin)
                {
                    gl.glEnd();
                    gl.glBegin(GL.GL_POLYGON);
                }
    
                point.polyBreak = false;
                begin = true;
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
