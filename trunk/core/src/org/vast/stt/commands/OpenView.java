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

package org.vast.stt.commands;

import java.text.NumberFormat;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.vast.stt.gui.views.DataItemView;
import org.vast.stt.project.tree.DataItem;


public class OpenView implements Command
{
    protected String viewID;
    protected Object data;
    
    
    public void execute()
    {
        // create runnable to be able
        Runnable refreshView = new Runnable()
        {
            public void run()
            {
                try
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    
                    // check if view is a multiple instance view
                    IViewDescriptor viewDesc = PlatformUI.getWorkbench().getViewRegistry().find(viewID);
                    
                    IViewPart openedView = null;
                    int maxIndex = 0;
                    if (viewDesc.getAllowMultiple())
                    {
                        IViewReference[] viewRefs = page.getViewReferences();
                        for (int i=0; i<viewRefs.length; i++)
                        {
                            if (viewRefs[i].getId().equals(viewID))
                            {
                                String secId = viewRefs[i].getSecondaryId();
                                int viewIndex = Integer.parseInt(secId);
                                maxIndex = Math.max(maxIndex, viewIndex);
                            }
                        }
                        
                        NumberFormat formatter = NumberFormat.getIntegerInstance();
                        formatter.setMinimumIntegerDigits(3);
                        String secId = formatter.format(maxIndex + 1);                        
                        openedView = page.showView(viewID, secId, IWorkbenchPage.VIEW_ACTIVATE);
                    }
                    else
                        openedView = page.showView(viewID);
                    
                    // set view input
                    if (openedView instanceof DataItemView)
                        ((DataItemView)openedView).setDataItem((DataItem)data);
                }
                catch (PartInitException e)
                {
                    e.printStackTrace();
                }
            }
        };

        PlatformUI.getWorkbench().getDisplay().asyncExec(refreshView);
    }


    public boolean isUndoAvailable()
    {
        return false;
    }


    public void unexecute()
    {
    }


    public void setViewID(String viewID)
    {
        this.viewID = viewID;
    }


    public void setData(Object data)
    {
        this.data = data;
    }
}
