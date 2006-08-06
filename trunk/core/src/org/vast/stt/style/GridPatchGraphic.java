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

import java.nio.Buffer;


/**
 * <p><b>Title:</b><br/>
 * Grid Patch Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a patch of a 3D grid. A patch is the atomic piece
 * of a composite grid.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class GridPatchGraphic extends GraphicObject
{
	public int tileNumber;
    
    public int width = 1;
    public int length = 1;
    public int depth = 1;
    public float lineWidth = 1.0f;
    
    public Buffer vertexData;
    public boolean hasGridData;
}
