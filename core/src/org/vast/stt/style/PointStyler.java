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

import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.DataNode;
import org.vast.stt.util.SpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Point Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Converts source data to a sequence of PointGraphic objects
 * that the renderer can access and render sequentially. 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class PointStyler extends AbstractStyler
{
	protected PointSymbolizer symbolizer;
	protected PointGraphic point;
	protected DataNode node;
	
	
	public PointStyler()
	{
		point = new PointGraphic();
		//  force a default name
		setName("Point Styler");
	}
	
	
	public PointGraphic getPoint(int i)
	{
		point.x = node.getComponent(i).getComponent(2).getData().getDoubleValue();
		point.y = node.getComponent(i).getComponent(1).getData().getDoubleValue();
		point.z = node.getComponent(i).getComponent(3).getData().getDoubleValue() / 1e6;
		point.g = 1.0f;
		return point;
	}
	
	
	public int getPointCount()
	{
		return node.getComponentCount();
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
	
	
	public PointSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (PointSymbolizer)sym;
	}
	
	
	public void accept(StylerVisitor visitor)
	{
		node = dataProvider.getDataNode();
		visitor.visit(this);		
	}
}
