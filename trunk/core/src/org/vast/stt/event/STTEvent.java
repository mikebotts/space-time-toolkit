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


/**
 * <p><b>Title:</b>
 * STT Event
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Base STT Event
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 8, 2006
 * @version 1.0
 */
public class STTEvent
{
    public STTEventProducer producer;
    public Object source;
    public EventType type;
    protected STTEvent next;
	
	
    public STTEvent(Object source, EventType type)
    {
        this.type = type;
        this.source = source;
    }
    
    
    public STTEvent copy()
    {
        STTEvent event = new STTEvent(this.source, this.type);
        event.producer = this.producer;
        return event;
    }
    
    
	public String toString()
	{
		return "Event: " + this.type;
	}
}
