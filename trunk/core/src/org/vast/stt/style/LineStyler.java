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

import org.ogc.cdm.common.DataComponent;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.DataNode;
import org.vast.stt.util.SpatialExtent;


/**
 * <p><b>Title:</b><br/>
 * Line Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Converts source data to a sequence of LineGraphic objects
 * that the renderer can access and render sequentially. 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class LineStyler extends AbstractStyler
{
	protected LineSymbolizer symbolizer;
	protected PointGraphic point;
	protected LineGraphic segment;
	protected DataNode node;
	protected DataComponent pointArray;
	
	
	public LineStyler()
	{
		point = new PointGraphic();
		segment = new LineGraphic();
	}
	
//	public void draw()
//	{
//		DataNode node = dataProvider.getDataNode();
//		
//		for (int i=0; i<node.getComponentCount(); i++)
//		{
//			node.getComponent(i).getComponent(0);
//			DataComponent pointArray = node.getComponent(i).getComponent(1);
//			
//			GL.glColor3f(1.0f, 0.0f, 0.0f);
//			GL.glBegin(GL.GL_LINE_STRIP);
//			
//			for (int j=0; j<pointArray.getComponentCount(); j++)
//			{
//				DataComponent point = pointArray.getComponent(j);
//				double x = point.getComponent(1).getData().getDoubleValue();
//				double y = point.getComponent(0).getData().getDoubleValue();
//				GL.glVertex2d(x, y);
//			}
//			
//			GL.glEnd();
//		}
//	}
	
	public LineGraphic getSegment(int i)
	{
		node.getComponent(i).getComponent(0);
		pointArray = node.getComponent(i).getComponent(1);
		segment.segmentSize = pointArray.getComponentCount();
		segment.width = 1;
		return segment;
	}
	
	
	public int getSegmentCount()
	{
		return node.getComponentCount();
	}
	
	
	public PointGraphic getPoint(int i)
	{
		DataComponent pointData = pointArray.getComponent(i);
		point.x = pointData.getComponent(1).getData().getDoubleValue();
		point.y = pointData.getComponent(0).getData().getDoubleValue();
		point.r = 1.0f;
		return point;
	}
	
	
	public SpatialExtent getBoundingBox()
	{
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
	
	
	public LineSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (LineSymbolizer)sym;
	}


	public void accept(StylerVisitor visitor)
	{
		node = dataProvider.getDataNode();
		visitor.visit(this);		
	}
}
