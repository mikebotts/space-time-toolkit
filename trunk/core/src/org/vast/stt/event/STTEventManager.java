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

import java.util.ArrayList;


/**
 * STTEventManager.java
 *
 * The thing that'll manage STTEvents.
 *
 * Modified:
 *   16 Dec 1998 - CSD - Removed sleep in loop.  Uses wait() and notify()
 *                       for better (at least theoretically) response time
 *                       and less CPU usage.
 *
 *  26 Aug 2000 - Mike Botts - Made STTEventManger a Singleton to stop the
 *                       crazy demand for passing eventManger around; now
 *                       simply use STTEventManager.getInstance() whenever
 *                       you need access
 *  Dec 00 - Jan 01 - Mike Botts and Kishore - cleaned up events in general.
 *                       removed need for separate subEvent classes and
 *                       removed need for STTEventQueItem
 *  Aug 27, 2001 - Mike Botts - added use of ThreadBarrier class in order to
 *                       synchronize time event result. Added dispatchAndWait
 *                       class
 *  28 Oct 2004 - TC - added Projection Event support - need to ensure that
 *                        all projection changes use this.  ProjectionEvents
 *                        have been in the project for a while, but their use
 *                        was previously circumvented
 *    1 Feb 2005 - TC - Changed to "pseudo-singleton", which still acts as a
 *                      Singleton for client calls, but maintains a hashtable
 *                      with multiple intstances for server calls.
 */

public class STTEventManager implements Runnable
{
	private STTEvent queue;
	private STTEvent queueEnd;
	private ArrayList<STTEventListener> projectListeners = new ArrayList<STTEventListener>();
	private ArrayList<STTEventListener> resourceListeners = new ArrayList<STTEventListener>();
	private ArrayList<STTEventListener> sceneListeners = new ArrayList<STTEventListener>();
	private ArrayList<STTEventListener> timeListeners = new ArrayList<STTEventListener>();
	private ArrayList<STTEventListener> viewListeners = new ArrayList<STTEventListener>();
	private ArrayList<STTEventListener> dataItemListeners = new ArrayList<STTEventListener>();

	public boolean dispatchTimeEvent = true; // added 11/01/04. AR

	// The event dispatch thread will wait() on this object
	// I could just synchronize on "this" but having this object
	// with a descriptive var name makes it clearer at little
	// expense.
	private Object moreEventsLock;
	private Thread dispatchThread;
	private boolean dispatchThreadRunning;


	// Mike Botts - 8/26/00: made this a Singleton CONSTRUCTOR
	public STTEventManager()
	{
		moreEventsLock = new Object();
		dispatchThread = new Thread(this, "STT Event Thread");
		startDispatchThread();
	}


	public void postEvent(STTEvent evt)
	{
		// We don't want the dispatch loop messing with the
		// queue while we're trying to add to it.  So both
		// chunks of code that mess with the queue are synched
		// on moreEventsLock (see run method).
		synchronized (moreEventsLock)
		{
			if (queue == null)
			{
				queue = evt;
				queueEnd = queue;
			}
			else
			{
				queueEnd.next = evt;
				queueEnd = evt;
                //queue = evt; //does it improve perf for redraw events?
			}

			// If we wanted to coalesce duplicate event types,
			// we'd do it right here.
			// * * *

			// Let the dispatch thread know that more events are available
			// if he's waiting on them.
			moreEventsLock.notify();
		}
	}


	// Start the dispatch thread when the components are first activated in main program
	// and stop it when you are done.
	public void startDispatchThread()
	{
		dispatchThreadRunning = true;
		dispatchThread.start();
	}


	public void stopDispatchThread()
	{
		synchronized (moreEventsLock)
		{
			dispatchThreadRunning = false;
			moreEventsLock.notify();
		}
	}


	private STTEvent next()
	{
		STTEvent evt = queue;
		queue = queue.next;
		if (queue == null)
			queueEnd = null;
		return evt;
	}


	/***********************/
	/* Event dispatch loop */
	/***********************/
	public void run()
	{
		while (dispatchThreadRunning)
		{
			try
			{
				STTEvent nextEvent;
				
				synchronized (moreEventsLock)
				{
					// If the event queue is empty, then we want to wait for more
					// events to be posted.
					if (queue == null)
					{
						moreEventsLock.wait();
					}
					
					nextEvent = next();
				}
				
				//  Dispense with the next event.
				//  Added queue null check since stopDispatchThread() now calls
				//  lock.notify() and queue will probably be null at that time.
				//  I use this for server process, which needs to start and stop
				//  multiple STTEventManagers.  Otherwise, dispatchThread was left
				//  running, even when the parent manager was set to null.  TC, 2/2/05
				//if (queue != null)
					dispatchEvent(nextEvent);
			}
			catch (Exception e)
			{
				// We could get InterruptedExceptions from the code above,
				// but we also want to trap exceptions that may occur
				// in the event handlers so that the dispatch thread won't
				// die, hence the "catch(Exception e)" as opposed to trapping
				// specific ones.
				System.err.println("Exception occurred dispatching events:" + e);
				e.printStackTrace(System.err);
			}
		}
	}

	
	private synchronized void dispatchEvent(STTEvent evt)
	{
		switch (evt.getSection())
		{		
			case PROJECT:
				for (int i=0; i<projectListeners.size(); i++)
					projectListeners.get(i).handleEvent(evt);
				break;
				
			case RESOURCE:
				for (int i=0; i<resourceListeners.size(); i++)
					resourceListeners.get(i).handleEvent(evt);
				break;
				
			case SCENE_VIEW:
				for (int i=0; i<viewListeners.size(); i++)
					viewListeners.get(i).handleEvent(evt);
				break;
				
			default:
				throw new RuntimeException("Unrecognized event type: " + evt.getSection());
		}
	}


	// Register/Remove Listeners ---------------------------------------------
	public synchronized void addProjectListener(STTEventListener listener)
	{
		projectListeners.add(listener);
	}


	public synchronized void removeProjectListener(STTEventListener listener)
	{
		projectListeners.remove(listener);
	}
	
	
	public synchronized void addResourceListener(STTEventListener listener)
	{
		resourceListeners.add(listener);
	}


	public synchronized void removeResourceListener(STTEventListener listener)
	{
		resourceListeners.remove(listener);
	}
	
	
	public synchronized void addSceneListener(STTEventListener listener)
	{
		sceneListeners.add(listener);
	}


	public synchronized void removeSceneListener(STTEventListener listener)
	{
		sceneListeners.remove(listener);
	}
	
	
	public synchronized void addSceneTimeListener(STTEventListener listener)
	{
		timeListeners.add(listener);
	}


	public synchronized void removeSceneTimeListener(STTEventListener listener)
	{
		timeListeners.remove(listener);
	}
	
	
	public synchronized void addSceneViewListener(STTEventListener listener)
	{
		viewListeners.add(listener);
	}


	public synchronized void removeSceneViewListener(STTEventListener listener)
	{
		viewListeners.remove(listener);
	}
	
	
	public synchronized void addDataItemListener(STTEventListener listener)
	{
		dataItemListeners.add(listener);
	}


	public synchronized void removeDataItemListener(STTEventListener listener)
	{
		dataItemListeners.remove(listener);
	}


	//  Clear all listener vectors (usually called when a new project is opened)
	//  TC, 12/2/04
	public synchronized void clearAllListeners()
	{
		projectListeners.clear();
		resourceListeners.clear();
		sceneListeners.clear();
		timeListeners.clear();
		dataItemListeners.clear();
	}
}
