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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.table.RichTable;
import org.vast.stt.project.table.TableItem;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b>
 * World View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * The world view is the 2D/3D view where the data items of
 * the scene are rendered using the provided renderer.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public class TableView extends DataItemView
{
	public static final String ID = "STT.TableView";
    protected RichTable table;
    
    
    public TableView()
    {
    }
			

    @Override
	public void createPartControl(Composite parent)
	{
        table = new RichTable(parent, SWT.NONE);
	}
    	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
        // add show target action to toolbar
//		IAction ShowTargetAction = new Action()
//		{
//			public void run()
//			{
//				//boolean targetshown = scene.getViewSettings().isShowCameraTarget();
//                //scene.getViewSettings().setShowCameraTarget(!targetshown);
//                //scene.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED));
//			}
//		};
//        ShowTargetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
//        ShowTargetAction.setToolTipText("Toggle Target Tripod");
//		site.getActionBars().getToolBarManager().add(ShowTargetAction);
	}
	   
    
    @Override
    public void updateView()
    {
        setPartName("Table: " + item.getName());
        table.setTable((TableItem)item, ((TableItem)item).getTableInfo());
        table.updateTable();
    }
    
    
    @Override
    public void clearView()
    {
        
    }
    
    
    @Override
    public void handleEvent(STTEvent event)
    {       
        switch (event.type)
        {
            case ITEM_SYMBOLIZER_CHANGED:
                Symbolizer symbolizer = (Symbolizer)event.source;
                table.updateSymbolizer(symbolizer);
                
            case PROVIDER_DATA_CHANGED:
                refreshViewAsync();
        }
    }


    @Override
    public void setDataItem(DataItem dataItem)
    {
        super.setDataItem(dataItem);
        TableItem tableItem = (TableItem)dataItem;
        table.setTable(tableItem, tableItem.getTableInfo());
        refreshViewAsync();
    }
}