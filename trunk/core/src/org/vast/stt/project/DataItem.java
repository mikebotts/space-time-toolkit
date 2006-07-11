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

import java.util.*;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventProducer;
import org.vast.stt.util.SpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * DataItem Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a Data item present in a scene. It is typically
 * a subset (in time, space, ...) of a given resource data set, 
 * which data is obtained through a DataProvider.
 * It also contains one or more style helpers used to 
 * render/display the data.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 3, 2005
 * @version 1.0
 */
public class DataItem implements DataEntry, STTEventProducer
{
	protected String name;
	protected boolean enabled = true;
	protected Hashtable<String, Object> options;
	protected DataProvider dataProvider;
	protected DataStylerList stylers;
    protected ArrayList<STTEventListener> listeners;

	
	public DataItem()
	{
        listeners = new ArrayList<STTEventListener>(2);
        stylers = new DataStylerList(2);
	}
	

	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}
	
	
	public DataStylerList getStylerList()
	{
		return stylers;
	}


	public DataProvider getDataProvider()
	{
		return dataProvider;
	}


	public void setDataProvider(DataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}


	public boolean isEnabled()
	{
		return enabled;
	}


	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}


	public Hashtable<String, Object> getOptions()
	{
		return options;
	}


	public void setOptions(Hashtable<String, Object> options)
	{
		this.options = options;
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
        if (!enabled)
            return;
        
        event.producer = this;
        
        for (int i=0; i<listeners.size(); i++)
        {
            STTEventListener next = listeners.get(i);
            if (next != sender)
                next.handleEvent(event);
        }        
    }
    
    
    public String toString()
    {
        return this.name + " - " + super.toString();
    }
    
    
    /**
     * Computes item bounding box and return it
     * @return
     */
    public SpatialExtent getBoundingBox()
    {
        SpatialExtent bbox = null;
        
        // compute smallest bbox containing all children bbox
        for (int i = 0; i < stylers.size(); i++)
        {
            DataStyler nextStyler = stylers.get(i);
            
            if (!nextStyler.isEnabled())
                continue;
            
            nextStyler.updateBoundingBox();
            SpatialExtent childBox = nextStyler.getBoundingBox();
            
            if (i == 0)
                bbox = childBox.copy();
            else
                bbox.add(childBox);
        }
        
        return bbox;
    }
}
