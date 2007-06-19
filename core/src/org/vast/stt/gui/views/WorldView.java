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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.renderer.SceneRenderer;


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
public class WorldView extends SceneView<WorldScene> implements PaintListener, ControlListener
{
	public static final String ID = "STT.WorldView";
    private Composite parent;
    private WorldViewController controller;
    
    
    public WorldView()
    {
        controller = new WorldViewController();
    }
			

    @Override
	public void createPartControl(Composite parent)
	{
        this.parent = parent;
        parent.setLayout(new FillLayout());
        getSite().getPage().addPartListener(this);        
	}
    	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
        ImageDescriptor descriptor;
        
        // add show target action to toolbar
		IAction ShowTargetAction = new Action()
		{
			public void run()
			{
				boolean targetShown = scene.getViewSettings().isShowCameraTarget();
                scene.getViewSettings().setShowCameraTarget(!targetShown);
                scene.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
			}
		};        
        descriptor = STTPlugin.getImageDescriptor("icons/tripod.gif");
        ShowTargetAction.setImageDescriptor(descriptor);
        ShowTargetAction.setToolTipText("Toggle Target Tripod");
		site.getActionBars().getToolBarManager().add(ShowTargetAction);
        
		// add show arcball action to toolbar
        IAction ShowArcballAction = new Action()
        {
            public void run()
            {
                boolean arcballShown = scene.getViewSettings().isShowArcball();
                scene.getViewSettings().setShowArcball(!arcballShown);
                scene.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
            }
        };
        descriptor = STTPlugin.getImageDescriptor("icons/arcball.gif");
        ShowArcballAction.setImageDescriptor(descriptor);
        ShowArcballAction.setToolTipText("Toggle Arcball");
        site.getActionBars().getToolBarManager().add(ShowArcballAction);
        
        // add show ROI action to toolbar
        IAction ShowROIAction = new Action()
        {
            public void run()
            {
                boolean roiShown = scene.getViewSettings().isShowItemROI();
                scene.getViewSettings().setShowItemROI(!roiShown);
                scene.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
            }
        };
        descriptor = STTPlugin.getImageDescriptor("icons/bbox.gif");
        ShowROIAction.setImageDescriptor(descriptor);
        ShowROIAction.setToolTipText("Toggle ROI");
        site.getActionBars().getToolBarManager().add(ShowROIAction);
	}
	
	
	@Override
	public void dispose()
	{
        super.dispose();
        if (scene != null)
            scene.getRenderer().dispose();
	}
	
	
	@Override
	public void setFocus()
	{
        parent.setFocus();
	}
    
    
    @Override
    public void setScene(WorldScene sc)
    {
        if (scene != sc)
        {
            // make sure we dispose previous scene renderer
            if (scene != null)
                scene.getRenderer().dispose();
    
            // call parent method !!
            super.setScene(sc);
            setPartName(scene.getName());
            
            // init the renderer
            SceneRenderer renderer = scene.getRenderer();
            renderer.setParent(parent);
            renderer.init();
            Composite canvas = renderer.getCanvas();
            
            // init size
            //Rectangle clientArea = canvas.getClientArea();
            //scene.getRenderer().resizeView(clientArea.width, clientArea.height);
            //scene.getViewSettings().setViewWidth(clientArea.width);
            //scene.getViewSettings().setViewHeight(clientArea.height);
            
            // create and register view controller
            controller.setScene(scene);
            canvas.addMouseListener(controller);
            canvas.addMouseMoveListener(controller);
            canvas.addListener(SWT.MouseWheel , controller);
            canvas.addControlListener(this);
            canvas.addPaintListener(this);
            
            // refresh display
            refreshViewAsync();
        }
    }
    
    
    @Override
    public void updateView()
    {
        // render whole scene tree
        scene.getRenderer().drawScene(scene);
    }
    
    
    @Override
    public void clearView()
    {
        // Clears the world view and unregister listeners
        parent.removeMouseListener(controller);
        parent.removeMouseMoveListener(controller);
        parent.removeListener(SWT.MouseWheel , controller);
        parent.redraw();
        setPartName("Nothing Open");
    }
    
    
    @Override
    public void handleEvent(STTEvent e)
    {       
        switch (e.type)
        {
            case SCENE_OPTIONS_CHANGED:            
            case SCENE_VIEW_CHANGED:
            case SCENE_ITEM_CHANGED:
            case ITEM_VISIBILITY_CHANGED:
                refreshViewAsync();
                break;
                
            case SCENE_PROJECTION_CHANGED:
                // create fit runnable for async exec
                Runnable runFitScene = new Runnable()
                {
                    public void run()
                    {
                        ViewSettings view = scene.getViewSettings();
                        view.getProjection().fitViewToBbox(scene.getBoundingBox(), scene, true);
                        refreshView();
                    }
                };
                // fit view to scene
                parent.getDisplay().syncExec(runFitScene);
                break;
        }
    }
    
    
    public void paintControl(PaintEvent e)
    {
        if (scene != null)
            updateView();
    }
    
    
    public void controlResized(ControlEvent e)
    {
        if (scene != null)
        {
            // redraw the whole scene
            updateView();
            scene.getViewSettings().dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
        }
    }
	
	
	public void controlMoved(ControlEvent e)
	{
	}
}