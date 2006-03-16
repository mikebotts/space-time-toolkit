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

import java.util.Hashtable;

import org.eclipse.swt.opengl.GL;
import org.eclipse.swt.opengl.GLU;
import org.eclipse.swt.opengl.GLContext;
import org.vast.math.Vector3D;
import org.vast.ows.sld.Color;
import org.vast.stt.renderer.Renderer;
import org.vast.stt.scene.*;
import org.vast.stt.style.*;


/**
 * <p><b>Title:</b><br/>
 * OpenGL Renderer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Main OpenGL renderer class.
 * Also uses auxiliary stylers to render specific graphic types.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class OpenGLRenderer extends Renderer
{
    private GLContext context;
    private int[] viewPort = new int[4];
    private double[] modelM = new double[16];
    private double[] projM = new double[16];
    private double[] xData = new double[1];
    private double[] yData = new double[1];
    private double[] zData = new double[1];
    private int GL_TEXTURE_TARGET = GL.GL_TEXTURE_2D;//0x84F5;//
    private Hashtable<RasterStyler, Integer> textureTable;


    public OpenGLRenderer()
    {
        textureTable = new Hashtable<RasterStyler, Integer>();
    }


    @Override
    public void drawScene(Scene scene)
    {
        if (!context.isCurrent())
            context.setCurrent();

        super.drawScene(scene);
    }


    @Override
    protected void setupView(ViewSettings view)
    {
        // clear back buffer
        Color backColor = view.getBackgroundColor();
        GL.glClearColor(backColor.getRedValue(), backColor.getGreenValue(), backColor.getBlueValue(), 1.0f);
        GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // set up projection
        GL.glMatrixMode(GL.GL_PROJECTION);
        GL.glLoadIdentity();
        float width = (float) view.getOrthoWidth();
        float height = width * view.getViewHeight() / view.getViewWidth();
        float farClip = (float) view.getFarClip();
        float nearClip = (float) view.getNearClip();
        GL.glOrtho(-width / 2.0f, width / 2.0f, -height / 2.0f, height / 2.0f, nearClip, farClip);

        // set up 3D camera position from ViewSettings
        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glLoadIdentity();
        double eyeX = view.getCameraPos().getX();
        double eyeY = view.getCameraPos().getY();
        double eyeZ = view.getCameraPos().getZ();
        double centerX = view.getTargetPos().getX();
        double centerY = view.getTargetPos().getY();
        double centerZ = view.getTargetPos().getZ();
        double upX = view.getUpDirection().getX();
        double upY = view.getUpDirection().getY();
        double upZ = view.getUpDirection().getZ();
        GLU.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }


    @Override
    public void resizeView(int width, int height)
    {
        if (!context.isCurrent())
            context.setCurrent();

        context.resize(0, 0, width, height);
    }


    @Override
    public void project(double worldX, double worldY, double worldZ, Vector3D viewPos)
    {
        GL.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelM);
        GL.glGetDoublev(GL.GL_PROJECTION_MATRIX, projM);
        GL.glGetIntegerv(GL.GL_VIEWPORT, viewPort);
        GLU.gluProject(worldX, worldY, worldZ, modelM, projM, viewPort, xData, yData, zData);
        viewPos.setCoordinates(xData[0], yData[0], zData[0]);
    }


    @Override
    public void unproject(double viewX, double viewY, double viewZ, Vector3D worldPos)
    {
        GL.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelM);
        GL.glGetDoublev(GL.GL_PROJECTION_MATRIX, projM);
        GL.glGetIntegerv(GL.GL_VIEWPORT, viewPort);
        GLU.gluUnProject(viewX, viewY, viewZ, modelM, projM, viewPort, xData, yData, zData);
        worldPos.setCoordinates(xData[0], yData[0], zData[0]);
    }


    @Override
    protected void swapBuffers()
    {
        context.swapBuffers();
    }


    @Override
    public void init()
    {
        context = new GLContext(canvas);
        context.setCurrent();

        GL.glClearDepth(1.0f);
        GL.glDepthFunc(GL.GL_LEQUAL);
        GL.glEnable(GL.GL_DEPTH_TEST);
        GL.glShadeModel(GL.GL_SMOOTH);
        GL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        //GL.glEnable(GL.GL_LINE_SMOOTH);
        GL.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        GL.glEnable(GL.GL_BLEND);
        GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        GL.glEnable(GL_TEXTURE_TARGET);
    }


    @Override
    public void dispose()
    {
        if (context != null)
        {
            context.dispose();
            context = null;
        }
    }


    public void visit(LineStyler styler)
    {
        boolean allowBreak = false;
        
        // get line width of first point
        styler.reset();
        LinePointGraphic point = styler.nextPoint();
        GL.glLineWidth(point.width); 
        
        // loop and draw all line points
        styler.reset();
        GL.glBegin(GL.GL_LINE_STRIP);        
        while (styler.hasNext())
        {
            point = styler.nextPoint();
            
            if (allowBreak)
            {
                if (point.lineBreak)
                {
                    GL.glEnd();
                    GL.glBegin(GL.GL_LINE_STRIP);
                }
            }
            else
                allowBreak = true;
            
            GL.glColor4f(point.r, point.g, point.b, point.a);
            GL.glVertex3d(point.x, point.y, point.z);
        }        
        GL.glEnd();
    }


    public void visit(PointStyler styler)
    {
        // get point size from first point
        styler.reset();
        PointGraphic point = styler.nextPoint();
        GL.glPointSize(point.size); 
        
        // loop and draw all points
        styler.reset();
        GL.glBegin(GL.GL_POINTS);        
        while (styler.hasNext())
        {
            point = styler.nextPoint();                
            GL.glColor4f(point.r, point.g, point.b, point.a);
            GL.glVertex3d(point.x, point.y, point.z);
        }
        
        GL.glEnd();
    }


    public void visit(PolygonStyler styler)
    {
        // TODO Auto-generated method stub		
    }


    public void visit(RasterStyler styler)
    {
        int textureNum;        
        
        int tileCount = styler.getTileCount();
        
        for (int tileNum=0; tileNum<tileCount; tileNum++)
        {
            // retrieve or generate texture name
            if (textureTable.containsKey(styler))
            {
                textureNum = textureTable.get(styler);
            }
            else
            {
                int[] names = new int[1];
                GL.glGenTextures(1, names);
                textureTable.put(styler, names[0]);
                textureNum = names[0];
            }        
            
            // set current texture in GL
            GL.glBindTexture(GL_TEXTURE_TARGET, textureNum);
            
            // get first image
            RasterImageGraphic image = styler.getImage(tileNum);
            
            // reload the texture if needed
            if (image.updated)
            {
                GL.glTexImage2D(GL_TEXTURE_TARGET, 0, GL.GL_RGB, image.width, image.height, 0, GL.GL_BGR_EXT, GL.GL_UNSIGNED_BYTE, (byte[])image.data);
                GL.glTexParameteri(GL_TEXTURE_TARGET, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
                GL.glTexParameteri(GL_TEXTURE_TARGET, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
                image.updated = false;
            }
            
            // setup white background color and offsets
            GL.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            GL.glPolygonOffset(1.0f, 1.0f);
            GL.glEnable(GL.GL_POLYGON_OFFSET_FILL);
            GL.glEnable(GL.GL_BLEND);
            GL.glDisable(GL.GL_CULL_FACE);
            
            RasterGridGraphic gridPatch = styler.getGrid(tileNum);
            GridRowGraphic row1 = null;
            GridRowGraphic row2 = null;
            
            // loop through all rows
            while (styler.hasMoreRows())
            {
                if (row1 == null)
                    row1 = styler.nextGridRow();
                else
                    row1 = row2;
                
                row2 = styler.nextGridRow();
                
                // loop and draw quads
                GL.glBegin(GL.GL_QUAD_STRIP);
                for (int i=0; i<gridPatch.width; i++)
                {
                    GridPointGraphic point2 = row2.gridPoints[i];                    
                    GL.glColor4f(point2.r, point2.g, point2.b, point2.a);
                    GL.glTexCoord2f(point2.texX, point2.texY);
                    GL.glVertex3d(point2.x, point2.y, point2.z);
                    
                    GridPointGraphic point1 = row1.gridPoints[i];                    
                    GL.glColor4f(point1.r, point1.g, point1.b, point1.a);
                    GL.glTexCoord2f(point1.texX, point1.texY);
                    GL.glVertex3d(point1.x, point1.y, point1.z);
                }
                GL.glEnd();
            }
        }
        
        // back to no texture for next renderings
        GL.glBindTexture(GL_TEXTURE_TARGET, 0);
    }
}
