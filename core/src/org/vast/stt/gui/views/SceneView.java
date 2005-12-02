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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.apps.*;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.scene.*;
import org.vast.stt.renderer.*;
import org.vast.stt.renderer.opengl.OpenGLRenderer;


public class SceneView extends ViewPart implements PaintListener, ControlListener, STTEventListener
{
	public static final String ID = "STT.SceneView";
	private Canvas canvas;
	private Renderer renderer;
	private Scene scene;
	
	
	Runnable renderExec = new Runnable()
	{
		public void run()
		{
			renderer.drawScene(scene);
		}
	};
			

	public void createPartControl(Composite parent)
	{
		canvas = new Canvas(parent, SWT.NO_REDRAW_RESIZE);
		canvas.addControlListener(this);
		canvas.addPaintListener(this);
		
		String id = ((IViewSite)this.getSite()).getSecondaryId();
		int sceneIndex = Integer.parseInt(id);
		Scene scene = STTConfig.getInstance().getCurrentProject().getSceneList().get(sceneIndex);
		this.setPartName(scene.getName());
		this.scene = scene;
		
		renderer = new OpenGLRenderer();
		renderer.setCanvas(canvas);
		renderer.init();
		
		ViewSettings viewSettings = scene.getViewSettings();		
		if (viewSettings instanceof ViewSettings)
		{
			new SceneViewController(this);
		}	
		
		STTConfig.getInstance().getEventManager().addSceneViewListener(this);
	}
	
	
	public void paintControl(PaintEvent e)
	{
		renderer.drawScene(scene);
	}
	
	
	public void controlResized(ControlEvent e)
	{
		Rectangle clientArea = canvas.getClientArea();
		renderer.resizeView(clientArea.width, clientArea.height);
		scene.getViewSettings().setViewHeight(clientArea.height);
		scene.getViewSettings().setViewWidth(clientArea.width);
	}
	
	
	public void handleEvent(STTEvent e)
	{
		if (e.getSource() == scene.getViewSettings())
		{
			canvas.getDisplay().asyncExec(renderExec);
		}
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
		IAction action1 = new Action()
		{
			public void run()
			{
				
			}
		};
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		site.getActionBars().getToolBarManager().add(action1);
	}
	
	
	@Override
	public void dispose()
	{
		canvas.dispose();
		renderer.dispose();
		STTConfig.getInstance().getEventManager().removeSceneViewListener(this);
	}
	
	
	@Override
	public void setFocus()
	{
		canvas.setFocus();
	}
	
	
	public void controlMoved(ControlEvent e)
	{
	}


	public Canvas getCanvas()
	{
		return canvas;
	}


	public Renderer getRenderer()
	{
		return renderer;
	}


	public Scene getScene()
	{
		return scene;
	}
}