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

import org.eclipse.swt.opengl.GL;
import org.eclipse.swt.opengl.GLU;
import org.eclipse.swt.opengl.GLContext;
import org.vast.math.Vector3D;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.ScalarParameter;
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
	private int [] viewPort = new int[4];
	private double [] modelM = new double[16];
	private double [] projM = new double[16];
	private double [] xData = new double[1];
	private double [] yData = new double[1];
	private double [] zData = new double[1];
	
	public OpenGLRenderer()
	{
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
		GL.glClearColor(backColor.getRed(), backColor.getGreen(), backColor.getBlue(), 0.0f);
		GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// set up projection
		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glLoadIdentity();
		float width = (float)view.getOrthoWidth();
		float height = width * view.getViewHeight() / view.getViewWidth();
		float farClip = (float)view.getFarClip();
		float nearClip = (float)view.getNearClip();
		GL.glOrtho(-width/2.0f, width/2.0f, -height/2.0f, height/2.0f, nearClip, farClip);
		
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
		GL.glDepthFunc(GL.GL_LESS);
		GL.glEnable(GL.GL_DEPTH_TEST);
		GL.glShadeModel(GL.GL_SMOOTH);
		GL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
		//GL.glEnable(GL.GL_LINE_SMOOTH);
		//GL.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		GL.glEnable(GL.GL_BLEND);
		GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
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
		ScalarParameter widthSP = styler.getSymbolizer().getStroke().getWidth();
		if(widthSP != null){
			Integer lineWidth = (Integer)widthSP.getConstantValue();
			//lineWidth
			GL.glLineWidth((float)lineWidth.intValue());
		}
		ScalarParameter colorSP = styler.getSymbolizer().getStroke().getColor();
		if(colorSP != null){
			//  need alpha support here
			Color color = (Color)colorSP.getConstantValue();
			GL.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		} else
			GL.glColor4d(1.0, 0.0, 0.0, 0.0);
		for (int i=0; i<styler.getSegmentCount(); i++)
		{
			LineGraphic segment = styler.getSegment(i);
			
			GL.glBegin(GL.GL_LINE_STRIP);
			
			for (int j=0; j<segment.segmentSize; j++)
			{
				PointGraphic point = styler.getPoint(j);
				//GL.glColor4f(point.r, point.g, point.b, point.a);
				GL.glVertex3d(point.x, point.y, point.z);
			}
			
			GL.glEnd();			
		}	
	}


	public void visit(PointStyler styler)
	{
		GL.glPointSize(2.0f);
		GL.glBegin(GL.GL_POINTS);
		
		for (int i=0; i<styler.getPointCount(); i++)
		{
			PointGraphic point = styler.getPoint(i);
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
		// TODO Auto-generated method stub		
	}
}
