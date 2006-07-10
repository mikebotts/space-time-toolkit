package org.vast.stt.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.styler.StyleWidget;


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
public class StyleView extends DataItemView
{
	public static final String ID = "STT.StyleView";
	protected StyleWidget styleWidget;
	
    
	@Override
	public void createPartControl(Composite parent)
    {
        styleWidget = new StyleWidget(parent);
        super.createPartControl(parent);
	}

	
    @Override
	public void dispose()
    {
		styleWidget.close();
		super.dispose();
	}
    

    @Override
    public void updateView()
    {
        styleWidget.setDataItem(this.item);
    }
    
    
    @Override
    public void clearView()
    {
        
    }
}
