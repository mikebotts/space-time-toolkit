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
 * Quad Tree
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO QuadTree type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Aug 10, 2006
 * @version 1.0
 */
public class QuadTree
{
    protected QuadTreeItem rootItem, topItem;
    protected double upLevelSurfaceRatio = 9;
    protected double downLevelSurfaceRatio = 7;
    
    
    public QuadTree()
    {
    }
    
    
    /**
     * Init the tree by setting the bbox as the root tile
     * @param bbox
     */
    public void init(SpatialExtent bbox)
    {
        rootItem = new QuadTreeItem(bbox.getMinX(), bbox.getMinY(), bbox.getMaxX(), bbox.getMaxY());
    }
    
    
    /**
     * Find matching and unused items depending on given bbox
     * @param matchingItems
     * @param unusedItems
     * @param bbox
     * @param maxLevel
     * @param maxDistance
     */
    public void findItems(ArrayList<QuadTreeItem> matchingItems, 
                          ArrayList<QuadTreeItem> unusedItems,
                          SpatialExtent bbox, int maxLevel, double maxDistance)
    {        
        // compute bbox surface
        double bboxSize = Math.abs(bbox.getMaxX() - bbox.getMinX()) * Math.abs(bbox.getMaxY() - bbox.getMinY());
        if (bboxSize == 0)
            return;
        
        if (rootItem == null)
        {
            rootItem = new QuadTreeItem(bbox.getMinX(), bbox.getMinY(), bbox.getMaxX(), bbox.getMaxY());
            topItem = rootItem;
        }
        
        // call findItems recursively in the tree
        topItem = rootItem.findTopItem(bbox, bboxSize);
        topItem.findChildItems(matchingItems, bbox, bboxSize, 0, maxLevel);
        rootItem.findUnusedItems(unusedItems, bbox, maxDistance);
    }
    
    
    
    // test method to be put in a JUnit Test!
    public static void main(String[] args)
    {
        ArrayList<QuadTreeItem> matchingItems = new ArrayList<QuadTreeItem>(30);
        ArrayList<QuadTreeItem> unusedItems = new ArrayList<QuadTreeItem>(30);
        
        QuadTree tree = new QuadTree();
        
        SpatialExtent bbox = new SpatialExtent();
        bbox.setMinX(-10);
        bbox.setMaxX(+10);
        bbox.setMinY(-10);
        bbox.setMaxY(+10);        
        
        tree.findItems(matchingItems, unusedItems, bbox, 3, 1);
        
        for (int i=0; i<matchingItems.size(); i++)
            System.out.println(matchingItems.get(i));
        
        // subset
        System.out.println();
        bbox.setMinX(-4.5);        
        bbox.setMinY(-4.5);
        bbox.setMaxX(+10);
        bbox.setMaxY(+10);
        
        matchingItems.clear();
        unusedItems.clear();
        tree.findItems(matchingItems, unusedItems, bbox, 3, 1);
        
        for (int i=0; i<matchingItems.size(); i++)
            System.out.println(matchingItems.get(i));
        
        // superset
        System.out.println();
        bbox.setMinX(-15);        
        bbox.setMinY(-15);
        bbox.setMaxX(+10);
        bbox.setMaxY(+10);
        
        matchingItems.clear();
        unusedItems.clear();
        tree.findItems(matchingItems, unusedItems, bbox, 3, 1);
        
        for (int i=0; i<matchingItems.size(); i++)
            System.out.println(matchingItems.get(i));
    }
}
