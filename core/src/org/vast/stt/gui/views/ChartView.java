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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.event.STTEvent;


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
public class ChartView extends DataItemView implements PaintListener, ControlListener
{
	public static final String ID = "STT.ChartView";
    protected Canvas canvas;
    
    
    public ChartView()
    {
    }
			

    @Override
	public void createPartControl(Composite parent)
	{
		canvas = new Canvas(parent, SWT.NO_REDRAW_RESIZE);
		canvas.addControlListener(this);
		canvas.addPaintListener(this);
        super.createPartControl(parent);
	}
    	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
        // add show target action to toolbar
		IAction ShowTargetAction = new Action()
		{
			public void run()
			{
				//boolean targetshown = scene.getViewSettings().isShowCameraTarget();
                //scene.getViewSettings().setShowCameraTarget(!targetshown);
                //scene.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
			}
		};
        ShowTargetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
        ShowTargetAction.setToolTipText("Toggle Target Tripod");
		site.getActionBars().getToolBarManager().add(ShowTargetAction);
	}
	
	
	@Override
	public void dispose()
	{
        super.dispose();
        canvas.dispose();
	}
	
	
	@Override
	public void setFocus()
	{
		canvas.setFocus();
	}
    
    
    @Override
    public void updateView()
    {
        
    }
    
    
    @Override
    public void clearView()
    {
        
    }
    
    
    @Override
    public void handleEvent(STTEvent e)
    {       
        switch (e.type)
        {
            case ITEM_VISIBILITY_CHANGED:
                refreshViewAsync();
        }
    }
    
    
    public void paintControl(PaintEvent e)
    {
        
    }
    
    
    public void controlResized(ControlEvent e)
    {
        
    }
	
	
	public void controlMoved(ControlEvent e)
	{
	}
}