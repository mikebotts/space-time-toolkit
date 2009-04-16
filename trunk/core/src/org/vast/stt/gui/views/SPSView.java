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

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.SPS.SPSWidget;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;


/**
 * <p><b>Title:</b><br/>
 * WCSTView
 * </p>
 *
 * <p><b>Description:</b><br/>
 * View for WCSTWidget (WCS Transaction GUI)
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Tony Cook
 * @date Oct 8, 2008
 * @version 1.0
 */
public class SPSView extends DataItemView
{
	public static final String ID = "STT.SPSView";
	protected SPSWidget spsWidget;
	
    
	@Override
	public void createPartControl(Composite parent)
    {
		spsWidget = new SPSWidget(parent);
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
        if (item != dataItem)
        {
            if (item != null)
            {
                item.removeListener(this);
                item.getDataProvider().getSpatialExtent().removeListener(this);
            }
            
            item = dataItem;
            
            if (item != null)
            {
                item.addListener(this);
                item.getDataProvider().getSpatialExtent().addListener(this);
            }
        }
    }


    @Override
    public void updateView()
    {
        ScenePageInput pageInput = (ScenePageInput)getSite().getPage().getInput();
        if (pageInput != null)
        {
        	spsWidget.setScene((WorldScene)pageInput.getScene());        
        	//spsWidget.setDataItem(item);
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
