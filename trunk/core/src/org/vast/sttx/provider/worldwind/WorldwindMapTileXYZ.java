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

package org.vast.sttx.provider.worldwind;

import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.QuadTreeVisitor;


/**
 * <p><b>Title:</b>
 * Virtual Earth Tile Number
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Generates a virtual earth tile number by visiting the QuadTree
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Sep 30, 2006
 * @version 1.0
 */
public class WorldwindMapTileXYZ implements QuadTreeVisitor
{
    protected int x, y, zoom;
    protected int xMul, yMul;
    
    
    public WorldwindMapTileXYZ()
    {
        zoom = 0;
        xMul = 1;
        yMul = 1;
    }
    
    
    public void visit(QuadTreeItem item)
    {
        // go further only if there is a parent
        QuadTreeItem parent = item.getParent();
        if (parent == null)
            return;
        
        switch (item.getQuadrant())
        {
            case 0:
                break;
                
            case 1:
                x += xMul;
                break;
                
            case 2:
                x += xMul;
                y += yMul;
                break;
                
            case 3:
                y += yMul;
                break;
        }
        
        zoom++;
        xMul *= 2;
        yMul *= 2;
        
        parent.accept(this);
    }


    public int getX()
    {
        return x;
    }


    public int getY()
    {
        return y;
    }


    public int getZoom()
    {
        return zoom - 3;
    }
}
