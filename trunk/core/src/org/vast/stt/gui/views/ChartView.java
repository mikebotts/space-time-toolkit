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

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.chart.ChartScene;


/**
 * <p><b>Title:</b>
 * Chart View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * The chart view is a 2D view used to display simple charts.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public class ChartView extends SceneView<ChartScene> implements PaintListener, ControlListener
{
	public static final String ID = "STT.ChartView";
    private Composite composite;
    
    
    public ChartView()
    {
    }
			

    @Override
	public void createPartControl(Composite parent)
	{
		composite = parent;
        composite.addControlListener(this);
        composite.addPaintListener(this);
	}
    	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);		
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
        composite.setFocus();
	}
    
    
    @Override
    public void setScene(ChartScene sc)
    {
        if (scene != sc)
        {
            // make sure we dispose previous renderer
            if (scene != null)
                scene.getRenderer().dispose();
    
            // call parent method !!
            super.setScene(sc);
            setPartName(scene.getName());
            
            // init the renderer
            scene.getRenderer().setParent(composite);
            scene.getRenderer().init();
            
            // create and register view controller
            //canvas.addMouseListener(controller);
            //canvas.addMouseMoveListener(controller);
            //canvas.addListener(SWT.MouseWheel , controller);
            
            // register view as listener to the scene
            scene.addListener(this);
            
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
        //canvas.removeMouseListener(controller);
        //canvas.removeMouseMoveListener(controller);
        //canvas.removeListener(SWT.MouseWheel , controller);
        composite.redraw();
        setPartName("Nothing Open");
    }
    
    
    @Override
    public void handleEvent(STTEvent e)
    {       
        switch (e.type)
        {
            case SCENE_OPTIONS_CHANGED:
            //case SCENE_VIEW_CHANGED:
            case SCENE_ITEM_CHANGED:
            case ITEM_VISIBILITY_CHANGED:
                refreshViewAsync();
        }
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
            // update view size
            Rectangle clientArea = composite.getClientArea();
            scene.getRenderer().resizeView(clientArea.width, clientArea.height);
            
            // redraw the whole scene
            //scene.getRenderer().drawScene(scene);
        }
    }
	
	
	public void controlMoved(ControlEvent e)
	{
	}
}