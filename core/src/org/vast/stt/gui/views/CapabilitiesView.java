/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.vast.stt.gui.widgets.catalog.CapabilitiesWidget;


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
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapabilitiesView extends DataItemView //ViewPart// implements IPageListener, STTEventListener
{
	public static final String ID = "STT.CapabilitiesView";
	protected CapabilitiesWidget capabilitiesWidget;
    protected ImageDescriptor editServerImg; 
	
    
	@Override
	public void createPartControl(Composite parent)
    {
		capabilitiesWidget = new CapabilitiesWidget(parent);
		//  TODO anything else?
		super.createPartControl(parent);
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
    public void updateView()
    {
    	//CatalogWidget.setDataItem(this.item);
    }
	    
   @Override
   public void clearView()
   {
       
   }
   
   
	@Override
	public void dispose()
	{
        super.dispose();
	}

	
}

