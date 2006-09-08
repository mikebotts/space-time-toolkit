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

package org.vast.stt.dynamics;

import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.Scene;


/**
 * <p><b>Title:</b>
 * Scene Time Updater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to synchronize a TimeExtent to the Scene time
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Sep 7, 2006
 * @version 1.0
 */
public class SceneTimeUpdater extends TimeExtentUpdater implements STTEventListener
{
    protected Scene scene;
    
    
    public SceneTimeUpdater(Scene scene)
    {
        this.setScene(scene);
    }
    
    
    public void handleEvent(STTEvent e)
    {
        switch (e.type)
        {
            case SCENE_TIME_CHANGED:
                updateTime(scene.getTimeSettings().getCurrentTime().getJulianTime());
                break;
        }        
    }
    
    
    protected void updateTime(double sceneTime)
    {
        this.timeExtent.setBaseTime(sceneTime);
        this.timeExtent.dispatchEvent(new STTEvent(this, EventType.PROVIDER_TIME_EXTENT_CHANGED));
    }
    

    public void setScene(Scene scene)
    {
        this.scene = scene;
        scene.getTimeSettings().addListener(this);
    }
}
