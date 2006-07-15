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
import org.vast.stt.gui.widgets.DataProcess.DataProcessWidget;


/**
 * <p><b>Title:</b><br/>
 * Data Provider View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * View for managing options of DataProviders
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Mar 2, 2006
 * @version 1.0
 *
 * 
 */
public class DataProviderView extends DataItemView
{
    public static final String ID = "STT.DataProviderView";
    protected DataProcessWidget procWidget;


    @Override
    public void createPartControl(Composite parent)
    {
        procWidget = new DataProcessWidget(parent);
        super.createPartControl(parent);
    }

    
    @Override
    public void dispose()
    {
        procWidget.close();
        super.dispose();
    }
    

    @Override
    public void updateView()
    {
        procWidget.setDataItem(this.item);
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
            case ITEM_OPTIONS_CHANGED:
            case PROVIDER_OPTIONS_CHANGED:
                refreshViewAsync();
        }            
    }
}
