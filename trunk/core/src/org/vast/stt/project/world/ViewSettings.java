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

package org.vast.stt.project.world;

import org.vast.math.*;
import org.vast.ows.sld.Color;
import org.vast.stt.dynamics.ViewSettingsUpdater;
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
 * <p>Copyright (c) 2007</p>
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
	protected double orthoWidth;
	//protected double orthoHeight;
    
	// camera modes
	protected CameraMode cameraMode;
    protected double arcballRadius = -1;   
    
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
    protected boolean showArcball = false;
    protected boolean showItemROI = false;
    
    protected STTEventListeners listeners;
    protected ViewSettingsUpdater updater;

	
	public ViewSettings()
	{
        backgroundColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        
        cameraPos.set(0.0, 0.0, 100.0);
		targetPos.set(0.0, 0.0, 0.0);
		upDirection.set(0.0, 1.0, 0.0);
		
        cameraMode = CameraMode.ORTHO;
		orthoWidth = 2.0;
		projection = new Projection_LLA();
        
		nearClip = 1.0;
		farClip = 200.0;        
        
        transConstraint = MotionConstraint.XY;
        rotConstraint = MotionConstraint.XYZ;
        zoomConstraint = MotionConstraint.XYZ;
        
        listeners = new STTEventListeners(1);
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


    public void setProjection(Projection projection)
    {
        this.projection = projection;
        this.transConstraint = projection.getDefaultTranslationConstraint();
        this.rotConstraint = projection.getDefaultRotationConstraint();
        this.zoomConstraint = projection.getDefaultZoomConstraint();
    }


	public Vector3d getCameraPos()
	{
		return cameraPos;
	}


	public void setCameraPos(Vector3d cameraPos)
	{
		this.cameraPos = cameraPos;
	}
	
	
	public double getCameraDistance()
	{
	    Vector3d diff = targetPos.copy();
	    diff.subtract(cameraPos);
	    return diff.length();
	}
	
	
	public double getCameraIncidence()
	{
	    return projection.getCameraIncidence(this);
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
    
    
    public boolean isShowArcball()
    {
        return showArcball;
    }


    public void setShowArcball(boolean showArcball)
    {
        this.showArcball = showArcball;
    }


    public boolean isShowItemROI()
    {
        return showItemROI;
    }


    public void setShowItemROI(boolean showCurrentROI)
    {
        this.showItemROI = showCurrentROI;
    }
    
    
    public double getArcballRadius()
    {
    	if (arcballRadius < 0)
            return getOrthoWidth() / 2;
    	else
    		return arcballRadius;
    }


    public void setArcballRadius(double arcballRadius)
    {
        this.arcballRadius = arcballRadius;
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
    
    
    public ViewSettingsUpdater getUpdater()
    {
        return updater;
    }


    public void setUpdater(ViewSettingsUpdater updater)
    {
        this.updater = updater;
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


    public void dispatchEvent(STTEvent event, boolean merge)
    {
        event.producer = this;
        listeners.dispatchEvent(event, merge);
    }
}
