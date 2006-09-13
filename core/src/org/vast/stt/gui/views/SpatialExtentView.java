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
import org.vast.stt.gui.widgets.bbox.BboxWidget;


/**
 * <p><b>Title:</b>
 * Spatial Extent View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * View Class for modding geographic region of a dataItem.
 * TODO  Could support other formats (dd'mm"ss, dd'mm.mm")  like the old CompassPanel, but 
 *       I need to figure out how to enforce editor behavior on SWT Text Widgets to make this work.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 13, 2005
 * @version 1.0
 */
public class SpatialExtentView extends DataItemView
{
    public static final String ID = "STT.SpatialExtentView";
    BboxWidget bboxWidget;


    public void createPartControl(Composite parent)
    {
        bboxWidget = new BboxWidget(parent);
        super.createPartControl(parent);
    }


    @Override
    public void updateView()
    {
        bboxWidget.setDataItem(item);        
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
            case PROVIDER_SPATIAL_EXTENT_CHANGED:
                if (e.source != bboxWidget)
                    refreshViewAsync();
        }
    }
}
