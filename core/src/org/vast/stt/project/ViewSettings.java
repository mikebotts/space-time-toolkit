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

package org.vast.stt.project;

import org.vast.math.*;
import org.vast.ows.sld.Color;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;


/**
 * <p><b>Title:</b><br/>
 * SceneViewSettings3D
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Contains parameters for a 3D view
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public class ViewSettings implements STTEventProducer
{
    public enum MotionConstraint
    {
        NO_MOTION,
        X, Y, Z,
        XY, YZ, XZ,
        XYZ
    }
    
    public enum CameraMode
    {
        ORTHO,
        PERPECTIVE
    }
    
    // basic parameters
	protected Color backgroundColor;
	protected boolean alphaBlendingEnabled = true;
	
	// parameters defining camera position and orientation
	protected Vector3d cameraPos = new Vector3d();
	protected Vector3d targetPos = new Vector3d();
	protected Vector3d upDirection = new Vector3d();

	// parameters defining perspective field of view
	protected double cameraFov;

	// parameters defining ortho field of view
	protected double orthoWidth, orthoHeight;
    
    // parameters containing view dimensions in pixels
    protected int viewWidth, viewHeight;

	// camera modes
	protected CameraMode cameraMode;
    
    // intended view projection
    protected Projection projection;

	// zDepthFudgeFactor provides support for stopping pixel fighting
	// between objects with similar z-depth values; the use of this factor
	// is dependent on the concrete class to implement
	protected int zDepthFudgeFactor;

	// clipping planes
	protected double nearClip, farClip;
    
    // camera motion contraints
    protected MotionConstraint transConstraint;
    protected MotionConstraint rotConstraint;
    protected MotionConstraint zoomConstraint;
    
    // other options
    protected boolean showCameraTarget = true;
    
    protected STTEventListeners listeners;

	
	public ViewSettings()
	{
        backgroundColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        
        cameraPos.set(0.0, 0.0, 100.0);
		targetPos.set(0.0, 0.0, 0.0);
		upDirection.set(0.0, 1.0, 0.0);
		
        cameraMode = CameraMode.ORTHO;
		orthoWidth = 2.0;
		orthoHeight = orthoWidth / 1.33;
		projection = new Projection_LLA();//Projection_ECEF();//Projection_LLA();
        
		nearClip = 0;
		farClip = 200.0;        
        
        transConstraint = MotionConstraint.XY;
        rotConstraint = MotionConstraint.XYZ;
        zoomConstraint = MotionConstraint.XYZ;
        
        listeners = new STTEventListeners(1);
    }
	

    public int getViewHeight()
    {
        return viewHeight;
    }


    public void setViewHeight(int viewHeight)
    {
        this.viewHeight = viewHeight;
    }


    public int getViewWidth()
    {
        return viewWidth;
    }


    public void setViewWidth(int viewWidth)
    {
        this.viewWidth = viewWidth;
    }
    
    
	public CameraMode getCameraMode()
	{
		return cameraMode;
	}


	public void setCameraMode(CameraMode cameraMode)
	{
		this.cameraMode = cameraMode;
	}
    
    
    public Projection getProjection()
    {
        return projection;
    }


    public void setProjection(Projection intendedProjection)
    {
        this.projection = intendedProjection;
    }


	public Vector3d getCameraPos()
	{
		return cameraPos;
	}


	public void setCameraPos(Vector3d cameraPos)
	{
		this.cameraPos = cameraPos;
	}


	public double getFarClip()
	{
		return farClip;
	}


	public void setFarClip(double farClip)
	{
		this.farClip = farClip;
	}


	public double getNearClip()
	{
		return nearClip;
	}


	public void setNearClip(double nearClip)
	{
		this.nearClip = nearClip;
	}


	public double getOrthoHeight()
	{
		return orthoHeight;
	}


	public void setOrthoHeight(double orthoHeight)
	{
		this.orthoHeight = orthoHeight;
	}


	public double getOrthoWidth()
	{
		return orthoWidth;
	}


	public void setOrthoWidth(double orthoWidth)
	{
		this.orthoWidth = orthoWidth;
	}


	public double getCameraFov()
	{
		return cameraFov;
	}


	public void setCameraFov(double cameraFov)
	{
		this.cameraFov = cameraFov;
	}


	public Vector3d getTargetPos()
	{
		return targetPos;
	}


	public void setTargetPos(Vector3d targetPos)
	{
		this.targetPos = targetPos;
	}


	public Vector3d getUpDirection()
	{
		return upDirection;
	}


	public void setUpDirection(Vector3d upDirection)
	{
		this.upDirection = upDirection;
	}


	public int getZDepthFudgeFactor()
	{
		return zDepthFudgeFactor;
	}


	public void setZDepthFudgeFactor(int depthFudgeFactor)
	{
		zDepthFudgeFactor = depthFudgeFactor;
	}


	public Color getBackgroundColor()
	{
		return backgroundColor;
	}


	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}


	public boolean isAlphaBlendingEnabled()
	{
		return alphaBlendingEnabled;
	}


	public void setAlphaBlendingEnabled(boolean useAlphaBlending)
	{
		this.alphaBlendingEnabled = useAlphaBlending;
	}


    public boolean isShowCameraTarget()
    {
        return showCameraTarget;
    }


    public void setShowCameraTarget(boolean showCameraTarget)
    {
        this.showCameraTarget = showCameraTarget;
    }
    
    
    public MotionConstraint getRotConstraint()
    {
        return rotConstraint;
    }


    public void setRotConstraint(MotionConstraint rotConstraint)
    {
        this.rotConstraint = rotConstraint;
    }


    public MotionConstraint getTransConstraint()
    {
        return transConstraint;
    }


    public void setTransConstraint(MotionConstraint transConstraint)
    {
        this.transConstraint = transConstraint;
    }


    public MotionConstraint getZoomConstraint()
    {
        return zoomConstraint;
    }


    public void setZoomConstraint(MotionConstraint zoomConstraint)
    {
        this.zoomConstraint = zoomConstraint;
    }
    
    
    public void addListener(STTEventListener listener)
    {
        listeners.add(listener);
    }


    public void removeListener(STTEventListener listener)
    {
        listeners.remove(listener);
    }


    public void removeAllListeners()
    {
        listeners.clear();
    }
    
    
    public boolean hasListeners()
    {
        return !listeners.isEmpty();
    }


    public void dispatchEvent(STTEvent event)
    {
        event.producer = this;
        listeners.dispatchEvent(event);
    }
}
