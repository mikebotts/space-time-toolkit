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

import org.vast.stt.style.GridFillStyler;
import org.vast.stt.style.GridPatchGraphic;
import org.vast.stt.style.GridPointGraphic;
import org.vast.stt.style.AbstractGridStyler;


/**
 * <p><b>Title:</b>
 * GLRenderGrids
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Runnable for rendering grids.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 2, 2006
 * @version 1.0
 */
public class GLRenderGrids extends GLRunnable
{
    protected AbstractGridStyler styler;
    protected GridPatchGraphic patch;
    protected boolean fill;
    
    
    public GLRenderGrids(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public void setStyler(AbstractGridStyler styler)
    {
        this.styler = styler;
        
        if (styler instanceof GridFillStyler)
            fill = true;
        else
            fill = false;
    }

    
    @Override
    public void run()
    {
        GridPointGraphic point;
        int count = 0;
        
        gl.glDisable(GL.GL_CULL_FACE);
        
        // select fill or wireframe
        if (fill)
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        else
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        
        do
        {
            // loop through all grid points
            for (int v = 0; v < patch.length-1; v++)
            {
                gl.glLineWidth(patch.lineWidth);
                gl.glBegin(GL.GL_QUAD_STRIP);
                
                //System.err.print("\nLine: " + v);
                
                for (int u = 0; u < patch.width; u++)
                {
                    for (int p=0; p<2; p++)
                    {                    
                        point = styler.getPoint(u, v+p);
                        
                        if (point.graphBreak)
                        {
                            gl.glEnd();
                            gl.glBegin(GL.GL_QUAD_STRIP);
                            if (p == 0) u--;
                            point.graphBreak = false;
                            break;
                        }
                                                
                        gl.glColor4f(point.r, point.g, point.b, point.a);
                        gl.glVertex3d(point.x, point.y, point.z);
                    }
                    
                    //System.err.print(" " + u);
                }
                
                
                gl.glEnd();
            }
            
            count++;
            if (count == blockCount)
                break;
        }
        while ((patch = styler.nextPatch()) != null);
        
        blockCount = count;
    }
}
