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

package org.vast.stt.gui.views;

import org.vast.math.Quaternion;
import org.vast.math.Vector3D;
import org.vast.stt.apps.STTConfig;
import org.vast.stt.event.STTEvent;
import org.vast.stt.scene.ViewSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.events.*;


/**
 * <p><b>Title:</b><br/>
 * Scene View Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Gives user 3D control on the view (rotation, translation, zoom)
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public class SceneViewController implements MouseListener, MouseMoveListener, Listener
{
	protected SceneView sceneView;
	protected boolean enableZoom = true;
	protected boolean enableRotation = true;
	protected boolean enableTranslation = true;
	
	private ViewSettings viewSettings;
	private Vector3D P0 = new Vector3D();
	private Vector3D P1 = new Vector3D();
	private int xOld;
	private int yOld;
	private boolean rotating;
	private boolean translating;
	private boolean zooming;
	

	public SceneViewController(SceneView sceneView)
	{
		this.sceneView = sceneView;	
	}
	
	
	protected void doRotation(int x0, int y0, int x1, int y1)
	{
		sceneView.getRenderer().unproject(x0, -y0, 0.0, P0);
		sceneView.getRenderer().unproject(x1, -y1, 0.0, P1);
		P1.sub(P0);		
		
        // actual camera position
        Vector3D up = viewSettings.getUpDirection();
        Vector3D pos = viewSettings.getCameraPos();
        Vector3D target = viewSettings.getTargetPos();
        Vector3D oldZ = Vector3D.subtractVectors(target, pos);

        // rotation angle proportional to drag distance on the screen
        double rotationAmount = P1.length()/viewSettings.getOrthoWidth() * Math.PI;//Math.atan(P1.length()/view.getOrthoWidth()/2);
        double rotationAngle = rotationAmount;//10.0;

        // rotation axis in world coordinates
        Vector3D rotationAxis = oldZ.cross(P1);
        Quaternion qRot = new Quaternion(rotationAxis, rotationAngle);

        // rotate current camera position
        pos.sub(target);
        pos.rotate(qRot);
        pos.add(target);

        //System.out.println(qRot);
        // also update up axis
        up.rotate(qRot);
	}
	
	
	protected void doTranslation(int x0, int y0, int x1, int y1)
	{
		sceneView.getRenderer().unproject(x0, -y0, 0.0, P0);
		sceneView.getRenderer().unproject(x1, -y1, 0.0, P1);
		P1.sub(P0);
		viewSettings.getTargetPos().sub(P1);
		viewSettings.getCameraPos().sub(P1);
	}
	
	
	protected void doZoom(double amount)
	{
		// zoom in ortho mode
		if (viewSettings.getCameraMode() == ViewSettings.ORTHO)
		{
			double currentWidth = viewSettings.getOrthoWidth();			
			double newWidth = currentWidth + amount*currentWidth;
			
			if (newWidth == 0.0 && amount > 0.0)
				newWidth = amount;
			else if (newWidth < 0.0)
				newWidth = 0.0;
			
			viewSettings.setOrthoWidth(newWidth);
		}
	}
	

	public void mouseDown(MouseEvent e)
	{
		if (e.button == 1)
		{
			if (e.stateMask == SWT.CTRL)
				zooming = true;
			else
				rotating = true;
		}
		
		if (e.button == 3)
			translating = true;
		
		if (e.button == 2)
			zooming = true;
		
		xOld = e.x;
		yOld = e.y;
	}


	public void mouseUp(MouseEvent e)
	{
		rotating = false;
		translating = false;
		zooming = false;
		((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
	}


	public void mouseMove(MouseEvent e)
	{
		if (rotating || translating || zooming)
		{
			if (rotating && enableRotation)
			{
				((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
				doRotation(xOld, yOld, e.x, e.y);				
			}
			
			else if (translating && enableTranslation)
			{
				((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL));
				doTranslation(xOld, yOld, e.x, e.y);				
			}
			
			else if (zooming && enableZoom)
			{
				((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEN));				
				double amount = 2.0 * ((double)(e.y - yOld)) / ((double)sceneView.getCanvas().getClientArea().height);
				doZoom(amount);
			}
			
			xOld = e.x;
			yOld = e.y;
			updateView();
		}
	}


	public void handleEvent(Event event)
	{
		double amount = event.count/20.0;
		doZoom(amount);
		updateView();
	}
	
	
	public void mouseDoubleClick(MouseEvent e)
	{
	}
	
	
	protected void updateView()
	{
		//sceneView.getRenderer().redraw();
		STTConfig.getInstance().getEventManager().postEvent(new STTEvent(viewSettings, STTEvent.Section.SCENE_VIEW));
	}


	public boolean isEnableRotation()
	{
		return enableRotation;
	}


	public void setEnableRotation(boolean enableRotation)
	{
		this.enableRotation = enableRotation;
	}


	public boolean isEnableTranslation()
	{
		return enableTranslation;
	}


	public void setEnableTranslation(boolean enableTranslation)
	{
		this.enableTranslation = enableTranslation;
	}


	public boolean isEnableZoom()
	{
		return enableZoom;
	}


	public void setEnableZoom(boolean enableZoom)
	{
		this.enableZoom = enableZoom;
	}


    public void setViewSettings(ViewSettings viewSettings)
    {
        this.viewSettings = viewSettings;
    }
}