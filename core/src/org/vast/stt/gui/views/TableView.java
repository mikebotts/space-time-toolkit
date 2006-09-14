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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.table.RichTable;
import org.vast.stt.project.table.TableItem;


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
    protected ScrolledComposite mainSC;
    
    
    public TableView()
    {
    }
			

    @Override
	public void createPartControl(Composite parent)
	{
        mainSC = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        FillLayout scLayout = new FillLayout();
        scLayout.marginHeight = 0;
        scLayout.marginWidth = 0;
        mainSC.setLayout(scLayout);
        
        table = new RichTable(mainSC, SWT.NONE);
        mainSC.setContent(table);
        mainSC.setExpandVertical(true);
        mainSC.setExpandHorizontal(true);        
        mainSC.setContent(table);
        mainSC.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        super.createPartControl(parent);
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
	public void dispose()
	{
        super.dispose();
	}
	
	
	@Override
	public void setFocus()
	{
	}
    
    
    @Override
    public void updateView()
    {
        setPartName("Table: " + item.getName());
        table.drawTable(((TableItem)item).getTable());
        mainSC.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
            case ITEM_SYMBOLIZER_CHANGED:
            case PROVIDER_DATA_CHANGED:
                refreshViewAsync();
        }
    }
}