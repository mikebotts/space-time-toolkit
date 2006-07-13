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
 * <p><b>Title:</b><br/>
 * STT Event Producer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * General Interface for all event producers
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Mar 1, 2006
 * @version 1.0
 */
public interface STTEventProducer
{
	public void addListener(STTEventListener listener);
    
    
    public void removeListener(STTEventListener listener);
    
    
    public void removeAllListeners();
    
    
    public void dispatchEvent(STTEvent event);
}
