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
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.project.world.Projection.Crs;
import org.vast.util.SpatialExtent;


/**
 * <p><b>Title:</b>
 * GLRenderRectBox
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Renders a rectangular surface with handles according
 * to the given SpatialExtent
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Oct 31, 2006
 * @version 1.0
 */
public class GLRenderRectBox
{   
    public static int HANDLE_SIZE = 8;
	protected GL gl;
    protected GLU glu;
    protected JOGLRenderer renderer;
    protected final static double DTR = Math.PI/180;
    protected Vector3d bboxCorner1 = new Vector3d();
    protected Vector3d bboxCorner2 = new Vector3d();
    protected Vector3d screenCorner1 = new Vector3d();
    protected Vector3d screenCorner2 = new Vector3d();
    protected Vector3d point = new Vector3d();
    
    
    public GLRenderRectBox(JOGLRenderer renderer, GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
        this.renderer = renderer;
    }
    
    
    public void drawROI(WorldScene scene, SpatialExtent extent, boolean onlyHandles)
    {
        gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT);
        gl.glDepthFunc(GL.GL_ALWAYS);                
        ViewSettings view = scene.getViewSettings();
        
        // compute grid dimensions and step in rads
        int gridWidth = 24;
        int gridLength = 24;        
        double minY = extent.getMinY() * DTR;
        double maxY = extent.getMaxY() * DTR;
        double minX = extent.getMinX() * DTR;
        double maxX = extent.getMaxX() * DTR;
        double dX = (maxX - minX) / (gridWidth-1);
        double dY = (maxY - minY) / (gridLength-1);
        
        if (!onlyHandles)
        {
            // draw bbox surface
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            gl.glColor4f(1.0f, 0.0f, 0.0f, 0.3f);
            
            for (int v = 0; v < gridLength-1; v++)
            {
                gl.glBegin(GL.GL_QUAD_STRIP);
                
                for (int u = 0; u < gridWidth; u++)
                {
                    for (int p = 0; p < 2; p++)
                    {                    
                        point.x = minX + dX * u;
                        point.y = minY + dY * (v + p);
                        drawPoint(view, point);
                    }
                }
                
                gl.glEnd();
            }
            
            // draw bbox boundary
            gl.glLineWidth(1);
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            gl.glBegin(GL.GL_LINE_STRIP);
            
            // segment 1
            for (int u = 0; u < gridWidth; u++)
            {
                point.x = minX + dX * u;
                point.y = minY;
                drawPoint(view, point);
            }        
            // segment 2
            for (int v = 0; v < gridLength; v++)
            {
                point.x = maxX;
                point.y = minY + dY * v;
                drawPoint(view, point);
            }        
            // segment 3
            for (int u = gridWidth-1; u >= 0; u--)
            {
                point.x = minX + dX * u;
                point.y = maxY;
                drawPoint(view, point);
            }        
            // segment 4
            for (int v = gridLength-1; v >= 0 ; v--)
            {
                point.x = minX;
                point.y = minY + dY * v;
                drawPoint(view, point);
            }
            
            gl.glEnd();        
        }
        
        // draw handles (two points of different size)
        for (int g=0; g<2; g++)
        {
        	if (g == 0)
            {
            	gl.glPointSize(HANDLE_SIZE);
                gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);                
            }
            else
            {
            	gl.glPointSize(HANDLE_SIZE-2);
                gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            }
        	
        	for (int i=0; i<5; i++)
            {
                double cX, cY;
                
                if (i == 4)
                {
                    cX = (minX+maxX) / 2;
                    cY = (minY+maxY) / 2;
                }
                else
                {
                    cX = (i==0 || i==1) ? minX : maxX;
                    cY = (i==0 || i==3) ? minY : maxY;
                }
                
                if (g == 0)
                	gl.glPushName(-i-1);
                
                gl.glBegin(GL.GL_POINTS);
                point.x = cX;
                point.y = cY;
                point.z = 0;
                view.getProjection().project(Crs.EPSG4329, point);
                gl.glVertex3d(point.x, point.y, point.z);
                gl.glEnd();
                
                if (g == 0)
                    gl.glPopName();
            }
        }
        
        gl.glPopAttrib();
    }
    
    
    protected void drawPoint(ViewSettings view, Vector3d point)
    {       
        point.z = 0;
        view.getProjection().project(Crs.EPSG4329, point);
        gl.glVertex3d(point.x, point.y, point.z);
    }
}
