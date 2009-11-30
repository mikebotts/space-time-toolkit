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

package org.vast.stt.dynamics;

import java.util.List;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.world.WorldScene;


/**
 * <p><b>Title:</b>
 * Scene Time Updater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to synchronize a TimeExtent to the Scene time
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Sep 7, 2006
 * @version 1.0
 */
public class SceneTimeUpdater extends TimeExtentUpdater implements STTEventListener
{
    protected WorldScene scene;
    
    
    public SceneTimeUpdater(WorldScene scene)
    {
        this.setScene(scene);
    }
    
    
    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case TIME_EXTENT_CHANGED:
                if (enabled)
                	updateTime(scene.getTimeExtent().getBaseTime());
                break;
        }        
    }
    
    
    protected void updateTime(double sceneTime)
    {
        this.timeExtent.setBaseTime(sceneTime);
        boolean visibleDataFound = false;
        
        // find all dataItems using this dataProvider
        List<SceneItem> sceneItems = scene.getSceneItems();
        for (int i=0; i<sceneItems.size(); i++)
        {
            SceneItem nextItem = sceneItems.get(i);
            if (nextItem.getDataItem().getDataProvider().getTimeExtent() == this.timeExtent)
                visibleDataFound = true;
        }
        
        // send event only if data is currently visible if this scene
        if (visibleDataFound)
            this.timeExtent.dispatchEvent(new STTEvent(this, EventType.TIME_EXTENT_CHANGED), true);
    }
    

    public void setScene(WorldScene scene)
    {
        this.scene = scene;
        scene.getTimeExtent().addListener(this);
    }
    
    
    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        if (enabled == true)
            updateTime(scene.getTimeExtent().getBaseTime());
    }
}
