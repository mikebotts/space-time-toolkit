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

package org.vast.stt.project.feedback;

import java.util.ArrayList;

import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.project.world.ViewSettings;

/**
 * <p><b>Title:</b>
 * Show Scene
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to open a separete scene in another workbench window
 * as a result to a user action. The camera parameters to use
 * to show this new view can be adjusted as well as the visible
 * items in the scene.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Oct 19, 2006
 * @version 1.0
 */
public class ShowScene extends ItemAction
{
    protected WorldScene scene;
    protected ViewSettings viewSettings;
    protected boolean adjustView;
    protected ArrayList<DataItem> visibleItems;


    public boolean isAdjustView()
    {
        return adjustView;
    }


    public void setAdjustView(boolean adjustView)
    {
        this.adjustView = adjustView;
    }


    public WorldScene getScene()
    {
        return scene;
    }


    public void setScene(WorldScene scene)
    {
        this.scene = scene;
    }


    public ViewSettings getViewSettings()
    {
        return viewSettings;
    }


    public void setViewSettings(ViewSettings viewSettings)
    {
        this.viewSettings = viewSettings;
    }


    public ArrayList<DataItem> getVisibleItems()
    {
        return visibleItems;
    }


    public void setVisibleItems(ArrayList<DataItem> visibleItems)
    {
        this.visibleItems = visibleItems;
    }
}
