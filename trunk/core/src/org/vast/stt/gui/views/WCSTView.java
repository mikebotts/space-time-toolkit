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
import org.vast.stt.gui.widgets.WCST.WCSTWidget;
import org.vast.stt.gui.widgets.bbox.BboxWidget;
import org.vast.stt.gui.widgets.catalog.CatalogWidget;


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
 * @date Mar 15, 2008
 * @version 1.0
 */
public class WCSTView extends DataItemView
{
	public static final String ID = "STT.WCSTView";
	protected WCSTWidget wcstWidget;
	
    
	@Override
	public void createPartControl(Composite parent)
    {
		wcstWidget = new WCSTWidget(parent);
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
