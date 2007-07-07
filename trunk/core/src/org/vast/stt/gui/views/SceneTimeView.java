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
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.time.SceneTimeController;
import org.vast.stt.project.world.WorldScene;


/**
 * <p><b>Title:</b><br/>
 * Master Time View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * View for MasterTime Controller for a Scene/Workbench
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 1, 2006
 * @version 1.0
 * 
 */
public class SceneTimeView extends SceneView<WorldScene>
{
    public static final String ID = "STT.SceneTimeView";
    protected SceneTimeController masterTimeController;


    @Override
    public void createPartControl(Composite parent)
    {
        masterTimeController = new SceneTimeController(parent, (WorldScene)scene);
        getSite().getPage().addPartListener(this);
    }


    @Override
    public void updateView()
    {
        masterTimeController.setScene((WorldScene)scene);
    }


    @Override
    public void clearView()
    {
        masterTimeController.setScene(null);
    }
    
    
    @Override
    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case TIME_EXTENT_CHANGED:
                if (e.source != masterTimeController) {
                	//System.err.println();
                    refreshViewAsync();
                }
        }
    }


    @Override
    public void setScene(WorldScene sc)
    {
        super.setScene(sc);
        masterTimeController.setScene(sc);
        
        // refresh display
        refreshViewAsync();
    }
}
