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

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.table.TableScene;


/**
 * <p><b>Title:</b>
 * Table View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * The table view is a multiple view that can display table scenes.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public class TableView extends SceneView<TableScene> implements PaintListener, ControlListener
{
    public static final String ID = "STT.TableView";
    private Composite composite;
    
    
    public TableView()
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
    public void setScene(TableScene sc)
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
            case SCENE_VIEW_CHANGED:
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
            //Rectangle clientArea = composite.getClientArea();
            //scene.getRenderer().resizeView(clientArea.width, clientArea.height);
            
            // redraw the whole scene
            //scene.getRenderer().drawScene(scene);
        }
    }
    
    
    public void controlMoved(ControlEvent e)
    {
    }
}