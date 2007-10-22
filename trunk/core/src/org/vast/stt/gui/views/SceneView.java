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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.renderer.SceneRenderer;


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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public abstract class SceneView<SceneType extends Scene<? extends SceneRenderer<? extends Scene>>> extends ViewPart implements IPartListener2, STTEventListener
{
    protected SceneType scene;
    protected boolean refreshThreadStarted = false;
    protected boolean refreshRequested = false;
    protected boolean doRefresh = true;
    protected Object lock = new Object();

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
                    synchronized (lock)
                    {
                        while (!refreshRequested)
                            lock.wait();

                        if (!refreshThreadStarted)
                            return;

                        refreshRequested = false;
                    }
                    
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
        if (doRefresh)
        {
            if (scene != null)
                updateView();
            else
                clearView();
        }
    }


    protected void refreshViewAsync()
    {
        if (doRefresh)
        {
            synchronized (lock)
            {
                if (!refreshThreadStarted)
                {
                    refreshThread.setName(this.getClass().getSimpleName() + " Refresh Thread");
                    refreshThreadStarted = true;
                    refreshThread.start();
                }
    
                refreshRequested = true;
                lock.notifyAll();
            }
        }
    }


    protected void assignScene()
    {
        ScenePageInput pageInput = (ScenePageInput) getSite().getPage().getInput();
        if (pageInput != null)
            setScene((SceneType)pageInput.getScene());
        else
            setScene(null);
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
    public void dispose()
    {
        if (scene != null)
            scene.removeListener(this);

        synchronized (refreshThread)
        {
            refreshThread.interrupt();
        }

        super.dispose();
    }
    
    
    @Override
    public void setFocus()
    {       
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


    public void partOpened(IWorkbenchPartReference partRef)
    {
        if (partRef.getPart(false) == this)
            assignScene();
    }
    
    
    public void partVisible(IWorkbenchPartReference partRef)
    {
        if (partRef.getPart(false) == this)
        {
            doRefresh = true;
            refreshView();
        }
    }
    
    
    public void partHidden(IWorkbenchPartReference partRef)
    {
        if (partRef.getPart(false) == this)
            doRefresh = false;
    }


    public void partActivated(IWorkbenchPartReference partRef)
    {
    }


    public void partBroughtToTop(IWorkbenchPartReference partRef)
    {
    }


    public void partClosed(IWorkbenchPartReference partRef)
    {
    }


    public void partDeactivated(IWorkbenchPartReference partRef)
    {
    }    


    public void partInputChanged(IWorkbenchPartReference partRef)
    {
    }
    
}