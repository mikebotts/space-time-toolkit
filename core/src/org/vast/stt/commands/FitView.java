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

import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.project.world.ViewSettings;
import org.vast.stt.provider.STTSpatialExtent;


/**
 *  FitView either to an entire scene or a single DataItem
 *   
 * @author tcook
 * @date Feb 27, 2007
 */

public class FitView implements Command
{
    private WorldScene scene;
    private SceneItem item;


    public FitView(WorldScene scene, SceneItem item)
    {
        this.scene = scene;
        this.item = item;
    }


    public FitView(WorldScene scene)
    {
        this.scene = scene;
    }
    
    
    protected void fit(STTSpatialExtent bbox, boolean adjustZRange)
    {
        if (bbox == null || bbox.isNull())
            return;
        
        ViewSettings view = scene.getViewSettings();
        view.getProjection().fitViewToBbox(bbox, scene, adjustZRange);
        //view.setArcballRadius(bbox.getMaxDistance()/2);
        
        // send event to redraw
        view.dispatchEvent(new STTEvent(this, EventType.SCENE_VIEW_CHANGED), false);
    }


    public void execute()
    {
        STTSpatialExtent bbox = null;
        
        if (item == null)
        {
            // fit to whole scene
            bbox = scene.getBoundingBox();
            fit(bbox, true);
        }
        else
        {
            // fit to one item
            bbox = item.getBoundingBox();
            fit(bbox, false);
        }
    }
    
    
    public void unexecute()
    {
    }


    public boolean isUndoAvailable()
    {
        return false;
    }


    public void setScene(WorldScene scene)
    {
        this.scene = scene;
    }


    public void setItem(SceneItem item)
    {
        this.item = item;
    }
}
