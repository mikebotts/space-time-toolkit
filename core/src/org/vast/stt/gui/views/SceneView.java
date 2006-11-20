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
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.scene.Scene;


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
public abstract class SceneView<SceneType extends Scene<?>> extends ViewPart implements IPartListener, STTEventListener
{
    protected SceneType scene;
    protected boolean refreshThreadStarted = true;
    
    protected Runnable runRefresh = new Runnable()
    {
        public void run() {refreshView();}
    };
    
    protected Thread refreshThread = new Thread()
    {
        @Override
        public void run()
        {
            try
            {
                while (refreshThreadStarted)
                {
                    synchronized(this) { wait(); }                    
                    getSite().getShell().getDisplay().syncExec(runRefresh);
                }
            }
            catch (InterruptedException e)
            {
            }
        }
    };


    public abstract void createPartControl(Composite parent);
    public abstract void updateView();
    public abstract void clearView();
    
    
    /**
     * Method called when the view needs to be refreshed 
     * (i.e. typically after a "CHANGE" event is received)
     */
    protected void refreshView()
    {
        if (scene != null)
            updateView();
        else
            clearView();
    }
    
    
    protected synchronized void refreshViewAsync()
    {
        if (!refreshThread.isAlive())
        {
            refreshThread.setName(scene.getName() + " Refresh Thread");
            refreshThreadStarted = true;
            refreshThread.start();            
        }
        
        synchronized (refreshThread)
        {
            refreshThread.notify();
        }
    }
    
    
    protected void assignScene()
    {
        ScenePageInput pageInput = (ScenePageInput)getSite().getPage().getInput();
        if (pageInput != null)
            setScene((SceneType)pageInput.getScene());
        else 
            setScene(null);
        refreshView();
    }
    
    
    public void setScene(SceneType sc)
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
    public void setFocus()
    {
        refreshView();
    }


    @Override
    public void dispose()
    {
        if (scene != null)
            scene.removeListener(this);
        
        refreshThreadStarted = false;
        super.dispose();
    }


    /**
     * handle scene events
     */
    public void handleEvent(STTEvent e)
    {
        // by default the view will refresh on all events!!
        // override this method to filter on event type
        refreshViewAsync();
    }

    
    public void partOpened(IWorkbenchPart part)
    {  
        if (part == this)
        {
            assignScene();
        }
    }
    
    
    public void partActivated(IWorkbenchPart part)
    {    
    }
    
    
    public void partBroughtToTop(IWorkbenchPart part)
    { 
    }
    
    
    public void partClosed(IWorkbenchPart part)
    {  
    }
    
    
    public void partDeactivated(IWorkbenchPart part)
    {  
    }
}