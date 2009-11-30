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

import java.util.List;


public class STTEventManager implements Runnable
{
    private static STTEventManager eventManager;
    private Thread dispatchThread;
    private boolean started;
    private DispatchJob firstJob;
    private DispatchJob lastJob;

    
    private class DispatchJob
    {
        public STTEvent event;
        public List<STTEventListener> listeners;
        public DispatchJob nextJob;
    }
    
    
    private STTEventManager()
    {
        start();
    }
    
    
    public synchronized static STTEventManager getInstance()
    {
        if (eventManager != null)
            return eventManager;
        
        eventManager = new STTEventManager();
        return eventManager;
    }


    public void run()
    {
        try
        {
            while (started)
            {
                DispatchJob currentJob;
                
                synchronized (this) 
                {
                    while (firstJob == null)
                        wait();
                    currentJob = firstJob;
                    firstJob = firstJob.nextJob;
                }
                
                for (int i=0; i<currentJob.listeners.size(); i++)
                {
                    STTEventListener next = currentJob.listeners.get(i);
                    if (next != currentJob.event.source)
                        next.handleEvent(currentJob.event);
                }
            }
        }
        catch (InterruptedException e)
        {
        }
    }
    
    
    public synchronized void dispatchEvent(STTEvent event, List<STTEventListener> listeners)
    {
        DispatchJob newJob = new DispatchJob();
        newJob.event = event;
        newJob.listeners = listeners;
        
        if (firstJob == null)
        {
            firstJob = newJob;
            lastJob = newJob;
        }
        else
        {
            lastJob.nextJob = newJob;
            lastJob = newJob;
        }
        
        notify();
    }
    
    
    public synchronized void mergeEvent(STTEvent event, List<STTEventListener> listeners)
    {
    	boolean found = false;
    	
    	// look for similar event
    	DispatchJob nextJob = firstJob;
    	while (nextJob != null)
    	{
    		STTEvent e = nextJob.event;
    		if (e.equals(event))
    		{
    			found = true;
    			break;
    		}
    		nextJob = nextJob.nextJob;
    	}
    	
    	// dispatch only if queue doesn't have an event of this kind
    	if (!found)
    		this.dispatchEvent(event, listeners);
    }
    
    
    public void start()
    {
        if (!started)
        {
            dispatchThread = new Thread(this, "STT Event Dispatcher");
            started = true;
            dispatchThread.start();
        }
    }
    
    
    public void stop()
    {
        started = false;
    }
}
