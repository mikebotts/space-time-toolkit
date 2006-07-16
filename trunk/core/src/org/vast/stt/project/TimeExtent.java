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
public class TimeExtent implements STTEventProducer
{
    //members
    private double baseTime;
    private double timeBias = 0;
    private double timeStep = 1;
    private double leadTimeDelta = 0, lagTimeDelta = 0;
    private double leadCacheDelta = 0, lagCacheDelta = 0; // added by meb: 02/02/03
    private boolean useAbsoluteTime = false; //  overrides STT clock, if true
    private double absoluteTime;
    protected STTEventListeners listeners;


    // constructors
    public TimeExtent()
    {
    }


    public TimeExtent(double baseJulianTime)
    {
        this.baseTime = baseJulianTime;
    }


    public TimeExtent(double baseJulianTime, double timeBiasSeconds, double timeStepSeconds, double leadTimeDeltaSeconds, double lagTimeDeltaSeconds)
    {

        this.baseTime = baseJulianTime;
        this.timeBias = timeBiasSeconds;
        this.timeStep = timeStepSeconds;
        this.leadTimeDelta = Math.abs(leadTimeDeltaSeconds);
        this.lagTimeDelta = Math.abs(lagTimeDeltaSeconds);
    }


    //  Copy constructor
    public TimeExtent(TimeExtent ts)
    {
        this(ts.getBaseTime(), ts.getTimeBias(), ts.getTimeStep(), ts.getLeadTimeDelta(), ts.getLagTimeDelta());
        this.leadCacheDelta = ts.getLeadCacheDelta();
        this.lagCacheDelta = ts.getLagCacheDelta();
        this.absoluteTime = ts.getAbsoluteTime();
        this.useAbsoluteTime = ts.getUseAbsoluteTime();
    }


    //set methods
    public void setBaseTime(double baseJulianTime)
    {
        this.baseTime = baseJulianTime;
    }


    public void setTimeBias(double timeBiasSeconds)
    {
        this.timeBias = timeBiasSeconds;
    }


    public void setTimeStep(double seconds)
    {
        this.timeStep = seconds;
    }


    public void setLeadTimeDelta(double seconds)
    {
        this.leadTimeDelta = Math.abs(seconds);
    }


    public void setLagTimeDelta(double seconds)
    {
        this.lagTimeDelta = Math.abs(seconds);
    }


    public void setDeltaTimes(double leadDeltaSeconds, double lagDeltaSeconds)
    {
        this.leadTimeDelta = Math.abs(leadDeltaSeconds);
        this.lagTimeDelta = Math.abs(lagDeltaSeconds);
    }


    // added by meb: 02/02/03
    // adding cache setting to TimeSetting allows suggestions for cache amounts
    public void setLeadCacheDelta(double seconds)
    {
        this.leadCacheDelta = Math.abs(seconds);
    }


    public void setLagCacheDelta(double seconds)
    {
        this.lagCacheDelta = Math.abs(seconds);
    }


    public void setCacheDelta(double leadCacheDeltaSeconds, double lagCacheDeltaSeconds)
    {
        this.leadCacheDelta = Math.abs(leadCacheDeltaSeconds);
        this.lagCacheDelta = Math.abs(lagCacheDeltaSeconds);
    }


    public void setUseAbsoluteTime(boolean b)
    {
        useAbsoluteTime = b;
    }


    public void setAbsoluteTime(double t)
    {
        absoluteTime = t;
    }


    //get methods
    /** getBaseTime() - return baseTime without applying bias */
    public double getBaseTime()
    {
        return baseTime;
    }


    /** getAdjustedTime() - return baseTime or absTime with bias applied */
    public double getAdjustedTime()
    {
        if (useAbsoluteTime)
            return (absoluteTime + timeBias);
        else
            return (baseTime + timeBias);
    }


    public double getTimeBias()
    {
        return timeBias;
    }


    public double getTimeStep()
    {
        return timeStep;
    }


    public double getLeadTimeDelta()
    {
        return leadTimeDelta;
    }


    public double getLagTimeDelta()
    {
        return lagTimeDelta;
    }


    public double getLeadCacheDelta()
    {
        return leadCacheDelta;
    }


    public double getLagCacheDelta()
    {
        return lagCacheDelta;
    }


    public double getTimeRange()
    {
        return (leadTimeDelta + lagTimeDelta);
    }


    public double getAdjustedLeadTime()
    {
        if (useAbsoluteTime)
            return (absoluteTime + timeBias + leadTimeDelta);
        else
            return (baseTime + timeBias + leadTimeDelta);
    }


    public double getAdjustedLagTime()
    {
        if (useAbsoluteTime)
            return (absoluteTime + timeBias - lagTimeDelta);
        else
            return (baseTime + timeBias - lagTimeDelta);
    }


    public double getAbsoluteTime()
    {
        return absoluteTime;
    }


    public boolean getUseAbsoluteTime()
    {
        return useAbsoluteTime;
    }


    /** calculates times based on current time settings, always assuring
     that both endpoints are included even if an uneven time step occurs
     at the end  */
    public double[] getTimes()
    {
        double time = getAdjustedLeadTime();
        double lagTime = getAdjustedLagTime();

        double timeRange = Math.abs(time - lagTime);
        double remainder = timeRange % timeStep;
        int steps = (int) (timeRange / timeStep) + 1;

        // added fix if timeStep = 0.0
        // just return adjusted time, AR 09/09/04
        if (timeStep == 0.0)
            return new double[] { time };

        double[] times;
        if (remainder != 0.0)
        {
            times = new double[steps + 1];
            times[steps] = lagTime;
        }
        else
            times = new double[steps];

        for (int i = 0; i < steps; i++)
            times[i] = time - i * timeStep;
        return times;
    }


    public int getNumberOfSteps()
    {
        return (int) ((leadTimeDelta + lagTimeDelta) / timeStep);
    }


    public String toString()
    {
        String tString = new String("TimeSettings dump:");

        tString += "\n\t\tbaseTime = " + baseTime;
        tString += "\n\t\ttimeBias = " + timeBias;
        tString += "\n\t\ttimeStep = " + timeStep;
        tString += "\n\t\tleadTimeDelta = " + leadTimeDelta;
        tString += "\n\t\tlagTimeDelta = " + lagTimeDelta;

        return tString;
    }


    public TimeExtent copy()
    {
        return new TimeExtent(baseTime, timeBias, timeStep, leadTimeDelta, lagTimeDelta);
    }


    // test if everything equal
    public boolean equals(TimeExtent ts)
    {
        boolean result = false;
        if (baseTime == ts.getBaseTime())
            if (timeBias == ts.getTimeBias())
                if (leadTimeDelta == ts.getLeadTimeDelta())
                    if (lagTimeDelta == ts.getLagTimeDelta())
                        if (timeStep == ts.getTimeStep())
                            if (absoluteTime == ts.getAbsoluteTime())
                                result = true;
        return result;
    }


    // returns true if time range is equal
    public boolean compareTimeRange(TimeExtent ts)
    {
        boolean result = false;
        if (this.getAdjustedLagTime() == ts.getAdjustedLagTime())
            if (this.getAdjustedLeadTime() == ts.getAdjustedLeadTime())
                result = true;
        return result;
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


    public void dispatchEvent(STTEvent event)
    {
        event.producer = this;
        listeners.dispatchEvent(event);
    }
}
