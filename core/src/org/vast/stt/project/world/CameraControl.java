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


/**
 * <p><b>Title:</b>
 * Camera Control
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Base interface for all mouse based camera controllers.
 * This allows derivation of controllers with different behaviors.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 11, 2006
 * @version 1.0
 */
public interface CameraControl
{
    public void setScene(WorldScene scene);
    
    public void doLeftDrag(int x0, int y0, int x1, int y1);
    
    public void doRightDrag(int x0, int y0, int x1, int y1);
    
    public void doMiddleDrag(int x0, int y0, int x1, int y1);
    
    public void doWheel(int count);
    
    public void doLeftClick(int x0, int y0);
    
    public void doRightClick(int x0, int y0);
    
    public void doMiddleClick(int x0, int y0);
    
    public void doLeftDblClick(int x0, int y0);
    
    public void doRightDblClick(int x0, int y0);
    
    public void doMiddleDblClick(int x0, int y0);
        
    public void doZoom(double amount);
}
