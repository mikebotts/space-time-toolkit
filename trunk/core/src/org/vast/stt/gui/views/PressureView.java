package org.vast.stt.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.catalog.CatalogWidget;
import org.vast.stt.gui.widgets.smart.PressureWidget;


/**
 * <p><b>Title:</b><br/>
 * PressureView
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 21, 2007
 * @version 1.0
 */
public class PressureView extends DataItemView
{
	public static final String ID = "STT.PressureView";
    
	@Override
	public void createPartControl(Composite parent)
    {
		PressureWidget pressureWidget = new PressureWidget(parent);
        super.createPartControl(parent);
	}

	
    @Override
	public void dispose()
    {
        //  Do any cleanup here
		super.dispose();
	}
    

    @Override
    public void updateView()
    {
    	//CatalogWidget.setDataItem(this.item);
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
        }
    }
}
