/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.event;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.vast.stt.provider.DataProvider;


public class STTProgressService implements IRunnableContext
{
    private boolean showInGUI;
    private DataProvider provider;
    
    
    public STTProgressService()
    {        
    }
    
    
    public void setProvider(DataProvider provider)
    {
        this.provider = provider;
    } 
    
    
    public void run(final boolean fork, final boolean cancelable, final IRunnableWithProgress runnable)
    {
        showInGUI = false;//(provider.getSpatialExtent().getUpdater() == null);
        
        if (showInGUI)
        {
            try
            {
                Job myJob = new Job("Updating: " + provider.getName())
                {

                    @Override
                    protected IStatus run(IProgressMonitor monitor)
                    {
                        try
                        {
                            runnable.run(monitor);
                        }
                        catch (Exception e)
                        {
                        }
                        
                        if (monitor.isCanceled())
                            return Status.CANCEL_STATUS;
                        else
                            return Status.OK_STATUS;
                    }
                    
                };
                
                //myJob.setUser(true);
                myJob.setPriority(6);
                myJob.schedule();                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Thread updateThread = new Thread("Updating: " + provider.getName())
            {
                public void run()
                {
                    try
                    {
                        runnable.run(null);
                    }
                    catch (Exception e)
                    {
                    }
                }
            };
            
            updateThread.setPriority(6);
            updateThread.start();
        }
    }    
}
