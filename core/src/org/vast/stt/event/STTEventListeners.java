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
 * <p>Copyright (c) 2005</p>
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
     * @param sender
     * @param event
     */
    public void dispatchEvent(STTEvent event)
    {
//        for (int i=0; i<this.size(); i++)
//        {
//            STTEventListener next = this.get(i);
//            if (next != event.source)
//                next.handleEvent(event);
//        }
        STTEventManager.getInstance().dispatchEvent(event, this);
    }
}
