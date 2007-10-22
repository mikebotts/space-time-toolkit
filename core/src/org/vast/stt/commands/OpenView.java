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

package org.vast.stt.commands;

import java.text.NumberFormat;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.vast.stt.gui.views.ChartView;
import org.vast.stt.gui.views.DataItemView;
import org.vast.stt.gui.views.TableView;
import org.vast.stt.project.chart.ChartScene;
import org.vast.stt.project.table.TableScene;
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
                    else if (openedView instanceof ChartView)
                        ((ChartView)openedView).setScene((ChartScene)data);
                    else if (openedView instanceof TableView)
                        ((TableView)openedView).setScene((TableScene)data);
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
