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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.vast.stt.data.DataException;
import org.vast.stt.data.DataNode;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTProgressService;
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
    protected static final String initError = "Error while initializing data provider ";
    protected static final String updateError = "Error while updating data provider ";
    
    protected String name;
    protected String description;
    protected boolean enabled = false;
	protected boolean canceled = false;
    protected boolean error = false;
    protected boolean redoUpdate = true;
    protected boolean updating = false;
	protected InputStream dataStream;
	protected DataNode dataNode;
	protected STTTimeExtent timeExtent;
    protected STTSpatialExtent spatialExtent;
	protected STTTimeExtent maxTimeExtent;
	protected STTSpatialExtent maxSpatialExtent;
    protected STTEventListeners listeners;
    protected Object lock = new Object();
    protected STTProgressService progressService;
    
    
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
        progressService = new STTProgressService();
        progressService.setProvider(this);
    }
      
    
    public void startUpdate(boolean force)
    {
        if (!enabled || error)
            return;
        
        // if updating, continue only if force is true
        synchronized(lock)
        {
            if (updating)
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
            
            updating = true;
        }
        
        // start the update thread
        IRunnableWithProgress runnable = new IRunnableWithProgress()
        {
            public void run(IProgressMonitor monitor)
            {
                if (monitor != null)
                    monitor.beginTask("Updating " + name + " / ", IProgressMonitor.UNKNOWN);
                
                do
                {
                    try
                    {
                        // clear previous data
                        clearData();                                
                        canceled = false;
                        
                        synchronized(lock)
                        {
                            if (!canceled)
                                redoUpdate = false;
                            //else
                                //System.out.println("Update canceled");
                        }
                        
                        // init provider
                        if (monitor != null)
                            monitor.subTask("Initializing...");
                        if (!dataNode.isNodeStructureReady())
                            init();
                        
                        // update data
                        //System.out.println("Updating " + name + "...");
                        if (monitor != null)
                            monitor.subTask("Updating data...");
                        updateData();
                    }
                    catch (DataException e)
                    {
                        error = true;
                        redoUpdate = false;
                        ExceptionSystem.display(e);
                        dispatchEvent(new STTEvent(e, EventType.PROVIDER_ERROR));
                    }
                    catch (Exception e)
                    {
                        error = true;
                        redoUpdate = false;
                        e.printStackTrace();
                        dispatchEvent(new STTEvent(e, EventType.PROVIDER_ERROR));
                    }
                }
                while (redoUpdate);
                
                updating = false;
                if (monitor != null)
                    monitor.done();
            }
        };
                
        progressService.run(true, true, runnable);
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
        if (!dataNode.isNodeStructureReady() || error)
        {
            error = false;
            startUpdate(false);
        }
        
        return dataNode;
    }
	
	
	public synchronized boolean isUpdating()
	{
		return updating;
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
                if (this.isTimeSubsetSupported())
                {
                    startUpdate(true);
                    break;
                }
                    
            case PROVIDER_SPATIAL_EXTENT_CHANGED:
                if (this.isSpatialSubsetSupported())
                {
                    startUpdate(true);
                    break;
                }
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
    
    
    public boolean isEnabled()
    {
        return enabled;
    }
    
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
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
