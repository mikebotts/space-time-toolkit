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

package org.vast.stt.provider;

import java.io.IOException;
import java.io.InputStream;

import org.vast.stt.data.DataException;
import org.vast.stt.data.DataNode;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.util.ExceptionSystem;


/**
 * <p><b>Title:</b><br/>
 * Abstract Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for data providers getting data from an 
 * input stream and using a cached DataNode.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 9, 2005
 * @version 1.0
 */
public abstract class AbstractProvider implements DataProvider
{
    protected String name;
    protected String description;
	protected boolean canceled = false;
    protected boolean error = false;
    protected boolean autoUpdate = false;
    protected boolean redoUpdate = true;
	protected InputStream dataStream;
	protected DataNode dataNode;
	protected STTTimeExtent timeExtent;
    protected STTSpatialExtent spatialExtent;
	protected STTTimeExtent maxTimeExtent;
	protected STTSpatialExtent maxSpatialExtent;
    protected Thread updateThread;
    protected STTEventListeners listeners;
    protected Object lock = new Object();
    
    
    public abstract void init() throws DataException;
    public abstract void updateData() throws DataException;
    public abstract boolean isSpatialSubsetSupported();
    public abstract boolean isTimeSubsetSupported();

	
    public AbstractProvider()
    {
        this.dataNode = new DataNode();
        this.setTimeExtent(new STTTimeExtent());
        this.setSpatialExtent(new STTSpatialExtent());
        listeners = new STTEventListeners(2);
    }
      
    
    public synchronized void startUpdate(boolean force)
    {
        if (error)
            return;
        
        // if updating, continue only if force is true
        synchronized(lock)
        {
            if (updateThread != null)
            {
                if (!canceled && force)
                {
                    // make sure we canceled previous update properly
                    redoUpdate = true;
                    cancelUpdate();                    
                    return;
                }
                else
                    return;
            }
        }
        
        // start the update thread
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                do
                {
                    try
                    {
                        // clear previous data
                        clearData();                                
                        canceled = false;
                        
                        // update data
                        System.out.println("Updating " + name + "...");
                        updateData();
                        
                        synchronized(lock)
                        {
                            if (!canceled)
                                redoUpdate = false;
                            else
                                System.out.println("Update canceled");
                        }
                    }
                    catch (DataException e)
                    {
                        error = true;
                        redoUpdate = false;
                        ExceptionSystem.display(e);
                        dispatchEvent(new STTEvent(e, EventType.PROVIDER_ERROR));
                    }
                }
                while (redoUpdate);
                updateThread = null;                
            }
        };
        
        updateThread = new Thread(runnable, "Data update: " + this.name);
        updateThread.start();
    }
    
    
    public void cancelUpdate()
    {
        canceled = true;

        try
        {
            if (dataStream != null)
                dataStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
	
	
	public void clearData()
	{
        error = false;
        
        if (dataNode != null)
		{
            dataNode.clearAll();
            dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CLEARED));
		}		
	}
	
	
    public DataNode getDataNode()
    {
        if (!dataNode.isNodeStructureReady())
            startUpdate(false);
        
        return dataNode;
    }
	
	
	public synchronized boolean isUpdating()
	{
		return updateThread.isAlive();
	}


	public STTSpatialExtent getSpatialExtent()
	{
		return spatialExtent;
	}
	
	
	public void setSpatialExtent(STTSpatialExtent spatialExtent)
	{
		if (this.spatialExtent != spatialExtent)
        {
            if (this.spatialExtent != null)
                this.spatialExtent.removeListener(this);
            
            this.spatialExtent = spatialExtent;
            
            if (this.spatialExtent != null)
                this.spatialExtent.addListener(this);
        }
	}


	public STTTimeExtent getTimeExtent()
	{
		return timeExtent;
	}


	public void setTimeExtent(STTTimeExtent timeExtent)
	{
        if (this.timeExtent != timeExtent)
        {
            if (this.timeExtent != null)
                this.timeExtent.removeListener(this);
            
            this.timeExtent = timeExtent;
            
            if (this.timeExtent != null)
                this.timeExtent.addListener(this);
        }
	}


	public STTSpatialExtent getMaxSpatialExtent()
	{
		return maxSpatialExtent;
	}


	public void setMaxSpatialExtent(STTSpatialExtent maxSpatialExtent)
	{
		this.maxSpatialExtent = maxSpatialExtent;
	}


	public STTTimeExtent getMaxTimeExtent()
	{
		return maxTimeExtent;
	}


	public void setMaxTimeExtent(STTTimeExtent maxTimeExtent)
	{
		this.maxTimeExtent = maxTimeExtent;
	}
    
    
    public boolean getAutoUpdate()
    {
        return autoUpdate;
    }
    
    
    public void setAutoUpdate(boolean autoUpdate)
    {
        this.autoUpdate = autoUpdate;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case PROVIDER_TIME_EXTENT_CHANGED:
            case PROVIDER_SPATIAL_EXTENT_CHANGED:
                if (autoUpdate)
                    startUpdate(true);
                dispatchEvent(e.copy());
                break;
        }
    }
    
    
    public boolean hasError()
    {
        return error;
    }
    
    
    public void setError(boolean error)
    {
        this.error = error;
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
