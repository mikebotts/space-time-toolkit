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
import org.vast.stt.commands.FitView;


/**
 * <p><b>Title:</b>
 * World View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * The world view is the 2D/3D view where the data items of
 * the scene are rendered using the provided renderer.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public class WorldView extends SceneView implements PaintListener, ControlListener
{
	public static final String ID = "STT.WorldView";
	private Canvas canvas;
	private WorldViewController controller;
    
    
    // Runnable for rendering scene asynchronously in GUI thread
	Runnable renderExec = new Runnable()
	{
		public void run()
		{
			scene.getRenderer().drawScene(scene);
		}
	};
    
    
    public WorldView()
    {
        controller = new WorldViewController();
    }
			

    @Override
	public void createPartControl(Composite parent)
	{
		canvas = new Canvas(parent, SWT.NO_REDRAW_RESIZE);
		canvas.addControlListener(this);
		canvas.addPaintListener(this);
        super.createPartControl(parent);
	}
    
    
    /**
     * Redraw the whole world view using provided renderer
     */
    public void updateView()
    {
        // set part name
        setPartName("Scene: " + scene.getName());
        
        // render whole scene tree
        canvas.getDisplay().asyncExec(renderExec);
    }
    
    
    /**
     * Clears the world view and unregister listeners
     */
    public void clearView()
    {
        canvas.removeMouseListener(controller);
        canvas.removeMouseMoveListener(controller);
        canvas.removeListener(SWT.MouseWheel , controller);
        canvas.redraw();
        setPartName("Nothing Open");
    }
    
    
    /**
     * init scene and renderer parameters
     */
    public void assignScene()
    {
        // make sure we dispose previous renderer
        if (scene != null)
            scene.getRenderer().dispose();

        // call parent method !!
        super.assignScene();
        
        if (scene != null)
        {        
            // init the renderer
            scene.getRenderer().setCanvas(canvas);
            scene.getRenderer().init();
            
            // init size
            Rectangle clientArea = canvas.getClientArea();
            scene.getRenderer().resizeView(clientArea.width, clientArea.height);
            scene.getViewSettings().setViewHeight(clientArea.height);
            scene.getViewSettings().setViewWidth(clientArea.width);
            
            // create and register view controller
            controller.setScene(scene);
            canvas.addMouseListener(controller);
            canvas.addMouseMoveListener(controller);
            canvas.addListener(SWT.MouseWheel , controller);
            
            // register view as listener to the scene
            scene.addListener(this);
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
	}
	
	
	@Override
	public void dispose()
	{
		canvas.dispose();
        scene.getRenderer().dispose();
        scene.removeListener(this);
        super.dispose();
	}
	
	
	@Override
	public void setFocus()
	{
		canvas.setFocus();
	}
    
    
    public void paintControl(PaintEvent e)
    {
        if (scene != null)
            scene.getRenderer().drawScene(scene);
    }
    
    
    public void controlResized(ControlEvent e)
    {
        if (scene != null)
        {
            Rectangle clientArea = canvas.getClientArea();
            scene.getRenderer().resizeView(clientArea.width, clientArea.height);
            scene.getViewSettings().setViewHeight(clientArea.height);
            scene.getViewSettings().setViewWidth(clientArea.width);
        }
    }
	
	
	public void controlMoved(ControlEvent e)
	{
	}
}