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

import org.vast.math.Quat4d;
import org.vast.math.Vector3d;
import org.vast.physics.MapProjection;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.scene.Projection;
import org.vast.stt.project.scene.Projection_ECEF;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.scene.ViewSettings;
import org.vast.stt.project.scene.ViewSettings.CameraMode;
import org.vast.stt.project.scene.ViewSettings.MotionConstraint;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.renderer.PickFilter;
import org.vast.stt.renderer.PickedObject;
import org.vast.stt.renderer.SceneRenderer;
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
public class WorldViewController implements MouseListener, MouseMoveListener, Listener
{
	protected Scene scene;    
	private Vector3d P0 = new Vector3d();
	private Vector3d P1 = new Vector3d();
    private Vector3d C = new Vector3d();
	private int xOld;
	private int yOld;
    private int corner;
	private boolean rotating;
	private boolean translating;
	private boolean zooming;
    private boolean resizing;
    private boolean noMove;
	private PickFilter pickFilter;
    private final static double RTD = 180/Math.PI;
    

	public WorldViewController()
	{
        pickFilter = new PickFilter();
	}
	
	
	protected void doRotation(int x0, int y0, int x1, int y1)
	{
        ViewSettings viewSettings = scene.getViewSettings();
        MotionConstraint rotConstraint = viewSettings.getRotConstraint();
        
        if (rotConstraint != MotionConstraint.NO_MOTION)
        {
            SceneRenderer renderer = scene.getRenderer();

            // actual camera position
            Vector3d up = viewSettings.getUpDirection();
            Vector3d pos = viewSettings.getCameraPos();
            Vector3d target = viewSettings.getTargetPos();
            
            // ge actual viewport dimensions
            int viewHeight = renderer.getViewHeight();
            int viewWidth = renderer.getViewWidth();
            
            // unproject to world view
            renderer.unproject(x0, viewHeight-y0, 0.0, P0);
            renderer.unproject(x1, viewHeight-y1, 0.0, P1);
            renderer.unproject(viewWidth/2, viewHeight/2, 0.0, C);
            
            // viewZ vector = target - pos
            Vector3d oldZ = new Vector3d(target);
            oldZ.sub(pos);
            oldZ.normalize();
            
            // arcball radius
            double r = viewSettings.getArcballRadius();
            if (r < 0)
                r = viewSettings.getOrthoWidth() / 2;

            // pos of point 0 on arcball
            P0.sub(C);
            P0.scale(1/r);
            double P0_2 = P0.lengthSquared();
            if (P0_2 < 1)
            {
                Vector3d fakeZ = oldZ.copy();
                fakeZ.scale(-Math.sqrt(1 - P0_2));
                P0.add(fakeZ);
            }
            P0.normalize();
            
            // pos of point 1 on arcball
            P1.sub(C);
            P1.scale(1/r);            
            double P1_2 = P1.lengthSquared();
            if (P1_2 < 1)
            {
                Vector3d fakeZ = oldZ.copy();
                fakeZ.scale(-Math.sqrt(1 - P1_2));
                P1.add(fakeZ);
            }
            P1.normalize();
            
            // compute rotation quaternion
            Vector3d crossP = new Vector3d();
            crossP.cross(P0, P1);
            crossP.normalize();
            Quat4d qRot = new Quat4d(crossP, -Math.acos(P0.dot(P1)));

            // rotate current camera position
            pos.sub(target);
            pos.rotate(qRot);
            pos.add(target);
            
            // also update up axis
            up.rotate(qRot);
        }
	}
	
	
	protected void doTranslation(int x0, int y0, int x1, int y1)
	{
        ViewSettings viewSettings = scene.getViewSettings();
        MotionConstraint transConstraint = viewSettings.getTransConstraint();
        
        if (transConstraint != MotionConstraint.NO_MOTION)
        {
            SceneRenderer renderer = scene.getRenderer();
            
            renderer.unproject(x0, -y0, 0.0, P0);
            renderer.unproject(x1, -y1, 0.0, P1);
    		P1.sub(P0);
            
            Vector3d pos = viewSettings.getCameraPos();
            Vector3d target = viewSettings.getTargetPos();
            
            // viewZ vector = target - pos
            Vector3d viewZ = new Vector3d(target);
            viewZ.sub(pos);
            viewZ.normalize();
            
            double s;
            double maxS = P1.length()*2;
            
            switch (transConstraint)
            {
                case XY:
                    // P1 + s*viewZ = [x,y,0]
                    s = -P1.z / viewZ.z;
                    if (Math.abs(s) > maxS) s = Math.signum(s) * maxS;
                    viewZ.scale(s);
                    P1.add(viewZ);
                    P1.z = 0.0;
                    break;
                    
                case XZ:
                    // P1 + s*viewZ = [x,0,z]
                    s = -P1.y / viewZ.y;
                    viewZ.scale(s);
                    P1.add(viewZ);
                    P1.y = 0.0;
                    break;
                    
                case YZ:
                    // P1 + s*viewZ = [0,y,z]
                    s = -P1.x / viewZ.x;
                    viewZ.scale(s);
                    P1.add(viewZ);
                    P1.x = 0.0;
                    break;
            }
    		
            viewSettings.getTargetPos().sub(P1);
    		viewSettings.getCameraPos().sub(P1);
        }
	}
	
	
	protected void doZoom(double amount)
	{
        ViewSettings viewSettings = scene.getViewSettings();
        MotionConstraint zoomConstraint = viewSettings.getZoomConstraint();
        
        if (zoomConstraint != MotionConstraint.NO_MOTION)
        {
            // zoom in ortho mode
    		if (viewSettings.getCameraMode() == CameraMode.ORTHO)
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
	}
	

	public void mouseDown(MouseEvent e)
	{
        noMove = true;
        
        if (scene.getViewSettings().isShowItemROI() && !scene.getSelectedItems().isEmpty())
        {
            SceneRenderer renderer = scene.getRenderer();
            pickFilter.x = e.x;
            pickFilter.y = e.y;
            pickFilter.dX = 5;
            pickFilter.dY = 5;
            pickFilter.onlyBoundingBox = true;
            pickFilter.onlySelectedItems = false;
            pickFilter.onlyWithEvent = false;
            PickedObject obj = renderer.pick(scene, pickFilter);
            
            if (obj != null && obj.indices.length > 0)
            {
                if (obj.indices[0] < 0)
                {
                    corner = -obj.indices[0];
                    resizing = true;
                    xOld = e.x;
                    yOld = e.y;
                    return;
                }
            }
        }
        
        if (e.button == 1)
		{
			if (e.stateMask == SWT.CTRL)
				zooming = true;
			else if (e.stateMask == SWT.SHIFT)
				translating = true;
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
		if (noMove)
		{
            SceneRenderer renderer = scene.getRenderer();
            pickFilter.x = e.x;
            pickFilter.y = e.y;
            pickFilter.dX = 5;
            pickFilter.dY = 5;
            pickFilter.onlyBoundingBox = false;
            pickFilter.onlySelectedItems = false;
            pickFilter.onlyWithEvent = true;
            renderer.pick(scene, pickFilter);
        }
        else if (resizing)
        {
            DataProvider provider = scene.getSelectedItems().get(0).getDataItem().getDataProvider();
            provider.getSpatialExtent().dispatchEvent(new STTEvent(this, EventType.PROVIDER_SPATIAL_EXTENT_CHANGED));
        }
        
        rotating = false;
		translating = false;
		zooming = false;
        resizing = false;
        
		((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
	}


	public void mouseMove(MouseEvent e)
	{
        noMove = false;
        
        if (rotating || translating || zooming)
		{           
            if (rotating)
			{
				((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
				doRotation(xOld, yOld, e.x, e.y);				
			}
			
			else if (translating)
			{
				((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL));
				doTranslation(xOld, yOld, e.x, e.y);				
			}
			
			else if (zooming)
			{
				((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEN));
                int viewHeight = scene.getRenderer().getCanvas().getClientArea().height;
				double amount = 2.0 * ((double)(e.y - yOld)) / ((double)viewHeight);
				doZoom(-amount);
			}
			
			xOld = e.x;
			yOld = e.y;
			updateView();
		}
        
        else if (resizing)
        {
            ((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL));
            STTSpatialExtent bbox = scene.getSelectedItems().get(0).getDataItem().getDataProvider().getSpatialExtent();
            int viewHeight = scene.getRenderer().getViewHeight();
            Projection projection = scene.getViewSettings().getProjection();
            projection.pointOnMap(e.x, viewHeight-e.y, scene, P0);
            
            if (Double.isNaN(P0.x))
                return;
            
            // hack to convert from ECEF to LLA
            if (projection instanceof Projection_ECEF)
            {
                double[] lla = MapProjection.ECFtoLLA(P0.x, P0.y, P0.z, null);
                
                if (lla[1] > Math.PI)
                    lla[1] -= 2*Math.PI;
                
                else if (lla[1] < -Math.PI)
                    lla[1] += 2*Math.PI;
                
                P0.y = lla[0];
                P0.x = lla[1];
                P0.z = lla[2];
            }
            
            switch (corner)
            {
                case 1:
                    bbox.setMinX(P0.x * RTD);
                    bbox.setMinY(P0.y * RTD);
                    break;
                    
                case 2:
                    bbox.setMinX(P0.x * RTD);
                    bbox.setMaxY(P0.y * RTD);
                    break;
                    
                case 3:
                    bbox.setMaxX(P0.x * RTD);
                    bbox.setMaxY(P0.y * RTD);
                    break;
                    
                case 4:
                    bbox.setMaxX(P0.x * RTD);
                    bbox.setMinY(P0.y * RTD);
                    break;
                    
                case 5:
                    double dX = (bbox.getMaxX() - bbox.getMinX()) / 2;
                    double dY = (bbox.getMaxY() - bbox.getMinY()) / 2;
                    bbox.setMinX(P0.x * RTD - dX);
                    bbox.setMaxX(P0.x * RTD + dX);
                    bbox.setMinY(P0.y * RTD - dY);
                    bbox.setMaxY(P0.y * RTD + dY);
                    break;
            }
            
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
        scene.getViewSettings().dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
	}


	public Scene getScene()
    {
        return scene;
    }


    public void setScene(Scene scene)
    {
        this.scene = scene;
    }
}