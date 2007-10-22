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
 * <p>Copyright (c) 2007</p>
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
