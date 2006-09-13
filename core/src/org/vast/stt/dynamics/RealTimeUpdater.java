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

package org.vast.stt.dynamics;

import org.vast.util.DateTime;


/**
 * <p><b>Title:</b>
 * Real Time Updater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO RealTimeUpdater type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 21, 2006
 * @version 1.0
 */
public abstract class RealTimeUpdater extends TimeExtentUpdater
{
    protected double updatePeriod;
    protected Thread realtimeUpdateThread;
    
    private class MyRunnable implements Runnable
    {
        public void run()
        {
            try
            {
                while(timeExtent != null && enabled)
                {
                    DateTime now = new DateTime();
                    timeExtent.setBaseTime(now.getJulianTime());
                    Thread.sleep((long)updatePeriod*1000);
                }
            }
            catch (InterruptedException e)
            {
                // do nothing
                // just exit thread
            }
        }
    }
    
    
    public RealTimeUpdater()
    {
        realtimeUpdateThread = new Thread(new MyRunnable());
    }
    
    
    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        
        if (enabled && !realtimeUpdateThread.isAlive())
            realtimeUpdateThread.start();
    }


    public double getUpdatePeriod()
    {
        return updatePeriod;
    }


    public void setUpdatePeriod(double updatePeriod)
    {
        this.updatePeriod = updatePeriod;
    }
}
