/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

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
 * <p>Copyright (c) 2007</p>
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
        float eX, eY;
        int count = 0;
        
        RasterTileGraphic tex = patch.getTexture();
        GridPatchGraphic grid = patch.getGrid();
                
        // select fill mode
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        gl.glDisable(GL.GL_CULL_FACE);            
        gl.glColor4f(1.0f, 1.0f, 1.0f, tex.opacity);
        
        // compute tex coordinate scale (for padded textures)
        if (normalizeCoords)
        {
        	uScale = (float)tex.width / (float)(tex.width + tex.widthPadding);
            vScale = (float)tex.height / (float)(tex.height + tex.heightPadding);
            eX = 0.5f / (float)tex.width;
            eY = 0.5f / (float)tex.height;
        }
        else
        {
            eX = eY = 0;
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
                        
                        // clamp to edge
                        if (normalizeCoords)
                        {
                        	if (point.tx < eX )
                        		point.tx = eX;
                        	else if (point.tx > 1-eX)
                        		point.tx = 1-eX;
                        	
                        	if (point.ty < eY )
                        		point.ty = eY;
                        	else if (point.ty > 1-eY)
                        		point.ty = 1-eY;
                        }
                        
                        // apply scaling for NPOT and padding
                        point.tx *= uScale;
                        point.ty *= vScale;
                        
                        gl.glTexCoord2f((float)point.tx, (float)point.ty);                        
                        gl.glVertex3d(point.x, point.y, point.z);
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
