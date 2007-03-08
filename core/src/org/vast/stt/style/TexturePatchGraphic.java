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
 * Texture Patch Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a patch of a mapped texture. A tile is composed of a grid and/or an image
 * to be textured on the grid.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class TexturePatchGraphic extends GraphicObject
{
    protected GridPatchGraphic grid;
    protected RasterTileGraphic texture;
    
    
    public TexturePatchGraphic()
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


    public void setGrid(GridPatchGraphic grid)
    {
        this.grid = grid;
    }


    public void setTexture(RasterTileGraphic texture)
    {
        this.texture = texture;
    }
}
