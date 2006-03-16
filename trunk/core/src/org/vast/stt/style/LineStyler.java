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

import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


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
    protected DataIndexer redData, greenData, blueData, alphaData;
    protected DataIndexer widthData;
    protected int breakRuleIndex;
    
	
	public LineStyler()
	{
		point = new LinePointGraphic();
        segment = new LineSegmentGraphic();

		//  force a default name
		setName("Line Styler");
	}
    
    
    public boolean hasNext()
    {
        return xData.hasNext();
    }
    
    
    public LinePointGraphic nextPoint()
    {
        point.lineBreak = false;
                        
        if (xData != null)
        {
            int linearIndex = xData.nextIndex();
            point.x = xData.dataBlock.getDoubleValue(linearIndex);
            
            // take care of breaks
            if (xData.indexRules[breakRuleIndex].newItem)
            {
                point.lineBreak = true;
                xData.indexRules[breakRuleIndex].newItem = false;
            }
        }
        
        if (yData != null)
        {
            int linearIndex = yData.nextIndex();
            point.y = yData.dataBlock.getDoubleValue(linearIndex);
        }
        
        if (zData != null)
        {
            int linearIndex = zData.nextIndex();
            point.z = zData.dataBlock.getDoubleValue(linearIndex);
        }
        
        if (redData != null)
        {
            int linearIndex = redData.nextIndex();
            point.r = redData.dataBlock.getFloatValue(linearIndex);
        }
        
        if (greenData != null)
        {
            int linearIndex = greenData.nextIndex();
            point.g = greenData.dataBlock.getFloatValue(linearIndex);
        }
        
        if (blueData != null)
        {
            int linearIndex = blueData.nextIndex();
            point.b = blueData.dataBlock.getFloatValue(linearIndex);
        }
        
        if (alphaData != null)
        {
            int linearIndex = alphaData.nextIndex();
            point.a = alphaData.dataBlock.getFloatValue(linearIndex);
        }
        
        if (widthData != null)
        {
            int linearIndex = widthData.nextIndex();
            point.width = widthData.dataBlock.getIntValue(linearIndex);
        }
        
        return point;
    }
    
    
    public int getSegmentCount()
    {
        return currentData.getComponentCount();
    }
    
    
    public LineSegmentGraphic getLineSegment(int segmentIndex)
    {
        return null;
    }

	
    public LinePointGraphic getLinePoint(int pointIndex)
	{      
        return null;
	}
    
    
    public void updateBoundingBox()
    {
        // TODO Auto-generated method stub
        // compute bounding box by scanning max and min geometry values
    }


    public void updateDataMappings()
    {
        ScalarParameter param;
        String propertyName = null;
        Object value;
        
        
        // generate indexing rules for geometry components        
        
        // segment object
        param = this.symbolizer.getGeometry().getObject();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            DataIndexer segData = new DataIndexer(currentData, propertyName);
            breakRuleIndex = segData.indexRules.length-1;
        }
        
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                xData = new DataIndexer(currentData, propertyName);
                dataHelpers.add(xData);                
            }
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                yData = new DataIndexer(currentData, propertyName);
                dataHelpers.add(yData);                
            }
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                zData = new DataIndexer(currentData, propertyName);
                dataHelpers.add(zData);                
            }
        }
        
        // color - red 
        param = this.symbolizer.getStroke().getColor().getRed();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.r = (Float)value;
            }
            else
            {
                if (!param.isMapped())
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        redData = new DataIndexer(currentData, propertyName);
                        dataHelpers.add(redData);                
                    }
                }
            }
        }
        
        // color - green 
        param = this.symbolizer.getStroke().getColor().getGreen();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.g = (Float)value;
            }
            else
            {
                if (!param.isMapped())
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        greenData = new DataIndexer(currentData, propertyName);
                        dataHelpers.add(greenData);                
                    }
                }
            }
        }
        
        // color - blue 
        param = this.symbolizer.getStroke().getColor().getBlue();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.b = (Float)value;
            }
            else
            {
                if (!param.isMapped())
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        blueData = new DataIndexer(currentData, propertyName);
                        dataHelpers.add(blueData);                
                    }
                }
            }
        }
        
        // color - alpha 
        param = this.symbolizer.getStroke().getColor().getAlpha();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.a = (Float)value;
            }
            else
            {
                if (!param.isMapped())
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        alphaData = new DataIndexer(currentData, propertyName);
                        dataHelpers.add(alphaData);                
                    }
                }
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
        
        // optimize rules
        yData.optimizeIndexRules(xData);
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
        currentData = dataProvider.getDataNode();

        if (currentData != null)
        {
            if ((xData == null) && (yData == null) && (zData == null))
                updateDataMappings();
            
    		visitor.visit(this);
        }
	}
}
