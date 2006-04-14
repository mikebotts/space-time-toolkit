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

import org.vast.stt.util.SpatialExtent;
import org.vast.stt.util.TimeExtent;


/**
 * <p><b>Title:</b><br/>
 * Texture Patch Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a patch of a mapped texture. A tile is composed of a grid and/or an image
 * to be textured on the grid. It is the atomic piece of a composite raster.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class TexturePatchInfo implements BlockInfo
{
    protected GridPatchGraphic grid;
    protected RasterTileGraphic texture;
    protected SpatialExtent spatialExtent;
    protected TimeExtent timeExtent;
    
    protected BlockListItem firstImageBlock; // blocks are always ordered along width, then height, then depth
    protected int imageBlockCount;
    protected int imageBlockWidth;
    protected int imageBlockHeight;
    
    protected BlockListItem firstGridBlock; // blocks are always ordered along width, then length, then depth
    protected int gridBlockCount;
    protected int gridBlockWidth;
    protected int gridBlockLength;
    
    
    public TexturePatchInfo()
    {
        grid = new GridPatchGraphic();
        texture = new RasterTileGraphic();
    }
    
    
    public GridPatchGraphic getGrid()
    {
        return grid;
    }
    
    
    public RasterTileGraphic getTexture()
    {
        return texture;
    }


    public SpatialExtent getSpatialExtent()
    {
        return spatialExtent;
    }


    public TimeExtent getTimeExtent()
    {
        return timeExtent;
    }
}
