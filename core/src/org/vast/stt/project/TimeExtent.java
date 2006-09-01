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

package org.vast.stt.project;

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
public class TimeExtent extends org.vast.physics.TimeExtent implements STTEventProducer
{
    protected STTEventListeners listeners;
    protected TimeExtentUpdater updater;
    protected double absoluteTime = 0.0;
    protected boolean useAbsoluteTime = false;

    
    public TimeExtent()
    {
        listeners = new STTEventListeners(1);
    }


    public TimeExtent(double baseJulianTime)
    {
        super(baseJulianTime);
    }
    
    
    public double getAbsoluteTime()
    {
        return absoluteTime;
    }


    public void setAbsoluteTime(double absoluteTime)
    {
        this.absoluteTime = absoluteTime;
    }


    public boolean getUseAbsoluteTime()
    {
        return useAbsoluteTime;
    }


    public void setUseAbsoluteTime(boolean useAbsoluteTime)
    {
        this.useAbsoluteTime = useAbsoluteTime;
    }

        
    public TimeExtentUpdater getUpdater()
    {
        return updater;
    }


    public void setUpdater(TimeExtentUpdater updater)
    {
        this.updater = updater;
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
