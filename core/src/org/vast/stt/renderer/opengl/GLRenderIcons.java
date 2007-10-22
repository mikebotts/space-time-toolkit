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
import org.vast.math.Vector3d;
import org.vast.stt.renderer.IconManager;
import org.vast.stt.renderer.IconManager.Icon;
import org.vast.stt.style.PointGraphic;


/**
 * <p><b>Title:</b>
 * GLRenderIcons
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Runnable for rendering icons.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Aug 2, 2006
 * @version 1.0
 */
public class GLRenderIcons extends GLRenderPoints
{
    private JOGLRenderer mainRenderer;
    private IconManager iconManager = new IconManager();
    private Vector3d iconPos;
    
    
    public GLRenderIcons(GL gl, GLU glu, JOGLRenderer mainRenderer)
    {
        super(gl, glu);
        this.mainRenderer = mainRenderer;
        this.iconPos = new Vector3d();
    }

    
    @Override
    public void run()
    {
        PointGraphic point;
        int count = 0;
        
        // loop and draw all points
        do
        {
            while ((point = styler.nextPoint()) != null)
            {
                Icon icon = iconManager.getIcon(point.iconUrl);
                
                if (icon != null)
                {
                    mainRenderer.project(point.x, point.y, point.z, iconPos);
                    iconPos.x += point.iconOffsetX - icon.width * point.size / 2;
                    iconPos.y += point.iconOffsetY + icon.height * point.size / 2;
                    gl.glWindowPos3d(iconPos.x, iconPos.y, 0);
                    gl.glPixelZoom(point.size, -point.size);
                    gl.glDrawPixels(icon.width, icon.height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, icon.data);
                }
                else
                {
                    // default to white dot
                    gl.glPointSize(20.0f);
                    gl.glBegin(GL.GL_POINTS);
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    gl.glVertex3d(point.x, point.y, point.z);
                    gl.glEnd();
                    
                    gl.glPointSize(10.0f);
                    gl.glBegin(GL.GL_POINTS);
                    gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
                    gl.glVertex3d(point.x, point.y, point.z);
                    gl.glEnd();
                }
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
