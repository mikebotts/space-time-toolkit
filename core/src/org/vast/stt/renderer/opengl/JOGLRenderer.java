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
    protected GLContext SWTContext;
    protected javax.media.opengl.GLContext JOGLContext;
    protected GL gl;
    protected GLU glu;
    protected GLUT glut;
    protected int[] viewPort = new int[4];
    protected double[] modelM = new double[16];
    protected double[] projM = new double[16];
    protected double[] coords = new double[3];
    protected int[] boolResult = new int[1];
    protected TextureManager textureManager;


    public JOGLRenderer()
    {
        
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
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

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
        textureManager = new TextureManager(gl, glu);
        
        // find out which texture 2D target to use
        String glExtensions = gl.glGetString(GL.GL_EXTENSIONS);
        if (glu.gluCheckExtension("GL_ARB_texture_rectangle", glExtensions) ||
            glu.gluCheckExtension("GL_EXT_texture_rectangle", glExtensions))
        {
            OpenGLCaps.TEXTURE_2D_TARGET = GL.GL_TEXTURE_RECTANGLE_EXT;
            System.err.println("--> NPOT textures supported <--");
        }
        else
        {
            OpenGLCaps.TEXTURE_2D_TARGET = GL.GL_TEXTURE_2D;
            System.err.println("--> NPOT textures NOT supported <--");
        }

        gl.glClearDepth(1.0f);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(OpenGLCaps.TEXTURE_2D_TARGET);

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
        LinePointGraphic point;
        boolean begin = false;
        float oldWidth = -1.0f;
        styler.reset();

        // loop and draw all points
        while (styler.nextBlock())
        {
            while ((point = styler.nextPoint()) != null)
            {
                if (!begin)
                {
                    // enable line smooth if needed        
                    if (point.smooth)
                        gl.glEnable(GL.GL_LINE_SMOOTH);
                    else
                        gl.glDisable(GL.GL_LINE_SMOOTH);
                }
                
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
        
        if (begin)
            gl.glEnd();
    }


    /**
     * Renders all data passed by a point styler
     */
    public void visit(PointStyler styler)
    {
        PointGraphic point;
        boolean begin = false;
        float oldSize = -1.0f;
        styler.reset();

        // loop and draw all points
        while (styler.nextBlock())
        {
            while ((point = styler.nextPoint()) != null)
            {
                if (point.size != oldSize)
                {
                    if (begin)
                        gl.glEnd();
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
        PolygonPointGraphic point;
        boolean begin = false;
        styler.reset();

        // setup polygon offset
        gl.glPolygonOffset(1.0f, 1.0f);
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);

        // loop and draw all points
        gl.glBegin(GL.GL_POLYGON);

        while (styler.nextBlock())
        {
            while ((point = styler.nextPoint()) != null)
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
     * Renders all data passed by a label styler
     */
    public void visit(LabelStyler styler)
    {
        LabelGraphic label;
        styler.reset();
        int minDist = 100;
        byte[] buf = new byte[1];
        buf[0] = (byte) 0x01;

        gl.glEnable(GL.GL_STENCIL_TEST);

        while (styler.nextBlock())
        {
            while ((label = styler.nextPoint()) != null)
            {
                double x1 = label.x;
                double y1 = label.y;
                double z1 = label.z;

                // get projected coordinates
                glu.gluProject(x1, y1, z1, modelM, 0, projM, 0, viewPort, 0, coords, 0);
                double xw1 = coords[0];
                double yw1 = coords[1];

                // get the first far enough point
                while ((label = styler.nextPoint()) != null)
                {
                    double x2 = label.x;
                    double y2 = label.y;
                    double z2 = label.z;

                    // get projected coordinates                    
                    glu.gluProject(x2, y2, z2, modelM, 0, projM, 0, viewPort, 0, coords, 0);
                    double xw2 = coords[0];
                    double yw2 = coords[1];

                    // compute distance between two points
                    double dx = Math.abs(xw1 - xw2);
                    double dy = Math.abs(yw1 - yw2);
                    double dist = dx + dy;

                    // print label only if points are more than minDist pixels appart
                    if (dist > minDist)
                    {
                        gl.glColor4f(label.r, label.g, label.b, label.a);
                        gl.glWindowPos2d((xw1 + xw2) / 2 + label.offsetX, (yw1 + yw2) / 2 + label.offsetY);

                        gl.glGetIntegerv(GL.GL_CURRENT_RASTER_POSITION_VALID, boolResult, 0);
                        if (boolResult[0] != 0)
                        {
                            // draw label
//                            gl.glStencilFunc(GL.GL_EQUAL, 0x0, 0x1);
//                            gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
                            glut.glutBitmapString(GLUT.BITMAP_8_BY_13, label.text);

                            // draw mask
//                            int length = glut.glutBitmapLength(GLUT.BITMAP_8_BY_13, label.text);
//                            gl.glViewport(viewPort[0] + label.offsetX + length / 2 - 50, viewPort[1] + label.offsetY - 44, viewPort[2], viewPort[3]);
//                            gl.glRasterPos3d(label.x, label.y, label.z);
//                            gl.glPixelZoom(100, 100);
//                            gl.glStencilFunc(GL.GL_ALWAYS, 0x1, 0x1);
//                            gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
//                            gl.glDrawPixels(1, 1, GL.GL_STENCIL_INDEX, GL.GL_BYTE, ByteBuffer.wrap(buf));

                            break;
                        }
                    }
                }
            }
        }

        gl.glDisable(GL.GL_STENCIL_TEST);
    }


    /**
     * Renders all data passed by a texture mapping styler
     */
    public void visit(TextureMappingStyler styler)
    {
        TexturePatchInfo patch;
        GridPointGraphic point;
        styler.reset();
        textureManager.setStyler(styler);

        // loop through all tiles
        while ((patch = styler.nextTile()) != null)
        {
            RasterTileGraphic tex = patch.getTexture();
            GridPatchGraphic grid = patch.getGrid();

            // bind texture and load in GL if needed
            textureManager.bindTexture(tex);

            if (grid.fill)
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            else
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            
            gl.glPolygonOffset(1.0f, 1.0f);
            gl.glEnable(GL.GL_BLEND);
            gl.glDisable(GL.GL_CULL_FACE);
            gl.glTexParameteri(OpenGLCaps.TEXTURE_2D_TARGET, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(OpenGLCaps.TEXTURE_2D_TARGET, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameterf(OpenGLCaps.TEXTURE_2D_TARGET, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            
            gl.glColor4f(1.0f, 1.0f, 1.0f, tex.opacity);
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            
            for (int y = 0; y < grid.length-1; y++)
            {
                for (int x = 0; x < grid.width; x++)
                {
                    point = styler.getGridPoint(x, y, false);
                    gl.glTexCoord2f(point.tx, point.ty);
                    gl.glVertex3d(point.x, point.y, point.z);
                    
                    point = styler.getGridPoint(x, y+1, false);
                    gl.glTexCoord2f(point.tx, point.ty);
                    gl.glVertex3d(point.x, point.y, point.z);
                }
                
                if (y < grid.length-2)
                {
                    gl.glEnd();
                    gl.glBegin(GL.GL_TRIANGLE_STRIP);
                }                
            }
            
            gl.glEnd();
        }
        
        // reload the void texture
        gl.glBindTexture(OpenGLCaps.TEXTURE_2D_TARGET, 0);
    }


    /**
     * Renders all data passed by a raster styler
     */
    public void visit(RasterStyler styler)
    {

    }
}
