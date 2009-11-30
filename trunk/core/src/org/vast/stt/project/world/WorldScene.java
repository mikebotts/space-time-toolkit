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

package org.vast.stt.project.world;

import org.vast.stt.event.STTEvent;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.provider.STTTimeExtent;
import org.vast.stt.style.DataStyler;


/**
 * <p><b>Title:</b><br/>
 * World Scene Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Encapsulate the current state of a world scene (graphic view)
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class WorldScene extends Scene
{
    protected ViewSettings viewSettings;
	protected STTTimeExtent timeExtent;
    protected CameraControl cameraController;
        

    public WorldScene()
    {
        super();
        cameraController = new CameraControl_Map(this);        
    }
    
    
    @Override
    protected void prepareStyler(DataStyler styler)
    {
        styler.setProjection(viewSettings.getProjection());
    }


	public ViewSettings getViewSettings()
	{
		return viewSettings;
	}


	public void setViewSettings(ViewSettings viewSettings)
	{
        if (this.viewSettings != viewSettings)
        {
            if (this.viewSettings != null)
                this.viewSettings.removeListener(this);
            
            this.viewSettings = viewSettings;
            
            if (this.viewSettings != null)
                this.viewSettings.addListener(this);
        }
	}

	public STTTimeExtent getTimeExtent() {
		return timeExtent;
	}


	public void setTimeExtent(STTTimeExtent timeExtent) {
		if (this.timeExtent != timeExtent)
        {
            if (this.timeExtent != null)
                this.timeExtent.removeListener(this);
            
            this.timeExtent = timeExtent;
            
            if (this.timeExtent != null)
                this.timeExtent.addListener(this);
        }
	}
	
    public CameraControl getCameraController()
    {
        return cameraController;
    }


    public void setCameraController(CameraControl cameraController)
    {
        this.cameraController = cameraController;
    }
    
    
    public void setProjection(Projection projection)
    {
    	// project geometry of all items and masks
        for (int i = 0; i < sceneItems.size(); i++)
            sceneItems.get(i).setProjection(projection);        
        
        // select default camera controller
        if (projection instanceof Projection_ECEF)
        	cameraController = new CameraControl_Globe(this);
        else
        	cameraController = new CameraControl_Map(this);
    }
    
    
    public void handleEvent(STTEvent event)
    {
        switch (event.type)
        {
            case SCENE_PROJECTION_CHANGED:
                setProjection(viewSettings.getProjection());         
                // forward event for scene fit and redraw
                dispatchEvent(event.copy(), false);                
                break;
                
            case SCENE_VIEW_CHANGED:
                dispatchEvent(event.copy(), false);
                break;
                
            case TIME_EXTENT_CHANGED:
                dispatchEvent(event.copy(), false);
                break;
        }
    }
}
