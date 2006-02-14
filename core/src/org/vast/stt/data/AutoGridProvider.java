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


/**
 * <p><b>Title:</b><br/>
 * AutoGridProvider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This provider automatically generates cordinates for
 * a rectangular grid based on the external bounding box
 * and values for vertical and horizontal spacing.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jan 25, 2006
 * @version 1.0
 */
public class AutoGridProvider extends AbstractProvider
{
    DataNode node;
    
    
	public AutoGridProvider()
	{
        this.maxSpatialExtent = new SpatialExtent();
        maxSpatialExtent.setMinX(-180.0);
        maxSpatialExtent.setMaxX(180.0);
        maxSpatialExtent.setMinY(-90.0);
        maxSpatialExtent.setMaxY(90.0);
        maxSpatialExtent.setMinZ(0.0);
        maxSpatialExtent.setMaxZ(0.0);
        
        this.node = new DataNode();
	}
	
	
	@Override
	public void updateData() throws DataException
	{
		
	}


	public boolean isSpatialSubsetSupported()
	{
		return true;
	}


	public boolean isTimeSubsetSupported()
	{
		return false;
	}
}
