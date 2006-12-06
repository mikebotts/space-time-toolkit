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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.GLDrawableFactory;
//import javax.media.opengl.DebugGL;
//import javax.media.opengl.TraceGL;
import com.sun.opengl.util.GLUT;
import org.eclipse.swt.graphics.Rectangle;
import org.vast.math.Vector3d;
import org.vast.ows.sld.Color;
import org.vast.stt.data.BlockListItem;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.project.world.WorldSceneItem;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.renderer.PickFilter;
import org.vast.stt.renderer.PickedObject;
import org.vast.stt.renderer.PopupRenderer;
import org.vast.stt.renderer.SceneRenderer;
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
public class JOGLRenderer extends SceneRenderer<Scene<WorldSceneItem>> implements StylerVisitor
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
    protected boolean contextInUse;
    protected Hashtable<Integer, SceneItem> selectableItems;
    protected PopupRenderer popupRenderer;

    protected GLRenderPoints pointRenderer;
    protected GLRenderIcons iconRenderer;
    protected GLRenderLines lineRenderer;
    protected GLRenderPolygons polygonRenderer;
    protected GLRenderGrids gridRenderer;
    protected GLRenderGridBorder gridBorderRenderer;
    protected GLRenderTexture textureRenderer;
    protected GLRenderBBOX bboxRenderer;
    

    public JOGLRenderer()
    {
        selectableItems = new Hashtable<Integer, SceneItem>();        
    }
    
    
    protected synchronized void getContext()
    {
        if (javax.media.opengl.GLContext.getCurrent() == JOGLContext)
            return;
        
        try
        {
            while (contextInUse)
                wait();
            
            contextInUse = true;
            SWTContext.setCurrent();
            JOGLContext.makeCurrent();
        }
        catch (InterruptedException e)
        {
        }
    }
    
    
    protected synchronized void releaseContext()
    {
        JOGLContext.release();
        contextInUse = false;
        notifyAll();
    }
    
    
    @Override
    public void cleanup(DataStyler styler, CleanupSection section)
    {
        getContext();
        
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
        
        releaseContext();
    }
    
    
    @Override
    public void cleanup(DataStyler styler, Object[] objects, CleanupSection section)
    {
        getContext();
        
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
        
        releaseContext();
    }
    
    
    @Override
    public void setupView(ViewSettings view)
    {
        getContext();
        setupMatrices(view);
        releaseContext();
    }
    
    
    protected void setupMatrices(ViewSettings view)
    {
        // clear back buffer
        Color backColor = view.getBackgroundColor();
        gl.glClearColor(backColor.getRedValue(), backColor.getGreenValue(), backColor.getBlueValue(), 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

        // set up projection
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        Rectangle clientArea = composite.getClientArea();
        float width = (float) view.getOrthoWidth();
        float height = (float) view.getOrthoWidth() * clientArea.height / clientArea.width;
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
        
        zBufferOffset = 100.0f;
    }
    
    
    @Override
    public void resizeView(int width, int height)
    {
        super.resizeView(width, height);
        getContext();
        SWTContext.resize(0, 0, width, height);
        releaseContext();
    }
    
    
    @Override
    public PickedObject pick(Scene<WorldSceneItem> sc, PickFilter filter)
    {
        WorldScene scene = (WorldScene)sc;
        ViewSettings view = scene.getViewSettings();
                
        getContext();
        
        // prepare selection buffer and switch to GL_SELECT mode
        ByteBuffer buffer = ByteBuffer.allocateDirect(4*5);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer selectBuffer = buffer.asIntBuffer();
        gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);
        gl.glRenderMode(GL.GL_SELECT);        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
        
        // set up projection
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPickMatrix(filter.x, filter.y, filter.dX, filter.dY, viewPort, 0);
        Rectangle clientArea = composite.getClientArea();
        float width = (float) view.getOrthoWidth();
        float height = (float) view.getOrthoWidth() * clientArea.height / clientArea.width;
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
        
        // init name stack
        gl.glInitNames();
        gl.glPushName(0);
        
        // draw pickable items
        selectableItems.clear();
        
        if (filter.onlyBoundingBox && !scene.getSelectedItems().isEmpty())
        {
            SceneItem selectedItem = scene.getSelectedItems().get(0);
            selectableItems.put(selectedItem.hashCode(), selectedItem);
            gl.glLoadName(selectedItem.hashCode());
            this.drawROI(scene, true);
        }
        else
        {
            // get all or only selected items
            List<WorldSceneItem> items = null;
            if (filter.onlySelectedItems)
                items = scene.getSelectedItems();
            else
                items = scene.getSceneItems();
            
            // loop through all items and render them in selection mode
            for (int i = 0; i < items.size(); i++)
            {
                SceneItem<?> nextItem = items.get(i);
    
                if (!nextItem.isVisible())
                    continue;
                
                if (!nextItem.getDataItem().isEnabled())
                    continue;
                
                if (filter.onlyWithEvent && !nextItem.getDataItem().hasEvent())
                    continue;
                
                selectableItems.put(nextItem.hashCode(), nextItem);
                drawOneItem(nextItem);
            }
        }
        
        gl.glRenderMode(GL.GL_RENDER);
        releaseContext();
        
        // read selection buffer
        SceneItem selectedItem = selectableItems.get(selectBuffer.get(3));
        if (selectedItem != null)
        {
            //System.out.println(selectedItem.getName());
            PickedObject pickedItem = new PickedObject();
            pickedItem.item = selectedItem;
            
            int nameCount = selectBuffer.get(0)-1;
            pickedItem.indices = new int[nameCount];
            
            for (int i=0; i<nameCount; i++)
            {
                int name = selectBuffer.get(4 + i);
                pickedItem.indices[i] = name;
            }
            
            return pickedItem;
//          popupRenderer.showPopup((int)x, (int)y, selectedItem);
        }
        
        return null;
    }
    
    
    @Override
    public void drawScene(Scene<WorldSceneItem> sc)
    {
        if (composite.isDisposed())
            return;
        
        getContext();
        WorldScene scene = (WorldScene)sc;
        ViewSettings view = scene.getViewSettings();
        setupMatrices(view);
        
        // draw all items
        List<WorldSceneItem> sceneItems = scene.getSceneItems();
        for (int i = 0; i < sceneItems.size(); i++)
        {
            SceneItem<?> nextItem = sceneItems.get(i);

            if (!nextItem.isVisible())
                continue;
            
            if (!nextItem.getDataItem().isEnabled())
                continue;

            try {drawOneItem(nextItem);}
            catch (RuntimeException e) {e.printStackTrace();}
        }

        // draw camera target if enabled
        if (view.isShowCameraTarget())
            this.drawCameraTarget(view);
        
        // draw camera target if enabled
        if (view.isShowArcball())
            this.drawArcball(view);
        
        // draw camera target if enabled
        if (view.isShowItemROI())
            this.drawROI(scene, false);
        
        // swap buffers        
        SWTContext.swapBuffers();
        releaseContext();
    }
       
    
    @Override
    public void drawItem(SceneItem<?> sceneItem)
    {
        getContext();
        drawOneItem(sceneItem);
        releaseContext();
    }
    
    
    protected void drawOneItem(SceneItem sceneItem)
    {
        resetZOffset = false;
        gl.glLoadName(sceneItem.hashCode());
        sceneItem.accept(this);
    }
    
    
    /**
     * Draws the tripod representing the camera target
     * @param view
     */
    protected void drawCameraTarget(ViewSettings view)
    {
        double x = view.getTargetPos().x;
        double y = view.getTargetPos().y;
        double z = view.getTargetPos().z;
        
        //gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
        //gl.glBlendFunc(GL.GL_ONE_MINUS_DST_ALPHA, GL.GL_DST_ALPHA);
        
        gl.glPushMatrix();
        gl.glTranslated(x, y, z);
        
        double axisLength = view.getOrthoWidth() / this.getViewWidth() * 30; 
        
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
        //gl.glPopAttrib();
    }
    
    
    /**
     * Draws circles symbolizing the Arcball size and orientation
     * @param view
     */
    protected void drawArcball(ViewSettings view)
    {
        gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT);
        gl.glDepthFunc(GL.GL_ALWAYS);
        
        double x = view.getTargetPos().x;
        double y = view.getTargetPos().y;
        double z = view.getTargetPos().z;
        
        gl.glPushMatrix();
        gl.glTranslated(x, y, z);
        
        double radius = view.getArcballRadius();

        gl.glPolygonOffset(0.0f, -10);
        gl.glColor4f(0.8f, 1.0f, 0.8f, 0.1f);
        gl.glLineWidth(2.0f);
        gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
        gl.glEnable(GL.GL_CULL_FACE);
        GLUquadric sphereObj = glu.gluNewQuadric();
        glu.gluSphere(sphereObj, radius, 24, 24);
        
        gl.glPopMatrix();
        gl.glPopAttrib();
    }
    
    
    /**
     * Draws a surface representing the ROI of the currently selected item
     * @param scene
     */
    protected void drawROI(WorldScene scene, boolean onlyHandles)
    {
        if (scene.getSelectedItems().isEmpty())
            return;
        
        SceneItem<?> sceneItem = scene.getSelectedItems().get(0);
        DataProvider provider = sceneItem.getDataItem().getDataProvider();
        
        if (provider.isSpatialSubsetSupported())
        {        
            STTSpatialExtent extent = provider.getSpatialExtent();
            
            if (!extent.isNull() && extent.getUpdater() == null)
            {
                gl.glLoadName(sceneItem.hashCode());
                bboxRenderer.drawROI(scene, extent, onlyHandles);
            }
        }
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
    public void init()
    {
        SWTContext = new org.eclipse.swt.opengl.GLContext(composite);
        SWTContext.setCurrent();
        //JOGLContext = GLDrawableFactory.getFactory().createExternalGLContext();
        GLDrawable drawable = GLDrawableFactory.getFactory().createExternalGLDrawable();
        
        // associate to a context already created
        if (contextList.isEmpty())
            JOGLContext = drawable.createContext(null);
        else
            JOGLContext = drawable.createContext(contextList.get(0));
        contextList.add(JOGLContext);
        
        //JOGLContext.setSynchronized(true);
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
        iconRenderer = new GLRenderIcons(gl, glu, this);
        lineRenderer = new GLRenderLines(gl, glu);
        polygonRenderer = new GLRenderPolygons(gl, glu);
        gridRenderer = new GLRenderGrids(gl, glu);
        gridBorderRenderer = new GLRenderGridBorder(gl, glu);
        textureRenderer = new GLRenderTexture(gl, glu);        
        bboxRenderer = new GLRenderBBOX(this, gl, glu);
        popupRenderer = new PopupRenderer(composite);
        
        gl.glClearDepth(1.0f);
        gl.glDepthFunc(GL.GL_LEQUAL);//GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBl
        
        // set up a light
//        gl.glEnable(GL.GL_LIGHTING);
//        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, FloatBuffer.wrap(new float[] {0.5f, 0.5f, 0.5f, 1.0f}));
//        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, FloatBuffer.wrap(new float[] {0.5f, 0.5f, 0.5f, 1.0f}));
//        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, FloatBuffer.wrap(new float[] {0.0f, 0.0f, -2.0f, 1.0f}));
//        gl.glEnable(GL.GL_LIGHT1);
        
        releaseContext();
    }


    @Override
    public void dispose()
    {
        if (JOGLContext != null && SWTContext != null)
        {
            // dispose context and remove from list
            releaseContext();
            contextList.remove(JOGLContext);
            SWTContext.dispose();
            JOGLContext.destroy();
            SWTContext = null;
            JOGLContext = null;
            DisplayListManager.DLTables.clear();
            TextureManager.symTextureTables.clear();
        }
    }
    

    /**
     * Renders all data passed by a point styler
     */
    public void visit(PointStyler styler)
    {
        BlockListItem block;
        styler.resetIterators();
        
        // select point or icon renderer
        if (styler.useIcons())
        {
            iconRenderer.setStyler(styler);            
            while ((block = styler.nextBlock()) != null)
            { 
                iconRenderer.blockCount = 10000;
                iconRenderer.run();
            }
        }
        else
        {        
            pointRenderer.setStyler(styler);
            while ((block = styler.nextBlock()) != null)
            { 
                pointRenderer.blockCount = 10000;
                displayListManager.useDisplayList(styler, block, pointRenderer, false);
            }
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
            displayListManager.useDisplayList(styler, segment.block, lineRenderer, false);
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
            displayListManager.useDisplayList(styler, block, polygonRenderer, false);
            updateZBufferOffset();
        }    
    }


    /**
     * Renders all data passed by a label styler
     */
    public void visit(LabelStyler styler)
    {
        LabelGraphic label1, label2;
        styler.resetIterators();
        int minDist = 100;
        byte[] buf = new byte[1];
        buf[0] = (byte) 0x01;
        double xw1, yw1, zw1, xw2, yw2;
        
        gl.glEnable(GL.GL_STENCIL_TEST);

        while (styler.nextBlock() != null)
        {
            while ((label1 = styler.nextPoint()) != null)
            {
                double x1 = label1.x;
                double y1 = label1.y;
                double z1 = label1.z;

                // get projected coordinates
                glu.gluProject(x1, y1, z1, modelM, 0, projM, 0, viewPort, 0, coords, 0);
                xw1 = xw2 = coords[0];
                yw1 = yw2 = coords[1];
                zw1 = coords[2];

                // get the first far enough point
                while ((label2 = styler.nextPoint()) != null)
                {
                    double x2 = label2.x;
                    double y2 = label2.y;
                    double z2 = label2.z;

                    // get projected coordinates                    
                    glu.gluProject(x2, y2, z2, modelM, 0, projM, 0, viewPort, 0, coords, 0);
                    xw2 = coords[0];
                    yw2 = coords[1];

                    // compute distance between two points
                    double dx = Math.abs(xw1 - xw2);
                    double dy = Math.abs(yw1 - yw2);
                    double dist = dx + dy;
                    
                    // get out only if points are more than minDist pixels appart
                    if (dist > minDist)
                        break;
                }
                
                // print label
                gl.glColor4f(label1.r, label1.g, label1.b, label1.a);
                gl.glWindowPos3d((xw1 + xw2) / 2 + label1.offsetX, (yw1 + yw2) / 2 + label1.offsetY, zw1);

                gl.glGetIntegerv(GL.GL_CURRENT_RASTER_POSITION_VALID, boolResult, 0);
                if (boolResult[0] != 0)
                {
                    // draw label
//                    gl.glStencilFunc(GL.GL_EQUAL, 0x0, 0x1);
//                    gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
                    gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT);
                    gl.glDepthFunc(GL.GL_ALWAYS);
                    glut.glutBitmapString(GLUT.BITMAP_8_BY_13, label1.text);
                    gl.glPopAttrib();

                    // draw mask
//                    int length = glut.glutBitmapLength(GLUT.BITMAP_8_BY_13, label.text);
//                    gl.glViewport(viewPort[0] + label.offsetX + length / 2 - 50, viewPort[1] + label.offsetY - 44, viewPort[2], viewPort[3]);
//                    gl.glRasterPos3d(label.x, label.y, label.z);
//                    gl.glPixelZoom(100, 100);
//                    gl.glStencilFunc(GL.GL_ALWAYS, 0x1, 0x1);
//                    gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
//                    gl.glDrawPixels(1, 1, GL.GL_STENCIL_INDEX, GL.GL_BYTE, ByteBuffer.wrap(buf));

                    break;
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
            displayListManager.useDisplayList(styler, patch.block, gridBorderRenderer, patch.updated);
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
            displayListManager.useDisplayList(styler, patch.block, gridRenderer, patch.updated);          
        }
        
        updateZBufferOffset();
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
            displayListManager.useDisplayList(styler, patch.block, gridRenderer, patch.updated);
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
            textureManager.useTexture(styler, patch.getTexture(), patch.updated);            
            gl.glPolygonOffset(0.5f, zBufferOffset*100);
            textureRenderer.patch = patch;
            textureRenderer.blockCount = 1;
            textureRenderer.normalizeCoords = textureManager.isNormalizationRequired();
            displayListManager.useDisplayList(styler, patch.getGrid().block, textureRenderer, patch.updated);
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
