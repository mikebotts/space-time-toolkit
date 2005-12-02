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

import org.vast.ows.sld.PolygonSymbolizer;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.util.SpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Polygon Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class PolygonStyler extends AbstractStyler
{
	protected PolygonSymbolizer symbolizer;
	
	
	public PolygonStyler()
	{
	}
	
	
	public SpatialExtent getBoundingBox()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public double[] getCenterPoint()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public void updateBoundingBox()
	{
		// TODO Auto-generated method stub
		
	}


	public void updateDataMappings()
	{
		// TODO Auto-generated method stub
		
	}
	
	
	public PolygonSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (PolygonSymbolizer)sym;
	}


	public void accept(StylerVisitor visitor)
	{
		visitor.visit(this);		
	}
}
