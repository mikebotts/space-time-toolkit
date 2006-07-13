
package org.vast.stt.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.time.TimeExtentWidget;


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
    private TimeExtentWidget timeSettingsWidget;


    @Override
    public void createPartControl(Composite parent)
    {
        timeSettingsWidget = new TimeExtentWidget(parent);
        super.createPartControl(parent);
    }


    @Override
    public void updateView()
    {
        timeSettingsWidget.setDataItem(item);
    }


    @Override
    public void clearView()
    {
        //timeSettingsWidget.disable();
    }
}
