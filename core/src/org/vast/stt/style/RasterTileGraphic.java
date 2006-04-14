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
 * Raster Tile Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a tile of a raster. A tile is the atomic piece
 * of a composite tiled raster.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class RasterTileGraphic extends GraphicObject
{
	public int tileNumber;
    
    public int width = 0;
    public int height = 0;
    public float opacity = 1.0f;
    
    public Buffer rasterData;
    public Buffer colorMapData;

    public boolean hasRasterData;
    public boolean hasColorMapData;
    
    public boolean updated = true;
}
