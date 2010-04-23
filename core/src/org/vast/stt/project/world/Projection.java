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

import org.vast.math.Vector3d;
import org.vast.stt.project.world.ViewSettings.MotionConstraint;
import org.vast.stt.style.PrimitiveGraphic;
import org.vast.util.SpatialExtent;


/**
 * <p><b>Title:</b>
 * Projection
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Interface for all map projections
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Aug 8, 2006
 * @version 1.0
 */
public interface Projection
{
    public enum Crs
    {
        ECI, ECEF,
        EPSG4329,
        MERC
    }
    
    
    public void adjust(Crs sourceCrs, PrimitiveGraphic point);
    
    
    public void clip(PrimitiveGraphic point);
    
    
    public void project(Crs sourceCrs, Vector3d point);
    
    
    public void unproject(Crs destCrs, Vector3d point);
    
        
    public void fitViewToBbox(SpatialExtent bbox, WorldScene scene, boolean adjustZRange);
    
    
    public void fitBboxToView(SpatialExtent bbox, WorldScene scene);
    
    
    public boolean pointOnMap(int x, int y, WorldScene scene, Vector3d pos);
    
    
    public double getCameraIncidence(ViewSettings viewSettings);
    
    
    public MotionConstraint getDefaultTranslationConstraint();
    
    
    public MotionConstraint getDefaultRotationConstraint();
    
    
    public MotionConstraint getDefaultZoomConstraint();
}
