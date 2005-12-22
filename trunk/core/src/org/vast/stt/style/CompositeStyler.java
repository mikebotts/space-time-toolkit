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

import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.DataProvider;
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
public class CompositeStyler extends ArrayList<DataStyler> implements DataStyler
{
	static final long serialVersionUID = 0;
	protected String name;
	protected SpatialExtent bbox;
	protected double[] centerPoint;
	
	
	public CompositeStyler()
	{
	}
	
	
	public CompositeStyler(int numStylers)
	{
		super(numStylers);
	}
	
	
	public void accept(StylerVisitor visitor)
	{
		// loop through all child stylers
		for (int i=0; i<size(); i++)
			get(i).accept(visitor);
	}
	
	
	public void updateBoundingBox()
	{
		// use first symbolizer to compute bounding box
		// should use the biggest of all child bbox ??
		if (size() > 0)
		{
			get(0).updateBoundingBox();
			this.bbox = get(0).getBoundingBox();
			this.centerPoint = get(0).getCenterPoint();
		}
	}


	public void updateDataMappings()
	{
		// loop through all child stylers
		for (int i=0; i<size(); i++)
			get(i).updateDataMappings();
	}


	public SpatialExtent getBoundingBox()
	{
		return bbox;
	}


	public double[] getCenterPoint()
	{
		return centerPoint;
	}


	public void setDataProvider(DataProvider dataProvider)
	{
	}


	public Symbolizer getSymbolizer()
	{
		return null;
	}
	
	
	public void setSymbolizer(Symbolizer sym)
	{
	}


	public boolean isEnabled()
	{
		// loop through all child stylers
		for (int i=0; i<size(); i++)
		{
			if (get(i).isEnabled())
				return true;			
		}
		
		return false;
	}


	public void setEnabled(boolean enabled)
	{
		// loop through all child stylers
		for (int i=0; i<size(); i++)
			get(i).setEnabled(enabled);
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}
}
