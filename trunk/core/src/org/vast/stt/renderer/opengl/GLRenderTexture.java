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
    protected float zOffset;
    
    
    public GLRenderTexture(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public void setStyler(TextureStyler styler)
    {
        this.styler = styler;
        zOffset = 0.0f;
    }

    
    @Override
    public void run()
    {
        GridPointGraphic point;
        float uScale = 1.0f;
        float vScale = 1.0f;
        int count = 0;
        float dz;
        
        RasterTileGraphic tex = patch.getTexture();
        GridPatchGraphic grid = patch.getGrid();
                
        // select fill mode
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        gl.glDisable(GL.GL_CULL_FACE);            
        gl.glColor4f(1.0f, 1.0f, 1.0f, tex.opacity);
        
        // compute tex coordinate scale (for padded textures)
        if (normalizeCoords)
        {
            uScale = (float)tex.width / (float)(tex.width + tex.widthPadding + 1);
            vScale = (float)tex.height / (float)(tex.height + tex.heightPadding + 1);
        }
        else
        {
            uScale = (float)tex.width;
            vScale = (float)tex.height;
        }
        
        do
        {
            // loop through all grid points
            for (int v = 0; v < grid.length-1; v++)
            {
                gl.glBegin(GL.GL_QUAD_STRIP);
                            
                for (int u = 0; u < grid.width; u++)
                {
                    for (int p=0; p<2; p++)
                    {                    
                        point = styler.getGridPoint(u, v+p);
                        
                        if (point.graphBreak)
                        {
                            gl.glEnd();
                            gl.glBegin(GL.GL_QUAD_STRIP);
                            if (p == 0) u--;
                            point.graphBreak = false;
                            break;
                        }
                        
                        if (p>0)
                            dz = zOffset + 1e-7f;
                        else
                            dz = zOffset;
                        
                        gl.glTexCoord2f(point.tx * uScale, point.ty * vScale);
                        gl.glVertex3d(point.x, point.y, point.z + dz);
                    }
                }                
                
                zOffset += 1e-7f; // hack for superimposing textures in LLA ...
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
