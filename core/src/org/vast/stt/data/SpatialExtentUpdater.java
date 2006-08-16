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

package org.vast.stt.data;

import org.vast.stt.project.SpatialExtent;


/**
 * <p><b>Title:</b>
 * Spatial Extent Updater
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO SpatialExtentUpdater type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 6, 2006
 * @version 1.0
 */
public abstract class SpatialExtentUpdater
{
    protected SpatialExtent spatialExtent;
    
    
    public SpatialExtentUpdater(SpatialExtent spatialExtent)
    {
        this.spatialExtent = spatialExtent;
    }
    
    
    public void setTimeExtent(SpatialExtent spatialExtent)
    {
        this.spatialExtent = spatialExtent;
    }
}
