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

import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.STTTimeExtent;


/**
 * <p><b>Title:</b><br/>
 * Block Info
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Additional (optional) info for a BlockListItem.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class BlockInfo
{
    protected STTSpatialExtent spatialExtent;
    protected STTTimeExtent timeExtent;
    //protected double geometryStepAverage;
    //protected double geometryStepVariance;
    
    
    public STTSpatialExtent getSpatialExtent()
    {
        if (spatialExtent == null)
            spatialExtent = new STTSpatialExtent();
        
        return spatialExtent;
    }


    public void setSpatialExtent(STTSpatialExtent spatialExtent)
    {
        this.spatialExtent = spatialExtent;
    }


    public STTTimeExtent getTimeExtent()
    {
        if (timeExtent == null)
            timeExtent = new STTTimeExtent();
        
        return timeExtent;
    }


    public void setTimeExtent(STTTimeExtent timeExtent)
    {
        this.timeExtent = timeExtent;
    }
}
