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
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.event;

import java.util.ArrayList;


/**
 * <p><b>Title:</b>
 * STT Event Listeners
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Helper for implementing Event Producers.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Jul 13, 2006
 * @version 1.0
 */
public class STTEventListeners extends ArrayList<STTEventListener>
{
	private final static long serialVersionUID = 0;
    
    
    public STTEventListeners()
    {
        super();
    }
    
    
    public STTEventListeners(int initialSize)
    {
        super(initialSize);
    }
    
    
    @Override
    public boolean add(STTEventListener listener)
    {
        if (!this.contains(listener))
            super.add(listener);
        return true;
    }
    
    
    /**
     * Dispatch event to all listeners in the list except the one
     * specified as the source. (to avoid event loops)
     * @param event
     * @param merge set to true for merging event w/ previous ones of the same kind
     */
    public void dispatchEvent(STTEvent event, boolean merge)
    {
//        for (int i=0; i<this.size(); i++)
//        {
//            STTEventListener next = this.get(i);
//            if (next != event.source)
//                next.handleEvent(event);
//        }
        //System.out.println("Received: " + event + " from " + event.source);
        if (merge)
        	STTEventManager.getInstance().mergeEvent(event, this);
        else
        	STTEventManager.getInstance().dispatchEvent(event, this);
    }
}
