/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.tiling;

import org.vast.stt.provider.tiling.QuadTreeItem;
import org.vast.stt.provider.tiling.QuadTreeVisitor;
import org.vast.util.SpatialExtent;


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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Oct 13, 2006
 * @version 1.0
 */
public abstract class ExtentSelector implements QuadTreeVisitor
{
	protected SpatialExtent maxExtent;
	protected SpatialExtent roi1, roi2;
    protected boolean splitROI;
    protected double roiSize;
    protected double sizeRatio; // ratio roiSize/tileSize
    protected int maxLevel, minLevel, currentLevel;
    
    
    public ExtentSelector(int minLevel, int maxLevel)
    {
        this.maxLevel = maxLevel;
        this.minLevel = minLevel;
        this.maxExtent = new SpatialExtent();
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
        if (item.intersects(roi1) || (splitROI && item.intersects(roi2)))
        {
            if (item.getTileSize() * sizeRatio < roiSize)
            {
                selectItem(item);
            }
            else
            {
                currentLevel++;
                
                if (currentLevel < maxLevel)
                {
                	// enforce all children
                    for (byte i=0; i<4; i++)
                    {
                        if (item.getChild(i) == null)
                            new QuadTreeItem(item, i);
                    }
                	visitChildren(item);
                }
                else
                	selectItem(item);
                
                currentLevel--;
            }
            
            item.needed = true;
        }
        else
        {
            deselectItem(item);
            item.needed = false;
        }
    }


    public void setROI(SpatialExtent roi)
    {
        this.roi1 = roi.copy();
        this.roi2 = roi.copy();
        splitROI = false;
        
        // adjust rois
        if (roi.getMinX() < maxExtent.getMinX())
        {
            roi1.setMinX(maxExtent.getMinX());
            roi2.setMinX(roi.getMinX() + maxExtent.getSizeX());
            roi2.setMaxX(maxExtent.getMaxX());
            splitROI = true;
        }
        else if (roi.getMaxX() > maxExtent.getMaxX())
        {
            roi1.setMaxX(maxExtent.getMaxX());
            roi2.setMinX(maxExtent.getMinX());
            roi2.setMaxX(roi.getMaxX() - maxExtent.getSizeX());
            splitROI = true;
        }
        
        // precompute bbox size
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


	public SpatialExtent getMaxExtent()
	{
		return maxExtent;
	}


	public void setMaxExtent(SpatialExtent maxExtent)
	{
		this.maxExtent = maxExtent;
	}
}
