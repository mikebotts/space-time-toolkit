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

package org.vast.stt.dynamics;

import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Aug 21, 2006
 * @version 1.0
 */
public class RealTimeUpdater extends TimeExtentUpdater
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
                    timeExtent.dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED), true);
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
    /**
     * Note that calling setEnabled(true) on an existing instance of realTimeUpdater
     * causes an IllegalThreadStateException
     */
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        
        if (enabled && !realtimeUpdateThread.isAlive()) {
        	try {
        		realtimeUpdateThread.start();
        	} catch (IllegalThreadStateException e) {
        		System.err.println(e);
        	}
        }
        	
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
