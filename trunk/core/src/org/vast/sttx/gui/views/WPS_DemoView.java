package org.vast.sttx.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.views.DataItemView;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.sttx.gui.widgets.WPS_Demo.WPS_DemoWidget;

/**
 * <p>Title: WPS_DemoView.java</p>
 * <p>Description:  </p>
 * @author Tony Cook
 * @since Jun 6, 2010
 */

public class WPS_DemoView extends DataItemView
{
	public static final String ID = "STT.WPS_DemoView";
	protected WPS_DemoWidget wpsWidget;
	
    
	@Override
	public void createPartControl(Composite parent)
    {
		wpsWidget = new WPS_DemoWidget(parent);
        ScenePageInput pageInput = (ScenePageInput)getSite().getPage().getInput();
		if(pageInput != null)
			wpsWidget.setScene((WorldScene)pageInput.getScene());   
		super.createPartControl(parent);
	}

	
    @Override
	public void dispose()
    {
        //  Do any cleanup here
		super.dispose();
	}
    

    @Override
    public void setDataItem(DataItem dataItem)
    {
    	//  Better to make this NOT extend DataItemView and assocaite the items
    	//  via Project file or some other way (like the Image Viewer works)
//        if (item != dataItem)
//        {
//            if (item != null)
//            {
//                item.removeListener(this);
//                item.getDataProvider().getSpatialExtent().removeListener(this);
//            }
//            
//            item = dataItem;
//            
//            if (item != null)
//            {
//                item.addListener(this);
//                item.getDataProvider().getSpatialExtent().addListener(this);
//            }
//        }
    }


    @Override
    public void updateView()
    {
        ScenePageInput pageInput = (ScenePageInput)getSite().getPage().getInput();
        if (pageInput != null)
        {
        	wpsWidget.setScene((WorldScene)pageInput.getScene());        
        }
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
