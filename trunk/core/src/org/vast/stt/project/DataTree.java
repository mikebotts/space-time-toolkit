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
 * Data List
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a list of data items in a scene
 * DataList can be nested recursively, this is why the
 * DataEntry interface is used her
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 17, 2005
 * @version 1.0
 */
public class DataTree extends DataFolder implements STTEventProducer
{
    static final long serialVersionUID = 0;
    protected STTEventListeners listeners;
    

    public DataTree()
    {
        listeners = new STTEventListeners(2);
    }
    
    
    public DataTree(DataFolder folder)
    {
        this.name = folder.name;
        this.addAll(folder);
    }


    public DataTree(int listSize)
    {
        super(listSize);
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
