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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.apps.STTPlugin;
import org.vast.stt.gui.widgets.catalog.CapabilitiesWidget;
import org.vast.stt.gui.widgets.catalog.EditCapServerDialog;

/**
 * <p><b>Title:</b>
 *  CapabilitiesView
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  View form managing Capabilities server list and adding 
 *  dynamically adding items to scene
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapabilitiesView extends ViewPart// implements IPageListener, STTEventListener
{
	public static final String ID = "STT.CapabilitiesView";
	protected CapabilitiesWidget capabilitiesWidget;
    private ImageDescriptor editServerImg; 
	
    
	@Override
	public void createPartControl(Composite parent)
    {
		capabilitiesWidget = new CapabilitiesWidget(parent);
		//  TODO anything else?
    }

    @Override
    public void setFocus()
    {
    }
	   
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
        
		//  NOTE:  I had editServer as a Toolbar thing, but changed my mind about 
		//         using it this way
		
//        // load menu images
//        editServerImg = STTPlugin.getImageDescriptor("icons/fitScene.gif");
//
//        // editServer action
//        IAction editServerAction = new Action()
//        {
//            public void run()
//            {
//            	new EditCapServerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
//            }
//        };
//        editServerAction.setImageDescriptor(editServerImg);
//        editServerAction.setToolTipText("Edit your Capabilities server list");
//        site.getActionBars().getToolBarManager().add(editServerAction);
	}
	
	
	@Override
	public void dispose()
	{
        super.dispose();
	}

	
}

