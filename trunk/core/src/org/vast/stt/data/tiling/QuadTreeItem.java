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

package org.vast.stt.data.tiling;

import java.util.ArrayList;
import org.vast.stt.project.SpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Quad Tree Item
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO QuadTreeItem type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 10, 2006
 * @version 1.0
 */
public class QuadTreeItem
{
    private final static int N = 1;
    private final static int E = 2;
    private final static int S = 4;
    private final static int W = 8;
    private final static int NE = N+E;
    private final static int NW = N+W;
    private final static int SE = S+E;
    private final static int SW = S+W;
    
    protected String id = "";
    protected double minX, minY;
    protected double maxX, maxY;
    protected double tileSize;
    protected Object data;
    protected QuadTreeItem parentBlock;
    //protected QuadTreeItem[] sideBlocks;   // 0->7 from N counter clockwise
    protected QuadTreeItem[] childBlocks;  // 0->3 from lower left, counter clockwise
    
    
    public QuadTreeItem()
    {
        childBlocks = new QuadTreeItem[4];
        //sideBlocks = new QuadTreeItem[8];
    }
    
    
    /**
     * Constructs a new item and add it as a child in the specified quadrant.
     * Quadrant is in the range 0-3, where 0 is lower left and going counter clockwise. 
     * @param parentBlock
     * @param quadrant
     */
    public QuadTreeItem(QuadTreeItem parentBlock, int quadrant)
    {
        this();
        this.parentBlock = parentBlock;
        
        double dX = (parentBlock.maxX - parentBlock.minX) / 2;
        double dY = (parentBlock.maxY - parentBlock.minY) / 2;
        tileSize = Math.abs(dX) * Math.abs(dY);
        
        switch (quadrant)
        {
            case 0:
                minX = parentBlock.minX;
                minY = parentBlock.minY;
                maxX = minX + dX;
                maxY = minY + dY;
                break;
                
            case 1:
                minX = parentBlock.minX + dX;
                minY = parentBlock.minY;
                maxX = minX + dX;
                maxY = minY + dY;
                break;
                
            case 2:
                minX = parentBlock.minX + dX;
                minY = parentBlock.minY + dY;
                maxX = minX + dX;
                maxY = minY + dY;
                break;
                
            case 3:
                minX = parentBlock.minX;
                minY = parentBlock.minY + dY;
                maxX = minX + dX;
                maxY = minY + dY;
                break;
        }
        
        this.id = parentBlock.id + (quadrant + 1);
    }
    
    
    /**
     * Constructs a new item and set it as the parent of the given child
     * expanding in the specified direction.
     * @param childBlock
     * @param directionOfExpansion
     */
    public QuadTreeItem(QuadTreeItem childBlock, int directionOfExpansion, boolean nothing)
    {
        this();
        
        double dX = (childBlock.maxX - childBlock.minX) * 2;
        double dY = (childBlock.maxY - childBlock.minY) * 2;
        tileSize = Math.abs(dX) * Math.abs(dY);
        
        switch (directionOfExpansion)
        {
            case N:
            case NE:
                childBlocks[0] = childBlock;
                minX = childBlock.minX;
                minY = childBlock.minY;
                maxX = minX + dX;
                maxY = minY + dY;
                break;
            
            case E:    
            case SE:
                childBlocks[3] = childBlock;
                minX = childBlock.minX;
                minY = childBlock.maxY - dY;
                maxX = minX + dX;
                maxY = childBlock.maxY;
                break;
            
            case S:
            case SW:
                childBlocks[2] = childBlock;
                minX = childBlock.maxX - dX;
                minY = childBlock.maxY - dY;
                maxX = childBlock.maxX;
                maxY = childBlock.maxY;
                break;
                
            case W:
            case NW:
                childBlocks[1] = childBlock;
                minX = childBlock.maxX - dX;
                minY = childBlock.minY;
                maxX = childBlock.maxX;
                maxY = minY + dY;
                break;
        }
    }
    
    
    public QuadTreeItem(double minX, double minY, double maxX, double maxY)
    {
        this();
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        tileSize = Math.abs(maxX - minX) * Math.abs(maxY - minY);
    }
    
    
    public void findChildItems(ArrayList<QuadTreeItem> matchingItems, SpatialExtent bbox, double bboxSize)
    {
        double sizeRatio = bboxSize / tileSize;
        
        // go down in the tree
        if (sizeRatio < 8)
        {
            for (int i=0; i<4; i++)
            {
                if (childBlocks[i] == null)
                    childBlocks[i] = new QuadTreeItem(this, i);
                
                if (childBlocks[i].intersects(bbox))
                    childBlocks[i].findChildItems(matchingItems, bbox, bboxSize);
            }            
        }
        
        // get tile at this level
        else
        {
            matchingItems.add(this);
        }
    }
    
    
    public QuadTreeItem findTopItem(SpatialExtent bbox, double bboxSize)
    {
        double sizeRatio = bboxSize / tileSize;
        
        if (sizeRatio > 16 || !this.contains(bbox))
        {
            if (parentBlock == null)
            {
                int dir = whereIs(bbox);
                parentBlock = new QuadTreeItem(this, dir, false);
            }
            
            return parentBlock.findTopItem(bbox, bboxSize);
        }
        else        
            return this;
    }
    
    
    public void createChildren()
    {
        for (int i=0; i<4; i++)
        {
            if (childBlocks[i] == null)
                childBlocks[i] = new QuadTreeItem(this, i);
        }
    }
    
    
    /**
     * 
     * TODO contains method description
     * @param bbox
     * @return
     */
    public boolean contains(SpatialExtent bbox)
    {
        double bboxX1 = bbox.getMinX();
        double bboxX2 = bbox.getMaxX();
        double bboxY1 = bbox.getMinY();
        double bboxY2 = bbox.getMaxY();
        
        if (bboxX1 < minX || bboxX1 > maxX)
            return false;
        
        if (bboxX2 < minX || bboxX2 > maxX)
            return false;
        
        if (bboxY1 < minY || bboxY1 > maxY)
            return false;
        
        if (bboxY2 < minY || bboxY2 > maxY)
            return false;
        
        return true;
    }
    
    
    /**
     * Finds out where the bbox is relative to this tile
     * @param bbox
     * @return
     */
    public boolean intersects(SpatialExtent bbox)
    {
        double bboxX1 = bbox.getMinX();
        double bboxX2 = bbox.getMaxX();
        double bboxY1 = bbox.getMinY();
        double bboxY2 = bbox.getMaxY();
        
        if (bboxX1 < minX && bboxX2 < minX)
            return false;
        
        if (bboxX1 > maxX && bboxX2 > maxX)
            return false;
        
        if (bboxY1 < minY && bboxY2 < minY)
            return false;
        
        if (bboxY1 > maxY && bboxY2 > maxY)
            return false;
        
        return true;
    }
    
    
    /**
     * Finds out where the bbox is relative to this tile
     * @param bbox
     * @return
     */
    public int whereIs(SpatialExtent bbox)
    {
        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;
        
        double dX1 = bbox.getMinX() - centerX;
        double dX2 = bbox.getMaxX() - centerX;
        double vX = (dX2 + dX1) / 2;
        
        double dY1 = bbox.getMinY() - centerY;
        double dY2 = bbox.getMaxY() - centerY;
        double vY = (dY2 + dY1) / 2;
        
        int dir = 0;
        
        if (vX > 0)
            dir += E;
        else if (vX < 0)
            dir += W;
        
        if (vY > 0)
            dir += N;
        else if (vY < 0)
            dir += S;

        return dir;
    }
    

    public Object getData()
    {
        return data;
    }


    public void setData(Object data)
    {
        this.data = data;
    }


    public double getMinX()
    {
        return minX;
    }


    public void setMinX(double minX)
    {
        this.minX = minX;
    }


    public double getMinY()
    {
        return minY;
    }


    public void setMinY(double minY)
    {
        this.minY = minY;
    }
    
    
    public double getMaxX()
    {
        return maxX;
    }


    public void setMaxX(double maxX)
    {
        this.maxX = maxX;
    }


    public double getMaxY()
    {
        return maxY;
    }


    public void setMaxY(double maxY)
    {
        this.maxY = maxY;
    }
    
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(maxX-minX);
        buf.append(" x ");
        buf.append(maxY-minY);
        buf.append(" (");
        buf.append(minX);
        buf.append(",");
        buf.append(minY);
        buf.append(" - ");
        buf.append(maxX);
        buf.append(",");
        buf.append(maxY);
        buf.append(")");
        return buf.toString();
    }
}
