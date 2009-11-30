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

package org.vast.stt.project;

import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.project.tree.DataEntry;


/**
 *  * <p><b>Title:</b>
 * Abstract Display
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO AbstractDisplay type description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Sep 12, 2006
 * @version 1.0
 */
public abstract class AbstractDisplay implements STTDisplay
{
    protected String name;
    protected String description;
    protected String rendererClass;
    protected DataEntry parent;
    protected STTEventListeners listeners;
    
    
    public AbstractDisplay()
    {
        listeners = new STTEventListeners(2);
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
        event.producer = this;
        listeners.dispatchEvent(event, merge);
    }
    

    public String getRendererClass()
    {
        return rendererClass;
    }


    public void setRendererClass(String rendererClass)
    {
        this.rendererClass = rendererClass;
    }
    
    
    public abstract void handleEvent(STTEvent event);
}
