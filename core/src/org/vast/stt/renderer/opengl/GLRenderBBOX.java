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
import org.vast.physics.SpatialExtent;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.ViewSettings;
import org.vast.stt.project.scene.Projection.Crs;
import org.vast.stt.style.PointGraphic;


/**
 * <p><b>Title:</b>
 * GLRenderBBOX
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Renders a surface with handles according to the given
 * SpatialExtent
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Oct 31, 2006
 * @version 1.0
 */
public class GLRenderBBOX
{   
    protected GL gl;
    protected GLU glu;
    protected final static double DTR = Math.PI/180;
    protected PointGraphic point = new PointGraphic();
    protected ViewSettings view;
    
    
    public GLRenderBBOX(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }
    
    
    public void drawROI(Scene scene, SpatialExtent extent, boolean onlyHandles)
    {
        gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT);
        gl.glDepthFunc(GL.GL_ALWAYS);                
        view = scene.getViewSettings();
        
        // compute grid dimensions and step in rads
        int gridWidth = 10;
        int gridLength = 10;        
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
                        drawPoint(point);
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
                drawPoint(point);
            }        
            // segment 2
            for (int v = 0; v < gridLength; v++)
            {
                point.x = maxX;
                point.y = minY + dY * v;
                drawPoint(point);
            }        
            // segment 3
            for (int u = gridWidth-1; u >= 0; u--)
            {
                point.x = minX + dX * u;
                point.y = maxY;
                drawPoint(point);
            }        
            // segment 4
            for (int v = gridLength-1; v >= 0 ; v--)
            {
                point.x = minX;
                point.y = minY + dY * v;
                drawPoint(point);
            }
            
            gl.glEnd();        
        }
        
        // draw handles
        double s = dX/6;//view.getOrthoWidth() / view.getViewWidth() / ratio * 50;
        
        for (int g=0; g<2; g++)
        {
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
                {
                    gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
                    gl.glPushName(-i-1);
                }
                else
                {
                    gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                    gl.glLineWidth(1);
                    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
                }
                
                gl.glBegin(GL.GL_QUAD_STRIP);
                
                for (int j=0; j<4; j++)
                {
                    point.x = cX;
                    point.y = cY;
                    point.x += (j==0 || j==1) ? -s : s;
                    point.y += (j==0 || j==2) ? -s : s;
                    drawPoint(point);
                }
                
                gl.glEnd();
                
                if (g == 0)
                    gl.glPopName();
            }            
        }
        
        gl.glPopAttrib();
    }
    
    
    protected void drawPoint(PointGraphic point)
    {       
        point.z = 0;
        view.getProjection().adjust(Crs.EPSG4329, point);
        gl.glVertex3d(point.x, point.y, point.z);
    }
}
