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
    protected QuadTreeItem rootItem;
    protected QuadTreeItem currentItem;
    protected double upLevelSurfaceRatio = 9;
    protected double downLevelSurfaceRatio = 7;
    
    
    public QuadTree()
    {
    }
    
    
    public ArrayList<QuadTreeItem> findItems(SpatialExtent bbox)
    {
        // create a fresh list of items
        ArrayList<QuadTreeItem> matchingItems = new ArrayList<QuadTreeItem>(30);
        
        // compute bbox surface
        double bboxSize = Math.abs(bbox.getMaxX() - bbox.getMinX()) * Math.abs(bbox.getMaxY() - bbox.getMinY());
        
        if (rootItem == null)
            rootItem = new QuadTreeItem(bbox.getMinX(), bbox.getMinY(), bbox.getMaxX(), bbox.getMaxY());
        
        // call findItems recursively in the tree
        rootItem = rootItem.findTopItem(bbox, bboxSize);
        rootItem.findChildItems(matchingItems, bbox, bboxSize);
        
        return matchingItems;
    }
    
    
    public static void main(String[] args)
    {
        QuadTree tree = new QuadTree();
        
        SpatialExtent bbox = new SpatialExtent();
        bbox.setMinX(-10);
        bbox.setMaxX(+10);
        bbox.setMinY(-10);
        bbox.setMaxY(+10);        
        
        ArrayList<QuadTreeItem> list = tree.findItems(bbox);
        
        for (int i=0; i<list.size(); i++)
            System.out.println(list.get(i));
        
        // subset
        System.out.println();
        bbox.setMinX(-4.5);        
        bbox.setMinY(-4.5);
        bbox.setMaxX(+10);
        bbox.setMaxY(+10);
        
        list.clear();
        list = tree.findItems(bbox);
        
        for (int i=0; i<list.size(); i++)
            System.out.println(list.get(i));
        
        // superset
        System.out.println();
        bbox.setMinX(-15);        
        bbox.setMinY(-15);
        bbox.setMaxX(+10);
        bbox.setMaxY(+10);
        
        list.clear();
        list = tree.findItems(bbox);
        
        for (int i=0; i<list.size(); i++)
            System.out.println(list.get(i));
    }
}
