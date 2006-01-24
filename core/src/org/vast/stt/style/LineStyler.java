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
import org.vast.ows.sld.Color;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.DataNode;


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
	protected LinePointGraphic point;
	protected LineSegmentGraphic segment;
	protected DataComponent pointArray;
    protected int[][] segIndices, xIndices, yIndices, zIndices;
	
	
	public LineStyler()
	{
		point = new LinePointGraphic();
		segment = new LineSegmentGraphic();
		//  force a default name
		setName("Line Styler");
	}

	
	public int getSegmentCount()
	{
		return this.getComponentCount(segIndices);
	}
    
    
    public LineSegmentGraphic getSegment(int i)
    {
        node.getComponent(i).getComponent(0);
        pointArray = this.getComponent(node, segIndices, i);
        segment.segmentSize = pointArray.getComponentCount();
        
        xIndices[0][1] = segment.segmentSize;
        yIndices[0][1] = segment.segmentSize;
        
        return segment;
    }
	
	
	public LinePointGraphic getPoint(int i)
	{      
		point.x = this.getComponent(pointArray, xIndices, i).getData().getDoubleValue();
		point.y = this.getComponent(pointArray, yIndices, i).getData().getDoubleValue();
		return point;
	}
    
    
    public void updateBoundingBox()
    {
        // TODO Auto-generated method stub
        // compute bounding box by scanning max and min geometry values
    }


    public void updateDataMappings()
    {
        ScalarParameter param;
        String propertyName;
        String segmentPropertyName = null;
        Object value;
        
        // generate indexing rules for geometry components
        DataNode newNode = dataProvider.getDataNode();
        if (newNode == node)
            return;
        
        node = newNode;
        
        // geometry object
        param = this.symbolizer.getGeometry().getObject();
        if (param != null)
        {
            segmentPropertyName = param.getPropertyName();            
            if (segmentPropertyName != null)
                segIndices = computeIndices(null, segmentPropertyName);
        }
        
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
                xIndices = computeIndices(segmentPropertyName, propertyName);
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
                yIndices = computeIndices(segmentPropertyName, propertyName);
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
                zIndices = computeIndices(segmentPropertyName, propertyName);
        }
                
        // line color
        param = this.symbolizer.getStroke().getColor();
        if (param != null)
        {
            value = param.getConstantValue();            
            if (value != null)
            {
                Color lineColor = (Color)value;
                point.r = lineColor.getRed();
                point.g = lineColor.getGreen();
                point.b = lineColor.getBlue();
                point.a = lineColor.getAlpha();
            }
        }
        
        // line width
        param = this.symbolizer.getStroke().getWidth();
        if (param != null)
        {
            value = param.getConstantValue();            
            if (value != null)
            {
                Float lineWidth = (Float)value;
                point.width = lineWidth.intValue();
            }
        }     
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
        updateDataMappings(); //TODO shouldn't be called here -> not too efficient !
		visitor.visit(this);		
	}
}
