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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.gui.widgets.image.ImageWidget;
import org.vast.stt.project.tree.DataItem;


/**
 * <p><b>Title:</b><br/>
 * WCSTView
 * </p>
 *
 * <p><b>Description:</b><br/>
 * View for displaying DataItems that contain images.  Introduced for OWS7 SFE 
 * demo.  May be expanded for video...
 * </p>
 *
 * <p>Copyright (c) 2010</p>
 * @author Tony Cook
 * @date Jun 10, 2010
 * @version 1.0
 */
public class ImageView  extends ViewPart //implements STTEventListener
{
	public static final String ID = "STT.ImageView";
	protected ImageWidget widget;
    
	@Override
	public void createPartControl(Composite parent)
    {
		widget = new ImageWidget(parent);
	}

	
    @Override
	public void dispose()
    {
        //  Do any cleanup here
		super.dispose();
	}
    
    public void addDataItem(DataItem dataItem)
    {
    	widget.addDataItem(dataItem);
    }


	@Override
	public void setFocus() {
	}

   
}
