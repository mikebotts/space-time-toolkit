package org.vast.stt.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.catalog.CatalogWidget;


/**
 * <p><b>Title:</b><br/>
 * CatalogView
 * </p>
 *
 * <p><b>Description:</b><br/>
 * View for searching Catalog and Capability docs
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 16, 2006
 * @version 1.0
 */
public class CatalogView extends DataItemView
{
	public static final String ID = "STT.CatalogView";
	protected CatalogWidget catalogWidget;
	
    
	@Override
	public void createPartControl(Composite parent)
    {
		catalogWidget = new CatalogWidget(parent, 0x0);
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
