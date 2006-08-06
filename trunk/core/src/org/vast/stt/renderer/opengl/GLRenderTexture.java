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
import org.vast.stt.style.RasterTileGraphic;
import org.vast.stt.style.TextureStyler;
import org.vast.stt.style.TexturePatchGraphic;


/**
 * <p><b>Title:</b>
 * GL Render Texture
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Runnable for rendering mapped textures.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 2, 2006
 * @version 1.0
 */
public class GLRenderTexture extends GLRunnable
{
    protected TextureStyler styler;
    protected TexturePatchGraphic patch;
    protected boolean normalizeCoords;
    
    
    public GLRenderTexture(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public void setStyler(TextureStyler styler)
    {
        this.styler = styler;        
    }

    
    @Override
    public void run()
    {
        GridPointGraphic point;
        float uScale = 0.0f;
        float vScale = 0.0f;
        int count = 0;
                
        RasterTileGraphic tex = patch.getTexture();
        GridPatchGraphic grid = patch.getGrid();
                
        // select fill mode
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

        gl.glLineWidth(grid.lineWidth);
        gl.glPolygonOffset(offset, 1.0f);
        gl.glDisable(GL.GL_CULL_FACE);            
        gl.glColor4f(1.0f, 1.0f, 1.0f, tex.opacity);
        
        // compute tex coordinate scale (for padded textures)
        if (tex.widthPadding != 0 || tex.heightPadding != 0)
        {
            uScale = (float)tex.width / (float)(tex.width + tex.widthPadding);
            vScale = (float)tex.height / (float)(tex.height + tex.heightPadding);
        }
        
        do
        {
            double oldX = 0.0;
            
            // loop through all grid points
            for (int v = 0; v < grid.length-1; v++)
            {
                gl.glBegin(GL.GL_QUAD_STRIP);
                            
                for (int u = 0; u < grid.width; u++)
                {
                    for (int p=0; p<2; p++)
                    {                    
                        point = styler.getGridPoint(u, v+p, uScale, vScale, normalizeCoords);
                        
                        // TODO hack to break grid when crossing lat/lon boundary
                        if (Math.abs(point.x - oldX) > Math.PI*9/10)
                        {
                            gl.glEnd();
                            gl.glBegin(GL.GL_QUAD_STRIP);
                        }
                        oldX = point.x;
                        
                        gl.glTexCoord2f(point.tx, point.ty);
                        gl.glVertex3d(point.x, point.y, point.z);
                    }
                }
                
                gl.glEnd();
            }
            
            count++;
            if (count == blockCount)
                break;
        }
        while ((patch = styler.nextTile()) != null);
        
        blockCount = count;
    }
}
