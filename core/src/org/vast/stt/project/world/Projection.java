/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.project.world;

import org.vast.math.Vector3d;
import org.vast.physics.SpatialExtent;
import org.vast.stt.project.world.ViewSettings.MotionConstraint;
import org.vast.stt.style.PrimitiveGraphic;


/**
 * <p><b>Title:</b>
 * Projection
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Interface for all map projections
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 8, 2006
 * @version 1.0
 */
public interface Projection
{
    public enum Crs
    {
        ECEF,
        EPSG4329
    }
    
    
    public abstract void adjust(Crs sourceCrs, PrimitiveGraphic point);
    
    
    public abstract void project(Crs sourceCrs, PrimitiveGraphic point);
    
    
    public abstract void clip(PrimitiveGraphic point);
    
    
    public abstract void fitViewToBbox(SpatialExtent bbox, WorldScene scene, boolean adjustZRange);
    
    
    public abstract void fitBboxToView(SpatialExtent bbox, WorldScene scene);
    
    
    public abstract boolean pointOnMap(int x, int y, WorldScene scene, Vector3d pos);
    
    
    public abstract MotionConstraint getDefaultTranslationConstraint();
    
    
    public abstract MotionConstraint getDefaultRotationConstraint();
    
    
    public abstract MotionConstraint getDefaultZoomConstraint();
}
