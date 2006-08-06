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

import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;
import org.vast.util.DateTime;


/**
 * <p><b>Title:</b><br/>
 * Scene Time Settings
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Contains information about a scene time settings
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public class TimeSettings implements STTEventProducer
{
    protected DateTime currentTime;
    protected double leadTime;
    protected double lagTime;
    protected double stepTime;
    protected boolean realTime;
    protected STTEventListeners listeners;


    public TimeSettings()
    {
        listeners = new STTEventListeners(1);
    }
    
    
    public DateTime getCurrentTime()
    {
        return currentTime;
    }


    public void setCurrentTime(DateTime currentTime)
    {
        this.currentTime = currentTime;
    }


    public double getLagTime()
    {
        return lagTime;
    }


    public void setLagTime(double lagTime)
    {
        this.lagTime = lagTime;
    }


    public double getLeadTime()
    {
        return leadTime;
    }


    public void setLeadTime(double leadTime)
    {
        this.leadTime = leadTime;
    }


    public double getStepTime()
    {
        return stepTime;
    }


    public void setStepTime(double timeStep)
    {
        this.stepTime = timeStep;
    }
    
    
    public boolean isRealTime()
    {
        return realTime;
    }


    public void setRealTime(boolean realTime)
    {
        this.realTime = realTime;
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
