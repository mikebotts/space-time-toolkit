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

import java.util.ArrayList;
import java.util.List;

import org.vast.ows.sld.Symbolizer;
import org.vast.stt.util.SpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Composite Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Composite Styler contains a group of other stylers to be rendered
 * sequentially.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class CompositeStyler extends AbstractStyler
{
    protected List<DataStyler> stylerList;
	
	
	public CompositeStyler()
	{
        stylerList = new ArrayList<DataStyler>(1);
	}
	
	
	public CompositeStyler(int numStylers)
	{
        stylerList = new ArrayList<DataStyler>(numStylers);
	}
	
	
	public void accept(StylerVisitor visitor)
	{
		// loop through all child stylers
		for (int i=0; i<stylerList.size(); i++)
            stylerList.get(i).accept(visitor);
	}
	
	
	public void updateBoundingBox()
	{
        // compute smallest bbox containing all children bbox
        for (int i=0; i<stylerList.size(); i++)
		{
            DataStyler nextStyler = stylerList.get(i);
            nextStyler.updateBoundingBox();
            SpatialExtent childBox =  nextStyler.getBoundingBox();
			
            if (i == 0)
                bbox = childBox.copy();
            else
                bbox.add(childBox);
		}
	}


	public void updateDataMappings()
	{
		// loop through all child stylers
        for (int i=0; i<stylerList.size(); i++)
            stylerList.get(i).updateDataMappings();
	}


	public boolean isEnabled()
	{
		// loop through all child stylers
        // if one is enabled return enabled
		for (int i=0; i<stylerList.size(); i++)
		{
			if (stylerList.get(i).isEnabled())
				return true;			
		}
		
		return false;
	}


	public void setEnabled(boolean enabled)
	{
		// loop through all child stylers
        for (int i=0; i<stylerList.size(); i++)
            stylerList.get(i).setEnabled(enabled);
	}
    
    
    public Symbolizer getSymbolizer()
    {
        return null;
    }
    
    
    public void setSymbolizer(Symbolizer sym)
    {
    }


    public List<DataStyler> getStylerList()
    {
        return stylerList;
    }


    public void setStylerList(List<DataStyler> stylerList)
    {
        this.stylerList = stylerList;
    }
}
