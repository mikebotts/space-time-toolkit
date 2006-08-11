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
public class GLRenderGridBorder extends GLRunnable
{
    protected AbstractGridStyler styler;
    protected GridPatchGraphic patch;
    
    
    public GLRenderGridBorder(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public void setStyler(AbstractGridStyler styler)
    {
        this.styler = styler;        
    }
    
    
    public void setPatch(GridPatchGraphic patch)
    {
        this.patch = patch;
    }

    
    @Override
    public void run()
    {
        GridPointGraphic point;
        int count = 0;
        
        // select fill or wireframe
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        
        gl.glLineWidth(patch.lineWidth);            
        gl.glPolygonOffset(offset, 1.0f);
        gl.glDisable(GL.GL_CULL_FACE);            
        
        do
        {
            gl.glBegin(GL.GL_LINE_STRIP);
            
            // segment 1
            for (int u = 0; u < patch.width; u++)
            {
                point = styler.getPoint(u, 0);
                renderPoint(point);
            }
            
            // segment 2
            for (int v = 0; v < patch.length; v++)
            {
                point = styler.getPoint(patch.width-1, v);
                renderPoint(point);
            }
            
            // segment 3
            for (int u = patch.width-1; u >= 0; u--)
            {
                point = styler.getPoint(u, patch.length-1);
                renderPoint(point);
            }
            
            // segment 4
            for (int v = patch.length-1; v >= 0 ; v--)
            {
                point = styler.getPoint(0, v);
                renderPoint(point);
            }
            
            gl.glEnd();
            
            count++;
            if (count == blockCount)
                break;
        }
        while ((patch = styler.nextPatch()) != null);
        
        blockCount = count;
    }
    
    
    protected void renderPoint(GridPointGraphic point)
    {       
        gl.glColor4f(point.r, point.g, point.b, point.a);
        gl.glVertex3d(point.x, point.y, point.z);
    }
}
