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
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.vast.stt.event.STTEvent;
import org.vast.stt.gui.widgets.time.SceneTimeController;
import org.vast.stt.project.world.WorldScene;


/**
 * <p><b>Title:</b><br/>
 * Master Time View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * View for MasterTime Controller for a Scene/Workbench
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date May 1, 2006
 * @version 1.0
 * 
 */
public class SceneTimeView extends SceneView<WorldScene>
{
    public static final String ID = "STT.SceneTimeView";
    protected SceneTimeController masterTimeController;


    @Override
    public void createPartControl(Composite parent)
    {
        masterTimeController = new SceneTimeController(parent, (WorldScene)scene);
        getSite().getPage().addPartListener(this);
    }


    @Override
    public void updateView()
    {
        masterTimeController.setScene((WorldScene)scene);
    }


    @Override
    public void clearView()
    {
        masterTimeController.setScene(null);
    }
    
    
    @Override
    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case TIME_EXTENT_CHANGED:
                if (e.source != masterTimeController) {
                	//System.err.println();
                    refreshViewAsync();
                }
        }
    }
    
    
    @Override
    protected void assignScene()
    {
        ScenePageInput pageInput = (ScenePageInput) getSite().getPage().getInput();
        if (pageInput != null)
            setScene((WorldScene)pageInput.getScene());
        else
            setScene(null);
    }


    @Override
    public void setScene(WorldScene sc)
    {
        super.setScene(sc);
        masterTimeController.setScene(sc);
        
        // refresh display
        refreshViewAsync();
    }
}
