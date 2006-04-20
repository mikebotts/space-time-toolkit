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
import org.vast.stt.commands.FitView;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.scene.*;
import org.vast.stt.project.Project;
import org.vast.stt.renderer.*;
import org.vast.stt.renderer.opengl.*;


public class SceneView extends ViewPart implements PaintListener, ControlListener, STTEventListener
{
	public static final String ID = "STT.SceneView";
	private Canvas canvas;
	private Renderer renderer;
	private Scene scene;
	private SceneViewController controller;
    
	
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
		this.reset();
		STTConfig.getInstance().getEventManager().addSceneViewListener(this);
	}
    
    
    public void clear()
    {
        if (scene != null)
        {
            scene = null;
            renderer = null;
            controller.setViewSettings(null);
            canvas.removeMouseListener(controller);
            canvas.removeMouseMoveListener(controller);
            canvas.removeListener(SWT.MouseWheel , controller);
            canvas.redraw();
            setPartName("Nothing Open");
        }
    }
    
    
    public void reset()
    {
        String id = ((IViewSite)this.getSite()).getSecondaryId();
        int sceneIndex = Integer.parseInt(id);
        Project currentProject = STTConfig.getInstance().getCurrentProject();
        
        if (currentProject != null)
        {
            // retrieve project scene
            scene = currentProject.getSceneList().get(sceneIndex);
            setPartName(scene.getName());
            
            // create the renderer
            renderer = new JOGLRenderer();
            renderer.setCanvas(canvas);
            renderer.init();
            
            // init size
            Rectangle clientArea = canvas.getClientArea();
            renderer.resizeView(clientArea.width, clientArea.height);
            scene.getViewSettings().setViewHeight(clientArea.height);
            scene.getViewSettings().setViewWidth(clientArea.width);
            
            // set and register view controller
            controller.setViewSettings(scene.getViewSettings());
            canvas.addMouseListener(controller);
            canvas.addMouseMoveListener(controller);
            canvas.addListener(SWT.MouseWheel , controller);
        }
    }
	
	
	public void paintControl(PaintEvent e)
	{
		if (scene != null)
		    renderer.drawScene(scene);
	}
	
	
	public void controlResized(ControlEvent e)
	{
        if (scene != null)
        {
            Rectangle clientArea = canvas.getClientArea();
    		renderer.resizeView(clientArea.width, clientArea.height);
    		scene.getViewSettings().setViewHeight(clientArea.height);
    		scene.getViewSettings().setViewWidth(clientArea.width);
        }
	}
	
	
	public void handleEvent(STTEvent e)
	{
		if (scene != null)
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
		action1.setToolTipText("File this!");
		site.getActionBars().getToolBarManager().add(action1);

		Action fitViewAction = new Action()
		{
			public void run()
			{
				FitView fv = new FitView(scene);
				System.err.println("Execute fitView");
				fv.execute();
			}
		};
		
		fitViewAction.setText("Fit View");
		site.getActionBars().getMenuManager().add(fitViewAction);
		
		Action fitItemAction = new Action()
		{
			public void run()
			{
				//FitView fv = new FitView(scene.getSelectedItem...);
				FitView fv = new FitView(scene);
				System.err.println("Execute fitItem");
				fv.execute();
			}
		};
		
		fitItemAction.setText("Fit View to Selected Item");
		site.getActionBars().getMenuManager().add(fitItemAction);
        
        controller = new SceneViewController(this);
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