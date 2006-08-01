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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.Scene;


/**
 * <p><b>Title:</b>
 * Scene View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for all Scene Views.
 * This provides event handling and enforce the implementation of 
 * two other methods updateView() and clearView().
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public abstract class SceneView extends ViewPart implements IPageListener, STTEventListener
{
    protected Scene scene;
    protected boolean updating = false;
    protected Runnable runRefresh = new Runnable()
    {
        public void run() {refreshView();}
    };


    public abstract void updateView();
    public abstract void clearView();
    
    
    /**
     * Method called when the view needs to be refreshed 
     * (i.e. typically after a "CHANGE" event is received)
     */
    protected void refreshView()
    {
        if (!updating)
        {
            updating = true;
            if (scene != null)
                updateView();
            else
                clearView();
            updating = false;
        }
    }
    
    
    protected void refreshViewAsync()
    {
        getSite().getShell().getDisplay().asyncExec(runRefresh);
    }
    
    
    protected void assignScene()
    {
        ScenePageInput pageInput = (ScenePageInput)getSite().getPage().getInput();
        if (pageInput != null)
            setScene(pageInput.getScene());
        else 
            setScene(null);
        refreshView();
    }
    
    
    public void setScene(Scene sc)
    {
        if (scene != sc)
        {
            if (scene != null)
                scene.removeListener(this);
            
            this.scene = sc;
            
            if (scene != null)
                scene.addListener(this);
        }
    }

    
    @Override
    public void createPartControl(Composite parent)
    {
        getSite().getWorkbenchWindow().addPageListener(this);
        assignScene();
    }
    

    @Override
    public void setFocus()
    {
        refreshView();
    }


    @Override
    public void dispose()
    {
        getSite().getWorkbenchWindow().removePageListener(this);
        
        if (scene != null)
            scene.removeListener(this);
    }


    /**
     * handle scene events
     */
    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case SCENE_VIEW_CHANGED:
            case ITEM_VISIBILITY_CHANGED:
            case ITEM_STYLE_CHANGED:
            case PROVIDER_DATA_CHANGED:
                refreshViewAsync();
                break;                
        }
    }


    /**
     * handle page events (new scene loaded)
     */
    public void pageActivated(IWorkbenchPage page)
    {
        assignScene();
    }


    public void pageClosed(IWorkbenchPage page)
    {
    }


    public void pageOpened(IWorkbenchPage page)
    {        
    }    
}