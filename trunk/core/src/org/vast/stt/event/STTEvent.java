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


/**
 * <p><b>Title:</b>
 * STT Event
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Base STT Event
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Jul 8, 2006
 * @version 1.0
 */
public class STTEvent
{
    public STTEventProducer producer;
    public Object source;
    public EventType type;
	
	
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
    
    
    public boolean equals(STTEvent e)
    {
        if (e.producer != this.producer)
        	return false;
        
        if (e.source != this.source)
        	return false;
        
        if (e.type != this.type)
        	return false;
                
        return true;
    }
    
    
	public String toString()
	{
		return "Event: " + this.type;
	}
}
