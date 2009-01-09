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

package org.vast.sttx.kml.actions;

import java.io.FileNotFoundException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.world.WorldScene;
import org.vast.sttx.kml.KMLExporter;


/**
 * <p><b>Title:</b>
 * KML Menu
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Receives and process events from the KML STT Menu
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 1, 2007
 * @version 1.0
 */
public class KMLMenu implements IWorkbenchWindowActionDelegate
{
    private IWorkbenchWindow window;


    public void dispose()
    {

    }


    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }


    public void run(IAction action)
    {
        if (action.getId().equals("STT.ExportKML"))
        {
            ScenePageInput pageInput = (ScenePageInput) window.getActivePage().getInput();
            WorldScene scene = (WorldScene) pageInput.getScene();

            KMLExporter exporter;
            try
            {
                exporter = new KMLExporter("d:\\test.kml", false);
                exporter.exportScene(scene);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }            
        }
    }


    public void selectionChanged(IAction action, ISelection selection)
    {

    }

}
