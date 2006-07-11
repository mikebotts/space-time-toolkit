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

import org.vast.stt.util.SpatialExtent;
import org.vast.stt.util.TimeExtent;

/**
 * <p><b>Title:</b><br/>
 * Block Info
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO BlockInfo type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class BasicBlockInfo extends BlockInfo
{
    public SpatialExtent spatialExtent;  // Stores bbox of this block for filtering
    public TimeExtent timeExtent;        // 
    public double geometryStepAverage;
    public double geometryStepVariance;
    
    // public BlockInfo[] subBlocks; // use for block hierarchy when needed
}
