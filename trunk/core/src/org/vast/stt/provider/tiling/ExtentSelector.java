/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.tiling;

import org.vast.physics.SpatialExtent;
import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.QuadTreeVisitor;


/**
 * <p><b>Title:</b>
 * Extent Selector
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This visitor selectss item based on the given Spatial Extent.
 * It will select all items intersecting the given Spatial Extent
 * and deselects any others. Only items with sufficient level of
 * details are selected. Items at a given distance from the ROI
 * (SpatialExtent) are completely deleted from memory while the
 * ones closer are simply deselected (hidden).
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Oct 13, 2006
 * @version 1.0
 */
public abstract class ExtentSelector implements QuadTreeVisitor
{
    protected SpatialExtent roi;
    protected double roiSize;
    protected double sizeRatio; // ratio roiSize/tileSize
    protected int maxLevel, currentLevel;
    
    
    public ExtentSelector(double sizeRatio, int maxLevel)
    {
        this.sizeRatio = sizeRatio;        
        this.maxLevel = maxLevel;
    }
    
    
    protected abstract void selectItem(QuadTreeItem item);
    protected abstract void deselectItem(QuadTreeItem item);
    
        
    protected void visitChildren(QuadTreeItem item)
    {
        for (int i=0; i<4; i++)
        {
            QuadTreeItem child = item.getChild(i);
            if (child != null)
                child.accept(this);
        }
    }
    
    
    public void visit(QuadTreeItem item)
    {
        if (item.intersects(roi))
        {
            if (item.getTileSize() * sizeRatio < roiSize)
            {
                selectItem(item);
                return;
            }
            
            // enforce all children
            for (byte i=0; i<4; i++)
            {
                if (item.getChild(i) == null)
                    new QuadTreeItem(item, i);
            }
            
            //deselectItem(item);
            currentLevel++;            
            if (currentLevel < maxLevel)
                visitChildren(item);            
            currentLevel--;
        }
        else
        {
            deselectItem(item);
        }
    }


    public void setROI(SpatialExtent roi)
    {
        this.roi = roi;
        this.roiSize = Math.abs(roi.getMaxX() - roi.getMinX()) * Math.abs(roi.getMaxY() - roi.getMinY());
    }


    public void setCurrentLevel(int currentLevel)
    {
        this.currentLevel = currentLevel;
    }


    public int getMaxLevel()
    {
        return maxLevel;
    }


    public void setMaxLevel(int maxLevel)
    {
        this.maxLevel = maxLevel;
    }


    public double getSizeRatio()
    {
        return sizeRatio;
    }


    public void setSizeRatio(double sizeRatio)
    {
        this.sizeRatio = sizeRatio;
    }
}
