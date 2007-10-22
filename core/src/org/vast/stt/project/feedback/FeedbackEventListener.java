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

package org.vast.stt.project.feedback;

//import org.vast.stt.project.scene.Scene;
import org.vast.stt.renderer.PickFilter;
//import org.vast.stt.renderer.PickedObject;
//import org.vast.stt.renderer.SceneRenderer;


public class FeedbackEventListener
{
    public void handleEvent(FeedbackEvent event)
    {
        PickFilter pickFilter = new PickFilter();
        pickFilter.x = event.cursorX;
        pickFilter.y = event.cursorY;
        pickFilter.dX = 5;
        pickFilter.dY = 5;
        pickFilter.onlyItemsWithAction = true;
        //Scene<? extends SceneRenderer<? extends Scene>> scene = event.getSourceScene(); 
        //PickedObject obj = scene.getRenderer().pick(scene, pickFilter);
    }
}
