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

import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.vast.math.Vector3d;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.project.world.Projection.Crs;
import org.vast.stt.provider.STTPolygonExtent;


/**
 * <p><b>Title:</b>
 * GLRenderPolyBox
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Renders a polygonal surface with handles according
 * to the given STTPolygonExtent
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Oct 31, 2006
 * @version 1.0
 */
public class GLRenderPolyBox
{   
    protected GL gl;
    protected GLU glu;
    protected JOGLRenderer renderer;
    protected final static double DTR = Math.PI/180;
    
    
    public GLRenderPolyBox(JOGLRenderer renderer, GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
        this.renderer = renderer;
    }
    
    
    public void drawROI(WorldScene scene, STTPolygonExtent extent, boolean onlyHandles)
    {
        gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT);
        gl.glDepthFunc(GL.GL_ALWAYS);                
        ViewSettings view = scene.getViewSettings();
        ArrayList<Vector3d> pointList = extent.getPointList();
        
        // TODO tesselate polygon for display in globe view??
        
        if (!onlyHandles)
        {
            // draw poly surface
        	gl.glBegin(GL.GL_POLYGON);
        	gl.glColor4f(1.0f, 0.0f, 0.0f, 0.3f);
            for (int i=0; i<pointList.size(); i++)
            {
            	Vector3d point = pointList.get(i);
            	drawPoint(view, point);
            }
            gl.glEnd();
            
            // draw poly boundary
            gl.glLineWidth(1);
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            gl.glBegin(GL.GL_LINE_LOOP);
            for (int i=0; i<pointList.size(); i++)
            {
            	Vector3d point = pointList.get(i);
            	drawPoint(view, point);
            }
            gl.glEnd();       
        }
        
        // draw handles
        gl.glPointSize(GLRenderRectBox.HANDLE_SIZE);
        gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glBegin(GL.GL_POINTS);
        for (int i=0; i<pointList.size(); i++)
        {
        	Vector3d point = pointList.get(i);
        	drawPoint(view, point);
        }
        gl.glEnd();
        gl.glPointSize(GLRenderRectBox.HANDLE_SIZE - 2);
        gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
        gl.glBegin(GL.GL_POINTS);
        for (int i=0; i<pointList.size(); i++)
        {
        	Vector3d point = pointList.get(i);
        	drawPoint(view, point);
        }
        gl.glEnd();
        
        gl.glPopAttrib();
    }
    
    
    protected void drawPoint(ViewSettings view, Vector3d point)
    {       
        point = point.copy();
        point.x *= DTR;
        point.y *= DTR;
    	view.getProjection().project(Crs.EPSG4329, point);
    	gl.glVertex3d(point.x, point.y, point.z);
    }
}
