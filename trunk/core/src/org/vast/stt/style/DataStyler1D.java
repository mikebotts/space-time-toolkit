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

package org.vast.stt.style;


/**
 * <p><b>Title:</b><br/>
 * Data Styler 1D
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Interface for stylers providing access to graphic objects using
 * a 1D index.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public interface DataStyler1D extends DataStyler
{

    public int getNumPoints();
    
    
	public PrimitiveGraphic getPoint(int u);
    
}
