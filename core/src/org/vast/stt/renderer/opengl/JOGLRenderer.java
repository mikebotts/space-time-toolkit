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

import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GLDrawableFactory;
//import javax.media.opengl.DebugGL;
//import javax.media.opengl.TraceGL;
import com.sun.opengl.util.GLUT;
import org.eclipse.swt.graphics.Rectangle;
import org.vast.math.Vector3d;
import org.vast.ows.sld.Color;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.project.DataStyler;
import org.vast.stt.project.SceneItem;
import org.vast.stt.project.ViewSettings;
import org.vast.stt.renderer.Renderer;
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
    protected final static ArrayList<GLContext> contextList = new ArrayList<GLContext>(2);
    protected org.eclipse.swt.opengl.GLContext SWTContext;
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
    protected DisplayListManager displayListManager;
    protected GLBlockFilter blockFilter;
    protected float zBufferOffset;
    protected float oldZBufferOffset;
    protected boolean resetZOffset;

    protected GLRenderPoints pointRenderer;
    protected GLRenderLines lineRenderer;
    protected GLRenderPolygons polygonRenderer;
    protected GLRenderGrids gridRenderer;
    protected GLRenderGridBorder gridBorderRenderer;
    protected GLRenderTexture textureRenderer;
    

    public JOGLRenderer()
    {

    }
    
    
    @Override
    public void cleanup(DataStyler styler, CleanupSection section)
    {
        switch (section)
        {
            case ALL:
                textureManager.clearTextures(styler);
                displayListManager.clearDisplayLists(styler);
                break;
                
            case TEXTURES:
                textureManager.clearTextures(styler);
                break;
                
            case GEOMETRY:
                displayListManager.clearDisplayLists(styler);
                break;
        }
    }
    
    
    @Override
    public void cleanup(DataStyler styler, Object[] objects, CleanupSection section)
    {
        switch (section)
        {
            case ALL:
                textureManager.clearTextures(styler, objects);
                displayListManager.clearDisplayLists(styler, objects);
                break;
                
            case TEXTURES:
                textureManager.clearTextures(styler, objects);
                break;
                
            case GEOMETRY:
                displayListManager.clearDisplayLists(styler, objects);
                break;
        }
    }
    
    
    @Override
    protected void setupView()
    {
        ViewSettings view = scene.getViewSettings();
        
        // clear back buffer
        Color backColor = view.getBackgroundColor();
        gl.glClearColor(backColor.getRedValue(), backColor.getGreenValue(), backColor.getBlueValue(), 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

        // set up projection
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        Rectangle clientArea = canvas.getClientArea();
        float width = (float) view.getOrthoWidth();
        float height = width * clientArea.height / clientArea.width;
        float farClip = (float) view.getFarClip();
        float nearClip = (float) view.getNearClip();
        gl.glOrtho(-width / 2.0f, width / 2.0f, -height / 2.0f, height / 2.0f, nearClip, farClip);
        
        // set up 3D camera position from ViewSettings
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        Vector3d eye = view.getCameraPos();
        Vector3d center = view.getTargetPos();
        Vector3d up = view.getUpDirection();
        glu.gluLookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z);

        // save projection matrices
        gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelM, 0);
        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projM, 0);
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewPort, 0);
        
        // draw camera target if requested
        if (view.isShowCameraTarget())
            this.drawCameraTarget();
        
        zBufferOffset = 100.0f;
    }
    
    
    @Override
    public void resizeView(int width, int height)
    {
        SWTContext.setCurrent();
        JOGLContext.makeCurrent();
        SWTContext.resize(0, 0, width, height);
        JOGLContext.release();
    }
    
    
    @Override
    public void drawScene()
    {
        SWTContext.setCurrent();
        JOGLContext.makeCurrent();
        super.drawScene();
        JOGLContext.release();
    }
    
    
    @Override
    protected void drawItem(SceneItem sceneItem)
    {
        resetZOffset = false;
        sceneItem.accept(this);
    }
    
    
    protected void drawCameraTarget()
    {
        ViewSettings view = scene.getViewSettings();
        double x = view.getTargetPos().x;
        double y = view.getTargetPos().y;
        double z = view.getTargetPos().z;
        gl.glPushMatrix();
        gl.glTranslated(x, y, z);
        
        double axisLength = canvas.getSize().x / canvas.getSize().y * 30; 
        
        gl.glLineWidth(2.0f);
        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3d(0.0, 0.0, 0.0);
        gl.glVertex3d(axisLength, 0.0, 0.0);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3d(0.0, 0.0, 0.0);
        gl.glVertex3d(0.0, axisLength, 0.0);
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3d(0.0, 0.0, 0.0);
        gl.glVertex3d(0.0, 0.0, axisLength);
        gl.glEnd();
        
        gl.glPopMatrix();
    }


    @Override
    public void project(double worldX, double worldY, double worldZ, Vector3d viewPos)
    {
        glu.gluProject(worldX, worldY, worldZ, modelM, 0, projM, 0, viewPort, 0, coords, 0);
        viewPos.set(coords[0], coords[1], coords[2]);      
    }


    @Override
    public void unproject(double viewX, double viewY, double viewZ, Vector3d worldPos)
    {
        glu.gluUnProject(viewX, viewY, viewZ, modelM, 0, projM, 0, viewPort, 0, coords, 0);
        worldPos.set(coords[0], coords[1], coords[2]);
    }


    @Override
    protected void swapBuffers()
    {
        SWTContext.swapBuffers();
    }


    @Override
    public void init()
    {
        SWTContext = new org.eclipse.swt.opengl.GLContext(canvas);
        SWTContext.setCurrent();
        //JOGLContext = GLDrawableFactory.getFactory().createExternalGLContext();
        GLDrawable drawable = GLDrawableFactory.getFactory().createExternalGLDrawable();
        
        // associate to a context already created
        if (contextList.isEmpty())
            JOGLContext = drawable.createContext(null);
        else
            JOGLContext = drawable.createContext(contextList.get(0));
        contextList.add(JOGLContext);
        
        JOGLContext.makeCurrent();
        
        //JOGLContext.setGL(new DebugGL(JOGLContext.getGL()));
        //JOGLContext.setGL(new TraceGL(JOGLContext.getGL(), System.err));

        gl = JOGLContext.getGL();
        glu = new GLU();
        glut = new GLUT();
        textureManager = new TextureManager(gl, glu);
        displayListManager = new DisplayListManager(gl, glu);
        blockFilter = new GLBlockFilter(gl, glu);
        pointRenderer = new GLRenderPoints(gl, glu);
        lineRenderer = new GLRenderLines(gl, glu);
        polygonRenderer = new GLRenderPolygons(gl, glu);
        gridRenderer = new GLRenderGrids(gl, glu);
        gridBorderRenderer = new GLRenderGridBorder(gl, glu);
        textureRenderer = new GLRenderTexture(gl, glu);        
        
        gl.glClearDepth(1.0f);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        // set up a light
//        gl.glEnable(GL.GL_LIGHTING);
//        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, FloatBuffer.wrap(new float[] {0.5f, 0.5f, 0.5f, 1.0f}));
//        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] {0.5f, 0.5f, 0.5f, 1.0f}));
//        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, FloatBuffer.wrap(new float[] {0.0f, 0.0f, -2.0f, 1.0f}));
//        gl.glEnable(GL.GL_LIGHT1);
        
        JOGLContext.release();
    }


    @Override
    public void dispose()
    {
        if (JOGLContext != null && SWTContext != null)
        {
            // dispose context and remove from list
            contextList.remove(JOGLContext);            
            SWTContext.setCurrent();
            JOGLContext.makeCurrent();
            JOGLContext.release();
            SWTContext.dispose();
            JOGLContext.destroy();
            SWTContext = null;
            JOGLContext = null;
        }
    }
    

    /**
     * Renders all data passed by a point styler
     */
    public void visit(PointStyler styler)
    {
        BlockListItem block;
        styler.resetIterators();        
        pointRenderer.setStyler(styler);
        
        // loop through all tiles
        while ((block = styler.nextBlock()) != null)
        { 
            pointRenderer.blockCount = 10000;
            displayListManager.useDisplayList(styler, block, pointRenderer);
        }      
    }
    
    
    /**
     * Renders all data passed by a line styler
     */
    public void visit(LineStyler styler)
    {
        LineSegmentGraphic segment;
        styler.resetIterators();        
        lineRenderer.setStyler(styler);
        
        // loop through all tiles
        while ((segment = styler.nextLineBlock()) != null)
        { 
            lineRenderer.blockCount = 10000;
            displayListManager.useDisplayList(styler, segment.block, lineRenderer);
        }
    }


    /**
     * Renders all data passed by a polygon styler
     */
    public void visit(PolygonStyler styler)
    {
        BlockListItem block;
        styler.resetIterators();        
        polygonRenderer.setStyler(styler);
        
        // loop through all tiles
        while ((block = styler.nextBlock()) != null)
        { 
            gl.glPolygonOffset(0.0f, zBufferOffset*100);
            polygonRenderer.blockCount = 1;
            displayListManager.useDisplayList(styler, block, polygonRenderer);
            updateZBufferOffset();
        }    
    }


    /**
     * Renders all data passed by a label styler
     */
    public void visit(LabelStyler styler)
    {
        LabelGraphic label;
        styler.resetIterators();
        int minDist = 100;
        byte[] buf = new byte[1];
        buf[0] = (byte) 0x01;

        gl.glEnable(GL.GL_STENCIL_TEST);

        while (styler.nextBlock() != null)
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
     * Renders all data passed by a grid border styler
     */
    public void visit(GridBorderStyler styler)
    {
        GridPatchGraphic patch;
        styler.resetIterators();
        gridBorderRenderer.setStyler(styler);
        
        // loop through all tiles
        while ((patch = styler.nextPatch()) != null)
        {           
            gridBorderRenderer.blockCount = 1;
            gridBorderRenderer.patch = patch;
            displayListManager.useDisplayList(styler, patch.block, gridBorderRenderer);
        }
    }
    
    
    protected void updateZBufferOffset()
    {
        if (zBufferOffset > 1.0f)
            zBufferOffset -= 1.0f; 
    }
    
    
    /**
     * Renders all data passed by a grid mesh styler
     */
    public void visit(GridMeshStyler styler)
    {
        GridPatchGraphic patch;
        styler.resetIterators();
        gridRenderer.setStyler(styler);
        
        if (resetZOffset)
            zBufferOffset = oldZBufferOffset;            
        
        oldZBufferOffset = zBufferOffset;
        resetZOffset = true;
        
        // loop through all tiles
        while ((patch = styler.nextPatch()) != null)
        {           
            gl.glPolygonOffset(0.0f, zBufferOffset*100 - 10);
            gridRenderer.blockCount = 1;
            gridRenderer.patch = patch;
            displayListManager.useDisplayList(styler, patch.block, gridRenderer);
            updateZBufferOffset();
        }
    }
    
    
    /**
     * Renders all data passed by a grid fill styler
     */
    public void visit(GridFillStyler styler)
    {
        GridPatchGraphic patch;
        styler.resetIterators();
        gridRenderer.setStyler(styler);
        
        if (resetZOffset)
            zBufferOffset = oldZBufferOffset;
        
        oldZBufferOffset = zBufferOffset;
        resetZOffset = true;
        
        // loop through all tiles
        while ((patch = styler.nextPatch()) != null)
        {           
            gl.glPolygonOffset(0.5f, zBufferOffset*100);
            gridRenderer.blockCount = 1;
            gridRenderer.patch = patch;
            displayListManager.useDisplayList(styler, patch.block, gridRenderer);
            updateZBufferOffset();
        }
    }


    /**
     * Renders all data passed by a texture mapping styler
     */
    public void visit(TextureStyler styler)
    {
        TexturePatchGraphic patch;        
        styler.resetIterators();
        textureRenderer.setStyler(styler);

        if (resetZOffset)
            zBufferOffset = oldZBufferOffset;
        
        oldZBufferOffset = zBufferOffset;
        resetZOffset = true;
        
        // loop through all tiles
        while ((patch = styler.nextTile()) != null)
        {
            // bind texture and load in GL if needed
            textureManager.useTexture(styler, patch.getTexture());            
            gl.glPolygonOffset(0.5f, zBufferOffset*100);
            textureRenderer.patch = patch;
            textureRenderer.blockCount = 1;
            displayListManager.useDisplayList(styler, patch.getGrid().block, textureRenderer);
            updateZBufferOffset();
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
