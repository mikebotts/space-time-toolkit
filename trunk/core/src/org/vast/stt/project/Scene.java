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

import java.util.ArrayList;

import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventProducer;
import org.vast.stt.renderer.Renderer;
import org.vast.stt.renderer.opengl.JOGLRenderer;


/**
 * <p><b>Title:</b><br/>
 * Scene Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current state of a scene (graphic view)
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class Scene implements STTEventProducer
{
	protected String name;
    protected Renderer renderer;
	protected ViewSettings viewSettings;
	protected TimeSettings timeSettings;
	protected DataEntryList dataList;
    protected ArrayList<STTEventListener> listeners;


    public Scene()
    {
        renderer = new JOGLRenderer();
        listeners = new ArrayList<STTEventListener>(2);
    }
    
    
	public DataEntryList getDataList()
	{
		return dataList;
	}


	public void setDataList(DataEntryList dataList)
	{
		this.dataList = dataList;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public ViewSettings getViewSettings()
	{
		return viewSettings;
	}


	public void setViewSettings(ViewSettings viewSettings)
	{
		this.viewSettings = viewSettings;
	}


	public TimeSettings getTimeSettings()
	{
		return timeSettings;
	}


	public void setTimeSettings(TimeSettings timeSettings)
	{
		this.timeSettings = timeSettings;
	}


    public void addListener(STTEventListener listener)
    {
        if (!listeners.contains(listener))
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


    /**
     * Sends an event to all registered listeners except
     * if the sender and listener are the same object.
     */
    public void dispatchEvent(Object sender, STTEvent event)
    {
        event.producer = this;
        
        for (int i=0; i<listeners.size(); i++)
        {
            STTEventListener next = listeners.get(i);
            if (next != sender)
                next.handleEvent(event);
        }        
    }


    public Renderer getRenderer()
    {
        return renderer;
    }


    public void setRenderer(Renderer renderer)
    {
        this.renderer = renderer;
    }
}
