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
 * Grid Patch Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a patch of a grid. A patch is the individual
 * piece of a composite grid.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class RasterGridGraphic implements GraphicObject
{
	public int width = 0;
    public int length = 0;
    public int depth = 0;
    public boolean updated;
}
