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

package org.vast.stt.project;

import org.vast.physics.MapProjection;
import org.vast.stt.style.PrimitiveGraphic;


/**
 * <p><b>Title:</b>
 * ECEF Projection
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Contains methods to adjust geometries for ECEF projection
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 8, 2006
 * @version 1.0
 */
public class Projection_ECEF implements Projection
{
    public void adjust(Crs sourceCrs, PrimitiveGraphic point)
    {
        switch (sourceCrs)
        {
            case EPSG4329:
                double[] ecef = MapProjection.LLAtoECF(point.y, point.x, point.z, null);
                point.x = ecef[0];
                point.y = ecef[1];
                point.z = ecef[2];
                break;
        }
    }
}
