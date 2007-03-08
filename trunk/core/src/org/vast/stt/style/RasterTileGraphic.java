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
	public enum BufferType
    {
        LUM, LUMA, R, G, B, RGB, BGR, RGBA, BGRA
    }    
    
    public int tileNumber;
    
    public int width = 0;
    public int height = 0;
    public int bands = 3;
    public float opacity = 1.0f;
    
    public int xPos = 0;
    public int yPos = 0;
    public int widthPadding = 0;
    public int heightPadding = 0;
    
    // if no transform is applied, image data can be made
    // accessible to the renderer directly using these fields
    public Buffer rasterData;
    public BufferType rasterType;
    public boolean hasRasterData;
}
