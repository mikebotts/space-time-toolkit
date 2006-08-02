package org.vast.stt.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.symbolizer.SymbolizerWidget;


/**
 * <p><b>Title:</b><br/>
 * Style View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * View for manipulating the Style/Symbolizers of DataItems
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 11, 2006
 * @version 1.0
 *
 * 
 */
public class SymbolizerView extends DataItemView
{
	public static final String ID = "STT.SymbolizerView";
	protected SymbolizerWidget symbWidget;
	
    
	@Override
	public void createPartControl(Composite parent)
    {
        symbWidget = new SymbolizerWidget(parent);
        super.createPartControl(parent);
	}

	
    @Override
	public void dispose()
    {
		symbWidget.close();
		super.dispose();
	}
    

    @Override
    public void updateView()
    {
        symbWidget.setDataItem(this.item);
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
            case ITEM_STYLE_CHANGED:
                refreshViewAsync();
        }
    }
}
