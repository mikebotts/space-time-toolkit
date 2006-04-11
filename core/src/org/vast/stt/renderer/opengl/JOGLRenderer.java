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
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GLDrawableFactory;
import com.sun.opengl.util.GLUT;

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
public class JOGLRenderer extends Renderer
{
    private GLContext SWTContext;
    private javax.media.opengl.GLContext JOGLContext;
    private GL gl;
    private GLU glu;
    private GLUT glut;
    private int[] viewPort = new int[4];
    private double[] modelM = new double[16];
    private double[] projM = new double[16];
    private double[] coords = new double[3];
    private int GL_TEXTURE_TARGET = GL.GL_TEXTURE_2D;//0x84F5;//
    private Hashtable<RasterStyler, Integer> textureTable;
    

    public JOGLRenderer()
    {
        textureTable = new Hashtable<RasterStyler, Integer>();
    }


    @Override
    public void drawScene(Scene scene)
    {
        SWTContext.setCurrent();
        JOGLContext.makeCurrent();
        super.drawScene(scene);
    }


    @Override
    protected void setupView(ViewSettings view)
    {
        // clear back buffer
        Color backColor = view.getBackgroundColor();
        gl.glClearColor(backColor.getRedValue(), backColor.getGreenValue(), backColor.getBlueValue(), 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // set up projection
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        float width = (float) view.getOrthoWidth();
        float height = width * view.getViewHeight() / view.getViewWidth();
        float farClip = (float) view.getFarClip();
        float nearClip = (float) view.getNearClip();
        gl.glOrtho(-width / 2.0f, width / 2.0f, -height / 2.0f, height / 2.0f, nearClip, farClip);

        // set up 3D camera position from ViewSettings
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        double eyeX = view.getCameraPos().getX();
        double eyeY = view.getCameraPos().getY();
        double eyeZ = view.getCameraPos().getZ();
        double centerX = view.getTargetPos().getX();
        double centerY = view.getTargetPos().getY();
        double centerZ = view.getTargetPos().getZ();
        double upX = view.getUpDirection().getX();
        double upY = view.getUpDirection().getY();
        double upZ = view.getUpDirection().getZ();
        glu.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        
        // save projection matrices
        gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelM, 0);
        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projM, 0);
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewPort, 0);
    }


    @Override
    public void resizeView(int width, int height)
    {
        SWTContext.setCurrent();
        SWTContext.resize(0, 0, width, height);
    }


    @Override
    public void project(double worldX, double worldY, double worldZ, Vector3D viewPos)
    {
        glu.gluProject(worldX, worldY, worldZ, modelM, 0, projM, 0, viewPort, 0, coords, 0);
        viewPos.setCoordinates(coords[0], coords[1], coords[2]);
    }


    @Override
    public void unproject(double viewX, double viewY, double viewZ, Vector3D worldPos)
    {
        glu.gluUnProject(viewX, viewY, viewZ, modelM, 0, projM, 0, viewPort, 0, coords, 0);
        worldPos.setCoordinates(coords[0], coords[1], coords[2]);
    }


    @Override
    protected void swapBuffers()
    {
        SWTContext.swapBuffers();
        JOGLContext.release();
    }


    @Override
    public void init()
    {
        SWTContext = new GLContext(canvas);
        SWTContext.setCurrent();
        JOGLContext = GLDrawableFactory.getFactory().createExternalGLContext();
        JOGLContext.makeCurrent();
        
        gl = JOGLContext.getGL();
        glu = new GLU();
        glut = new GLUT();

        gl.glClearDepth(1.0f);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        //gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_TEXTURE_TARGET);
        
        JOGLContext.release();
    }


    @Override
    public void dispose()
    {
        if (SWTContext != null)
        {
            SWTContext.dispose();
            JOGLContext.release();
            SWTContext = null;
            JOGLContext = null;
        }
    }

    
    /**
     * Renders all data passed by a line styler
     */
    public void visit(LineStyler styler)
    {
        LinePointGraphic point = styler.point;
        boolean begin = false;
        styler.reset();        
        gl.glLineWidth(point.width);
        
        // loop and draw all points
        while (styler.nextBlock())
        {
            float oldWidth = -1.0f;
            
            while (styler.nextPoint())
            {
                if (point.width != oldWidth)
                {
                    if (begin)
                    {
                        gl.glEnd();
                        begin = false;
                    }
                    gl.glLineWidth(point.width);
                    oldWidth = point.width;
                    gl.glBegin(GL.GL_LINE_STRIP);                 
                }
                
                if (point.lineBreak && begin)
                {
                    gl.glEnd();
                    gl.glBegin(GL.GL_LINE_STRIP);                    
                }
                
                point.lineBreak = false;
                begin = true;
                gl.glColor4f(point.r, point.g, point.b, point.a);
                gl.glVertex3d(point.x, point.y, point.z);
            }
        }
        
        gl.glEnd();
    }


    /**
     * Renders all data passed by a point styler
     */
    public void visit(PointStyler styler)
    {       
        PointGraphic point = styler.point;
        boolean begin = false;
        float oldSize = -1.0f;
        styler.reset();        
        
        // loop and draw all points
        while (styler.nextBlock())
        {
            while (styler.nextPoint())
            {
                if (point.size != oldSize)
                {
                    if (begin) gl.glEnd();
                    gl.glPointSize(point.size);
                    oldSize = point.size;
                    gl.glBegin(GL.GL_POINTS);
                    begin = true;
                }
                
                gl.glColor4f(point.r, point.g, point.b, point.a);
                gl.glVertex3d(point.x, point.y, point.z);
            }
        }
        
        gl.glEnd();
    }


    /**
     * Renders all data passed by a polygon styler
     */
    public void visit(PolygonStyler styler)
    {
        PolygonPointGraphic point = styler.point;
        boolean begin = false;
        styler.reset();        
        
        // setup polygon offset
        gl.glPolygonOffset(1.0f, 1.0f);
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        
        // loop and draw all points
        gl.glBegin(GL.GL_POLYGON);
        
        while (styler.nextBlock())
        {
            while (styler.nextPoint())
            {
                if (point.polyBreak && begin)
                {
                    gl.glEnd();
                    gl.glBegin(GL.GL_POLYGON);                    
                }
                
                point.polyBreak = false;
                begin = true;
                gl.glColor4f(point.r, point.g, point.b, point.a);
                gl.glVertex3d(point.x, point.y, point.z);
            }
        }
        
        gl.glEnd();		
    }
    
    
    /**
     * Renders all data passed by a polygon styler
     */
    public void visit(LabelStyler styler)
    {
        LabelGraphic label = styler.label;
        styler.reset();        
        
        while (styler.nextBlock())
        {
            while (styler.nextPoint())
            {                
                glu.gluProject(label.x, label.y, label.z, modelM, 0, projM, 0, viewPort, 0, coords, 0);
                gl.glRasterPos2d(coords[0], coords[1]);
                gl.glColor4f(label.r, label.g, label.b, label.a);
                glut.glutBitmapString(GLUT.BITMAP_9_BY_15, label.text);
                
                // skip 50 points
                for (int i=0; i<50; i++)
                    styler.nextPoint();
            }
        }
    }


    /**
     * Renders all data passed by a raster styler
     */
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
                gl.glGenTextures(1, names, 0);
                textureTable.put(styler, names[0]);
                textureNum = names[0];
            }        
            
            // set current texture in GL
            gl.glBindTexture(GL_TEXTURE_TARGET, textureNum);
            
            // get first image
            RasterImageGraphic image = styler.getImage(tileNum);
            
            // reload the texture if needed
            if (image.updated)
            {
                //gl.glTexImage2D(GL_TEXTURE_TARGET, 0, GL.GL_RGB, image.width, image.height, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, (byte[])image.data);
                gl.glTexParameteri(GL_TEXTURE_TARGET, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
                gl.glTexParameteri(GL_TEXTURE_TARGET, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
                image.updated = false;
            }
            
            // setup white background color and offsets
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            gl.glPolygonOffset(1.0f, 1.0f);
            gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
            gl.glEnable(GL.GL_BLEND);
            gl.glDisable(GL.GL_CULL_FACE);
            
            RasterGridGraphic gridPatch = styler.getGrid(tileNum);
            GridRowGraphic row1 = null;
            GridRowGraphic row2 = null;
            
            // loop through all rows
            while (styler.nextBlock())
            {
                if (row1 == null)
                    row1 = styler.nextGridRow();
                else
                    row1 = row2;
                
                row2 = styler.nextGridRow();
                
                // loop and draw quads
                gl.glBegin(GL.GL_QUAD_STRIP);
                for (int i=0; i<gridPatch.width; i++)
                {
                    GridPointGraphic point2 = row2.gridPoints[i];                    
                    gl.glColor4f(point2.r, point2.g, point2.b, point2.a);
                    gl.glTexCoord2f(point2.texX, point2.texY);
                    gl.glVertex3d(point2.x, point2.y, point2.z);
                    
                    GridPointGraphic point1 = row1.gridPoints[i];                    
                    gl.glColor4f(point1.r, point1.g, point1.b, point1.a);
                    gl.glTexCoord2f(point1.texX, point1.texY);
                    gl.glVertex3d(point1.x, point1.y, point1.z);
                }
                gl.glEnd();
            }
        }
        
        // back to no texture for next renderings
        gl.glBindTexture(GL_TEXTURE_TARGET, 0);
    }
}
