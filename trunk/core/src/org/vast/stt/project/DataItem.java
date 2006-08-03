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
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;


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
public class DataItem implements DataEntry, STTEventListener, STTEventProducer
{
	protected String name;
	protected boolean enabled = true;
	protected Hashtable<String, Object> options;
	protected DataProvider dataProvider;
    protected ArrayList<Symbolizer> symbolizers;
    protected STTEventListeners listeners;

	
	public DataItem()
	{
        listeners = new STTEventListeners(2);
        symbolizers = new ArrayList<Symbolizer>(2);
	}
	

	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}
    
    
    public List<Symbolizer> getSymbolizers()
    {
        return symbolizers;
    }


	public DataProvider getDataProvider()
	{
		return dataProvider;
	}


	public void setDataProvider(DataProvider dataProvider)
	{
        if (this.dataProvider != dataProvider)
        {
            if (this.dataProvider != null)
                this.dataProvider.removeListener(this);
            
            this.dataProvider = dataProvider;
            
            if (this.dataProvider != null)
                this.dataProvider.addListener(this);
        }
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
        if (enabled)
        {
            event.producer = this;
            listeners.dispatchEvent(event);
        }
    }
    
    
    public void handleEvent(STTEvent event)
    {
        switch(event.type)
        {
            case PROVIDER_DATA_CHANGED:
            case PROVIDER_DATA_CLEARED:
                dispatchEvent(event.copy());
                break;    
        }        
    }
    
    
    public String toString()
    {
        return this.name + " - " + super.toString();
    }
}
