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
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>

 ******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.views;

import java.text.NumberFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.vast.math.Vector3d;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.feedback.FeedbackEvent;
import org.vast.stt.project.feedback.FeedbackEventListener;
import org.vast.stt.project.feedback.FeedbackEvent.FeedbackType;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.Projection;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.project.world.WorldSceneRenderer;
import org.vast.stt.project.world.Projection.Crs;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTPolygonExtent;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.renderer.PickFilter;
import org.vast.stt.renderer.PickedObject;


/**
 * <p><b>Title:</b><br/>
 * Scene View Controller
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Gives user 3D control on the view (rotation, translation, zoom)
 * and handles clicking capabilities (point and object selection)
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public class WorldViewController implements MouseListener, MouseMoveListener, Listener
{
	protected WorldScene scene;
	protected FeedbackEventListener pickListener;
	protected boolean pointSelectionMode;
	protected boolean objectSelectionMode;

	private Vector3d P0 = new Vector3d();
	private int xOld;
	private int yOld;
	private int corner;
	private boolean leftButtonDown;
	private boolean rightButtonDown;
	private boolean midButtonDown;
	private boolean dragged;
	private boolean resizing;    
	private final static double RTD = 180/Math.PI;
// ***  Figure out where this should go- use page.findView(WorldView.ID) as in SPSWidget,
	//  and this can be removed from this class
	LatLonStatusLine llStatus;
	

	public WorldViewController()
	{
		pickListener = new FeedbackEventListener();
	}

	public void setLatLonStatusLine(LatLonStatusLine llStatus){
		this.llStatus = llStatus;
	}

	//  thrown in to report LLA temporarily for EC08- clean up SOON!
	protected void reportLLTemp(int x1, int y1){
		Vector3d P0 = getLatLon(x1, y1);
		StringBuffer llStrBuff = new StringBuffer(40);
		NumberFormat nf  = NumberFormat.getInstance();
		nf.setMaximumIntegerDigits(3);
		nf.setMinimumFractionDigits(4);
		String lonStr = nf.format(P0.x);
		String latStr = nf.format(P0.y);
		llStrBuff.append("Lat: " + latStr + "   ");
		llStrBuff.append("Lon: " + lonStr);
		llStatus.setText(llStrBuff.toString());
	}

	//  Method to allow other classes to get Lat-Lon from this canvas based on mouse click
	//  Used by SPSWidget only right now.  TC
	public Vector3d getLatLon(int x1, int y1){
//		System.err.println(x1 +  " " + y1);
		Projection projection = scene.getViewSettings().getProjection();
		boolean found = projection.pointOnMap(x1, scene.getRenderer().getViewHeight()-y1, scene, P0);

		if (!found)
			return new Vector3d();

		// convert to LLA
		projection.unproject(Crs.EPSG4329, P0);

		P0.x *= RTD;
		P0.y *= RTD;
		
		return P0;
	}
	
	protected void doChangeROI(int x0, int y0, int x1, int y1)
	{
		DataItem selectedItem = scene.getSelectedItems().get(0).getDataItem();
		DataProvider provider = selectedItem.getDataProvider();
		STTSpatialExtent bbox = provider.getSpatialExtent();

		Projection projection = scene.getViewSettings().getProjection();
		boolean found = projection.pointOnMap(x1, y1, scene, P0);

		if (!found)
			return;

		// convert to LLA
		projection.unproject(Crs.EPSG4329, P0);

		P0.x *= RTD;
		P0.y *= RTD;

		switch (corner)
		{
		case 1:
			bbox.setMinX(P0.x);
			bbox.setMinY(P0.y);
			break;

		case 2:
			bbox.setMinX(P0.x);
			bbox.setMaxY(P0.y);
			break;

		case 3:
			bbox.setMaxX(P0.x);
			bbox.setMaxY(P0.y);
			break;

		case 4:
			bbox.setMaxX(P0.x);
			bbox.setMinY(P0.y);
			break;

		case 5:
			double dX = (bbox.getMaxX() - bbox.getMinX()) / 2;
			double dY = (bbox.getMaxY() - bbox.getMinY()) / 2;
			P0.x = Math.max(-179.99 + dX, P0.x);
			P0.x = Math.min(+179.99 - dX, P0.x);
			P0.y = Math.max(-89.99 + dY, P0.y);
			P0.y = Math.min(+89.99 - dY, P0.y);
			bbox.setMinX(P0.x - dX);
			bbox.setMaxX(P0.x + dX);
			bbox.setMinY(P0.y - dY);
			bbox.setMaxY(P0.y + dY);
			break;
		}

		// send event to update spatial extent listeners
		//provider.setEnabled(false);
		//bbox.dispatchEvent(new STTEvent(this, EventType.PROVIDER_SPATIAL_EXTENT_CHANGED));
		// commented out because it causes other providers subscribed to this bbox to redraw
	}


	public void mouseDown(MouseEvent e)
	{
		dragged = false;
		
		int viewHeight = scene.getRenderer().getViewHeight();
		e.y = viewHeight - e.y;
		reportLLTemp(e.x,e.y);

		// check if resizing ROI
		if (scene.getViewSettings().isShowItemROI() && !scene.getSelectedItems().isEmpty())
		{
			WorldSceneRenderer renderer = (WorldSceneRenderer)scene.getRenderer();
			PickFilter pickFilter = new PickFilter();
			pickFilter.x = e.x;
			pickFilter.y = e.y;
			pickFilter.dX = 5;
			pickFilter.dY = 5;
			pickFilter.onlyBoundingBox = true;
			PickedObject obj = renderer.pick(scene, pickFilter);

			if (obj != null && obj.indices.length > 0)
			{
				// case of corner selected
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

		// case of left button pressed
		if (e.button == 1)
		{
			if (e.stateMask == SWT.CTRL)
				midButtonDown = true;
			else if (e.stateMask == SWT.SHIFT)
				rightButtonDown = true;
			else
				leftButtonDown = true;
		}

		// case of middle button pressed
		else if (e.button == 2)
			midButtonDown = true;

		// case of right button pressed
		else if (e.button == 3)
			rightButtonDown = true;

		xOld = e.x;
		yOld = e.y;
	}


	public void mouseUp(MouseEvent e)
	{
		int viewHeight = scene.getRenderer().getViewHeight();
		e.y = viewHeight - e.y;

		// check if selecting point
		// this mode must be activated externally
		if (!dragged && pointSelectionMode)
		{
			DataItem selectedItem = scene.getSelectedItems().get(0).getDataItem();
			DataProvider provider = selectedItem.getDataProvider();
			STTSpatialExtent extent = provider.getSpatialExtent();

			if (extent instanceof STTPolygonExtent)
			{
				Projection proj = scene.getViewSettings().getProjection();
				Vector3d newPoint = new Vector3d();
				boolean onMap = proj.pointOnMap(e.x, e.y, scene, newPoint);
				if (onMap)
				{
					proj.unproject(Crs.EPSG4329, newPoint);
					newPoint.x *= RTD;
					newPoint.y *= RTD;
					newPoint.z = 0;
					((STTPolygonExtent)extent).addPoint(newPoint);
					updateView();
				}
			}
		}

		// check if selecting 3D object
		// this mode must be activated externally
		else if (!dragged && objectSelectionMode)
		{
			WorldSceneRenderer renderer = (WorldSceneRenderer)scene.getRenderer();
			PickFilter pickFilter = new PickFilter();
			pickFilter.x = e.x;
			pickFilter.y = e.y;
			pickFilter.dX = 5;
			pickFilter.dY = 5;
			pickFilter.onlyBoundingBox = true;
			PickedObject obj = renderer.pick(scene, pickFilter);

			// case of object selected
			if (obj != null && obj.indices.length > 0)
			{
				FeedbackType feedbackType = null;

				if (leftButtonDown)
					feedbackType = FeedbackType.LEFT_CLICK;
				else if (midButtonDown)
					feedbackType = FeedbackType.MID_CLICK;
				else if (rightButtonDown)
					feedbackType = FeedbackType.RIGHT_CLICK;

				FeedbackEvent event = new FeedbackEvent(feedbackType);
				event.setSourceScene(scene);
				pickListener.handleEvent(event);
			}
		}

		else if (dragged && resizing)
		{
			// trigger provider refresh when button is released
			DataProvider provider = scene.getSelectedItems().get(0).getDataItem().getDataProvider();
			provider.getSpatialExtent().dispatchEvent(new STTEvent(this, EventType.SPATIAL_EXTENT_CHANGED));
		}

		// resets all flags to false
		leftButtonDown = false;
		rightButtonDown = false;
		midButtonDown = false;
		resizing = false;
		dragged = false;        

		((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
	}


	public void mouseMove(MouseEvent e)
	{
		dragged = true;
		//  For now, reportLLTemp is reversing y convention to do computation.  Fix to be 
		//  consistent later today...  T
		reportLLTemp(e.x,e.y);

		int viewHeight = scene.getRenderer().getViewHeight();
		e.y = viewHeight - e.y;
//		System.err.println("MM: " + e.x + " " +e.y);
		
		if (leftButtonDown)
		{
			((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL));
			scene.getCameraController().doLeftDrag(xOld, yOld, e.x, e.y);
			xOld = e.x;
			yOld = e.y;
			updateView();
		}

		else if (rightButtonDown)
		{
			((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL));
			scene.getCameraController().doRightDrag(xOld, yOld, e.x, e.y);
			xOld = e.x;
			yOld = e.y;
			updateView();
		}

		else if (midButtonDown)
		{
			((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL));
			scene.getCameraController().doMiddleDrag(xOld, yOld, e.x, e.y);
			xOld = e.x;
			yOld = e.y;
			updateView();
		}

		else if (resizing)
		{
			((Control) e.widget).setCursor(e.widget.getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL));
			doChangeROI(xOld, yOld, e.x, e.y);
			updateView();
		}
	}


	public void handleEvent(Event event)
	{
		scene.getCameraController().doWheel(event.count);
		updateView();
	}


	public void mouseDoubleClick(MouseEvent e)
	{
		int viewHeight = scene.getRenderer().getViewHeight();
		e.y = viewHeight - e.y;

		if (e.button == 1)
			scene.getCameraController().doLeftDblClick(e.x, e.y);
		else if (e.button == 2)
			scene.getCameraController().doMiddleDblClick(e.x, e.y);
		else if (e.button == 3)
			scene.getCameraController().doRightDblClick(e.x, e.y);

		updateView();
	}


	protected void updateView()
	{
		scene.getViewSettings().dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
	}


	public WorldScene getScene()
	{
		return scene;
	}


	public void setScene(WorldScene scene)
	{
		this.scene = scene;
	}


	public boolean isPointSelectionMode()
	{
		return pointSelectionMode;
	}


	public void setPointSelectionMode(boolean selectingPoint)
	{
		this.pointSelectionMode = selectingPoint;
	}


	public boolean isObjectSelectionMode()
	{
		return objectSelectionMode;
	}


	public void setObjectSelectionMode(boolean selectingObject)
	{
		this.objectSelectionMode = selectingObject;
	}
}