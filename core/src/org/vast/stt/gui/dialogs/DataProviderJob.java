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

package org.vast.stt.gui.dialogs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.provider.DataProvider;


/**
 * <p><b>Title:</b>
 * Data Provider Job
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This class listens to start/cancel/done events from
 * a provider and manages an Eclipse Job accordingly, in
 * order to show a progress bar in the app progress dialog.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 18, 2007
 * @version 1.0
 */
public class DataProviderJob extends Job implements STTEventListener
{
    protected DataProvider provider;
    protected boolean canceled;
    
    
    public DataProviderJob(DataProvider provider)
    {
        super(provider.getName());
        this.provider = provider;
        this.provider.addListener(this);
    }

    
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try
        {
            canceled = false;
                        
            if (monitor != null)
                monitor.beginTask("Updating " + provider.getName(), IProgressMonitor.UNKNOWN);
            
            synchronized(this)
            {
                // wait for update to be done
                while (!canceled && provider.isUpdating())
                    wait();               
            }
            
            if (monitor != null)
                monitor.done();            
        }
        catch (InterruptedException e)
        {
        }
        
        if (canceled)
            return Status.CANCEL_STATUS;
        else
            return Status.OK_STATUS;
    }


    /**
     * Starts and stop the job when receiving corresponding
     * events from DataProvider
     */
    public void handleEvent(STTEvent e)
    {
        if (e.type == EventType.PROVIDER_UPDATE_START)
        {
            //setUser(true);
            this.schedule();
        }
        
        else if (e.type == EventType.PROVIDER_UPDATE_DONE)
        {
            synchronized(this)
            {
                notifyAll();
            }
        }
        
        else if (e.type == EventType.PROVIDER_UPDATE_CANCELED)
        {
            synchronized(this)
            {
                canceled = true;
                notifyAll();
            }
        }
    }
}
