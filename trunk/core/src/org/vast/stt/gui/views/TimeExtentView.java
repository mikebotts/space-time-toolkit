
package org.vast.stt.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.time.TimeExtentController;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b><br/>
 * TimeExtentView
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	View for manipulating the TimeSettings of DataItems.   
 *
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 19, 2005
 * @version 1.0
 * 
 */
public class TimeExtentView extends DataItemView
{
    public static final String ID = "STT.TimeExtentView";
    private TimeExtentController timeExtentController;

    @Override
    public void createPartControl(Composite parent)
    {
    	timeExtentController = new TimeExtentController(parent);
        super.createPartControl(parent);
    }
    
    
    @Override
    public void setDataItem(DataItem dataItem)
    {
        if (item != dataItem)
        {
            if (item != null)
            {
                item.removeListener(this);
                item.getDataProvider().getTimeExtent().removeListener(this);
            }
            
            item = dataItem;
            
            if (item != null)
            {
                item.addListener(this);
                item.getDataProvider().getTimeExtent().addListener(this);
            }
        }
    }


    @Override
    public void updateView()
    {
    	timeExtentController.setDataItem(item);
    }


    @Override
    public void clearView()
    {
        //timeSettingsWidget.disable();
    }
    
    
    @Override
    public void handleEvent(STTEvent e)
    {       
        switch (e.type)
        {
            case ITEM_OPTIONS_CHANGED:
            case TIME_EXTENT_CHANGED:
            	//  Need to filter events from the TimeExentWidget, and its MasterTimeWidget, probably
                //if (e.source != timeExtentController.)
                    refreshViewAsync();
        }
    }
}
