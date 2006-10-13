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

package org.vast.stt.provider.tiling;

import java.util.ArrayList;
import org.vast.physics.SpatialExtent;


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
public class QuadTreeItem extends SpatialExtent
{
    private final static int N = 1;
    private final static int E = 2;
    private final static int S = 4;
    private final static int W = 8;
    private final static int NE = N+E;
    private final static int NW = N+W;
    private final static int SE = S+E;
    private final static int SW = S+W;
    
    protected double tileSize;
    protected double distance;
    protected byte quadrant;
    protected Object data;
    protected QuadTreeItem parent;
    protected QuadTreeItem[] children;  // 0->3 from lower left, counter clockwise
    
    
    public QuadTreeItem()
    {
        children = new QuadTreeItem[4];
    }
    
    
    /**
     * Constructs a new item and add it as a child in the specified quadrant.
     * Quadrant is in the range 0-3, where 0 is lower left and going counter clockwise. 
     * @param parentBlock
     * @param quadrant
     */
    public QuadTreeItem(QuadTreeItem parentBlock, byte quadrant)
    {
        this();
        this.parent = parentBlock;
        this.quadrant = quadrant;
        
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
                children[0] = childBlock;
                childBlock.quadrant = 0;
                minX = childBlock.minX;
                minY = childBlock.minY;
                maxX = minX + dX;
                maxY = minY + dY;
                break;
            
            case E:    
            case SE:
                children[3] = childBlock;
                childBlock.quadrant = 3;
                minX = childBlock.minX;
                minY = childBlock.maxY - dY;
                maxX = minX + dX;
                maxY = childBlock.maxY;
                break;
            
            case S:
            case SW:
                children[2] = childBlock;
                childBlock.quadrant = 2;
                minX = childBlock.maxX - dX;
                minY = childBlock.maxY - dY;
                maxX = childBlock.maxX;
                maxY = childBlock.maxY;
                break;
                
            case W:
            case NW:
                children[1] = childBlock;
                childBlock.quadrant = 1;
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
    
    
    /**
     * Expand tree until the root tile contains the bbox
     * @param bbox
     * @param bboxSize
     * @return
     */
    public QuadTreeItem findTopItem(SpatialExtent bbox, double bboxSize)
    {
        double sizeRatio = bboxSize / tileSize;
        
        if (sizeRatio > 16 || !this.contains(bbox))
        {
            // create parent if null
            if (parent == null)
            {
                int dir = whereIs(bbox);
                parent = new QuadTreeItem(this, dir, false);
            }
            
            return parent.findTopItem(bbox, bboxSize);
        }
        else        
            return this;
    }
    
    
    /**
     * Finds all child items that intersects that bbox and
     * are at the right level of details.
     * @param matchingItems
     * @param unusedItems
     * @param bbox
     * @param bboxSize
     * @param level
     * @param maxLevel
     * @param maxDistance
     */
    public void findChildItems(ArrayList<QuadTreeItem> matchingItems,
                               SpatialExtent bbox, double bboxSize,
                               int level, int maxLevel)
    {
        double sizeRatio = bboxSize / tileSize;
        
        // we reached the good level
        if (sizeRatio >= 2 || level == maxLevel)
        {
            addToMatchingItemsAndSort(matchingItems, bbox);
        }
        
        // go down in the tree
        else
        {
            for (byte i=0; i<4; i++)
            {
                if (children[i] == null)
                    children[i] = new QuadTreeItem(this, i);
                
                if (children[i].intersects(bbox))
                    children[i].findChildItems(matchingItems, bbox, bboxSize, level+1, maxLevel);
            }
        }
    }

    
    /**
     * Add item and all children to unused items if far away enough from bbox
     * @param unusedItems
     * @param bbox
     * @param maxDistance
     * @return
     */
    public void findUnusedItems(ArrayList<QuadTreeItem> unusedItems, SpatialExtent bbox, double maxDistance)
    {
        double dX =  (bbox.getMinX() + bbox.getMaxX() - minX - maxX) / 2;
        double dY =  (bbox.getMinY() + bbox.getMaxY() - minY - maxY) / 2;
        double distance = dX*dX + dY*dY;
        
        // loop through children
        for (byte i=0; i<4; i++)
        {
            if (children[i] != null)
                children[i].findUnusedItems(unusedItems, bbox, maxDistance);
        }
        
        // figure out if the tile is too far
        if (distance > tileSize*maxDistance*maxDistance)
        {
            unusedItems.add(this);       
            parent.children[quadrant] = null;
        }
    }
    
    
    /**
     * Add the item in the list at the right position so that the list
     * is sorted from closest to farthest to the center of the bbox
     * @param matchingItems
     * @param bbox
     */
    protected void addToMatchingItemsAndSort(ArrayList<QuadTreeItem> matchingItems, SpatialExtent bbox)
    {
        double dX =  (bbox.getMinX() + bbox.getMaxX() - minX - maxX) / 2;
        double dY =  (bbox.getMinY() + bbox.getMaxY() - minY - maxY) / 2;
        distance = dX*dX + dY*dY;
        
        if (matchingItems.isEmpty())
        {
            matchingItems.add(this);
        }
        else
        {
            for (int i=0; i<matchingItems.size(); i++)
            {
                if (matchingItems.get(i).distance >= distance)
                {
                    matchingItems.add(i, this);
                    return;
                }
            }
            
            // if we didn't add it yet, add at end of list
            matchingItems.add(this);
        }
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
    
    
    public void appendId(StringBuffer buf)
    {
        if (parent != null)
        {
            parent.appendId(buf);
            buf.append(quadrant);
        }
        else
            buf.append('q');
    }
    
    
    public void acceptUp(QuadTreeItemVisitor visitor)
    {
        visitor.visit(this);
        if (parent != null)
            parent.acceptUp(visitor);
    }
    
    
    public void acceptDown(QuadTreeItemVisitor visitor)
    {
        visitor.visit(this);
        
        for (int i=0; i<4; i++)
        {
            if (children[i] != null)
                children[i].acceptDown(visitor);
        }
    }
    
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        appendId(buf);
        buf.append(": ");
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


    public QuadTreeItem getChild(int i)
    {
        return children[i];
    }


    public void setChild(int i, QuadTreeItem item)
    {
        this.children[i] = item;
    }


    public QuadTreeItem getParent()
    {
        return parent;
    }


    public void setParent(QuadTreeItem parentBlock)
    {
        this.parent = parentBlock;
    }


    public byte getQuadrant()
    {
        return quadrant;
    }
}
