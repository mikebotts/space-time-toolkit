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

package org.vast.stt.actions;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.commands.OpenProject_NoMonitor;
import org.vast.stt.commands.OpenProject;
import org.vast.stt.commands.OpenProject_NoMonitor;


public class ProjectMenu implements IWorkbenchWindowActionDelegate
{
    public ProjectMenu()
    {
    }


    public void run(IAction action)
    {
        String actionId = action.getId();
        String url = null;

        // close project 
        if (actionId.endsWith("CloseProject"))
        {
            // TODO close project
            return;
        }        
        
        // open file chooser window
        if (actionId.endsWith("OpenProject"))
        {
            FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
            String path = fileDialog.open();
            if (path != null)
                url = "file:///" + path.replace('\\', '/');
            else return;
        }
        else if (actionId.endsWith("OpenTestProject"))
        {
        	//  NOTE: Remove this option
            ;//testScreenCap();
            return;
        }

        // launch OpenProject command in separate thread
//        final OpenProject_ProgInd openProjectCmd = new OpenProject_ProgInd();
        final OpenProject openProjectCmd = new OpenProject();
        openProjectCmd.setUrl(url);
        openProjectCmd.execute();
    }


    public void selectionChanged(IAction action, ISelection selection)
    {
    }


    public void dispose()
    {
    }


    public void init(IWorkbenchWindow window)
    {
    }
    
    /** TODO, add dialog for testScreenCap and move it to its own class 
     * 
     */
    int cnt=1;
    public void testScreenCap() {
    	
    	try {
			Robot robot = new Robot();
			Rectangle appBounds = PlatformUI.getWorkbench().getDisplay().getActiveShell().getBounds();
			java.awt.Rectangle appBoundsAwt = new java.awt.Rectangle();
			appBoundsAwt.x = appBounds.x;
			appBoundsAwt.y = appBounds.y;
			appBoundsAwt.width = appBounds.width;
			appBoundsAwt.height = appBounds.height;
			
			BufferedImage screenShot = robot.createScreenCapture(appBoundsAwt);
			ImageIO.write(screenShot, "PNG", new File("C:/tcook/ows5/AMSR_anim/amsrE_" + cnt + ".png"));
			cnt++;
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}