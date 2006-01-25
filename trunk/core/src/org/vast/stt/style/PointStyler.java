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

import org.vast.ows.sld.Color;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.GraphicSource;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.DataNode;


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
    protected int[][] xIndices, yIndices, zIndices;
	
	
	public PointStyler()
	{
		point = new PointGraphic();
		//  force a default name
		setName("Point Styler");
	}
	
	
	public PointGraphic getPoint(int i)
	{
        point.x = this.getComponent(node, xIndices, i).getData().getDoubleValue();
        point.y = this.getComponent(node, yIndices, i).getData().getDoubleValue();
        point.z = this.getComponent(node, zIndices, i).getData().getDoubleValue() / 1e6;
        return point;
	}
	
	
	public int getPointCount()
	{
		return this.getComponentCount(xIndices);
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
        Object value;
        
        // generate indexing rules for geometry components
        DataNode newNode = dataProvider.getDataNode();
        //if (newNode == node)
        //    return;
        
        node = newNode;
        
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
                xIndices = computeIndices(null, propertyName);
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
                yIndices = computeIndices(null, propertyName);
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
                zIndices = computeIndices(null, propertyName);
        }
                
        // point color
        GraphicSource glyph = this.symbolizer.getGraphic().getGlyphs().get(0);
        if (glyph instanceof GraphicMark)
        {
            param = ((GraphicMark)glyph).getFill().getColor();
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
        }        
        
        // point size
        param = this.symbolizer.getGraphic().getSize();
        if (param != null)
        {
            value = param.getConstantValue();
            if (value != null)
            {
                Float pointSize = (Float)value;
                point.size = pointSize.intValue();
            }
        }
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
        updateDataMappings();
		visitor.visit(this);		
	}
}
