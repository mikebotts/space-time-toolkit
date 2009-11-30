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

package org.vast.stt.project.tree;

import java.util.*;

import org.vast.ows.sld.Symbolizer;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;
import org.vast.stt.project.feedback.ItemAction;
import org.vast.stt.provider.DataProvider;


/**
 * <p><b>Title:</b><br/>
 * DataItem
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Object representing a renderable item in a scene.
 * A DataItem is connected to a DataProvider and also stores
 * info about how the data should be represented/rendered though
 * the use of Symbolizers.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 3, 2005
 * @version 1.0
 */
public class DataItem implements DataEntry, STTEventListener, STTEventProducer
{
	protected String name;
    protected String description;
    protected DataEntry parent;
	protected boolean enabled = true;
	protected Hashtable<String, Object> options;
	protected DataProvider dataProvider;
    protected STTEventListeners listeners;
    protected List<Symbolizer> symbolizers;
    protected List<DataItem> masks;
    protected List<ItemAction> actions;
    
    
    public DataItem()
	{
        listeners = new STTEventListeners(2);
        symbolizers = new ArrayList<Symbolizer>(2); 
        actions = new ArrayList<ItemAction>(1);
        masks = new ArrayList<DataItem>(1);
        options = new Hashtable<String, Object>();
	}
    
    
    public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}
    
    
    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }
    
    
    public DataEntry getParent()
	{
		return parent;
	}


	public void setParent(DataEntry parent)
	{
		this.parent = parent;		
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
        
        if (dataProvider != null)
            dataProvider.setEnabled(enabled);
	}
    
    
    public boolean hasEvent()
    {
        return !actions.isEmpty();
    }
    
    
    public boolean hasMask()
    {
        return !masks.isEmpty();
    }
    
    
    public List<Symbolizer> getSymbolizers()
    {
        return this.symbolizers;
    }
    
    
    public List<DataItem> getMasks()
    {
        return masks;
    }
    
    
    public List<ItemAction> getActions()
    {
        return actions;
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
    
    
    public boolean hasListeners()
    {
        return !listeners.isEmpty();
    }


    public void dispatchEvent(STTEvent event, boolean merge)
    {
        if (enabled)
        {
            event.producer = this;
            listeners.dispatchEvent(event, merge);
        }
    }
    
    
    public void handleEvent(STTEvent event)
    {
        switch(event.type)
        {
            case PROVIDER_DATA_CHANGED:
            case PROVIDER_DATA_CLEARED:
            case PROVIDER_DATA_REMOVED:
                dispatchEvent(event.copy(), false);
                break;
                
            case PROVIDER_ERROR:
                this.enabled = false;
                dispatchEvent(event.copy(), false);
                break;
        }        
    }
    
    
    public String toString()
    {
        return this.name + " - " + super.toString();
    }
}
