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

package org.vast.stt.provider;

import org.vast.physics.TimeExtent;
import org.vast.stt.dynamics.TimeExtentUpdater;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;


/**
 * <p><b>Title:</b><br/>
 * Time Extent
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Class for storing the definition of a spatial domain.
 * This can include an absolute time, time bias (deviation from abs time),
 * time step, and timeBefore and timeAfter deltas.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook, Mike Botts, Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class STTTimeExtent extends TimeExtent implements STTEventProducer
{
    protected STTEventListeners listeners;
    protected TimeExtentUpdater updater;

    
    public STTTimeExtent()
    {
        listeners = new STTEventListeners(1);
    }


    public STTTimeExtent(double baseJulianTime)
    {
        super(baseJulianTime);
    }

        
    public TimeExtentUpdater getUpdater()
    {
        return updater;
    }


    public void setUpdater(TimeExtentUpdater updater)
    {
        this.updater = updater;
        updater.setTimeExtent(this);
    }

    
    public void addListener(STTEventListener listener)
    {
        listeners.add(listener);
    }


    public void removeListener(STTEventListener listener)
    {
        listeners.remove(listener);
    }


    public void removeAllListeners()
    {
        listeners.clear();
    }
    
    
    public boolean hasListeners()
    {
        return !listeners.isEmpty();
    }


    public void dispatchEvent(STTEvent event)
    {
        event.producer = this;
        listeners.dispatchEvent(event);
    }
}
