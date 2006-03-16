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

import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.GraphicSource;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


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
    protected DataIndexer redData, greenData, blueData, alphaData;
    protected DataIndexer pointSizeData;
    	
	
	public PointStyler()
	{
		point = new PointGraphic();
		//  force a default name
		setName("Point Styler");
	}
    
    
    public boolean hasNext()
    {
        return xData.hasNext();
    }
    
    
    public PointGraphic nextPoint()
    {
        if (xData != null)
        {
            int linearIndex = xData.nextIndex();
            point.x = xData.dataBlock.getDoubleValue(linearIndex);
        }
        
        if (yData != null)
        {
            int linearIndex = yData.nextIndex();
            point.y = yData.dataBlock.getDoubleValue(linearIndex);
        }
        
        if (zData != null)
        {
            int linearIndex = zData.nextIndex();
            point.z = zData.dataBlock.getDoubleValue(linearIndex) / 1e6; //TODO remove scale factor hack;
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
        
        if (pointSizeData != null)
        {
            int linearIndex = pointSizeData.nextIndex();
            point.size = pointSizeData.dataBlock.getIntValue(linearIndex);
        }
        
        return point;
    }
	
	
	public PointGraphic getPoint(int pointIndex)
	{
	    return null;
	}
	
	
	public int getPointCount()
	{
	    return 0;
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
                
        // simple graphic mark
        GraphicSource glyph = this.symbolizer.getGraphic().getGlyphs().get(0);
        if (glyph instanceof GraphicMark)
        {
            // color - red 
            param = ((GraphicMark)glyph).getFill().getColor().getRed();
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
            param = ((GraphicMark)glyph).getFill().getColor().getGreen();
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
            param = ((GraphicMark)glyph).getFill().getColor().getBlue();
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
            param = ((GraphicMark)glyph).getFill().getColor().getAlpha();
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
        }        
        
        // point size
        param = this.symbolizer.getGraphic().getSize();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.size = ((Float)value).intValue();
            }
            else
            {
                if (!param.isMapped())
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        pointSizeData = new DataIndexer(currentData, propertyName);
                        dataHelpers.add(pointSizeData);               
                    }
                }
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
        currentData = dataProvider.getDataNode();
        
        if (currentData != null)
        {
            if ((xData == null) && (yData == null) && (zData == null))
                updateDataMappings();
            
    		visitor.visit(this);
        }
	}
}
