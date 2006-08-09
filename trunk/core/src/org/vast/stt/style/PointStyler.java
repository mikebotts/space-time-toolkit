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
    protected PointGraphic point;
    protected PointSymbolizer symbolizer;	
    	
	
	public PointStyler()
	{
		point = new PointGraphic();
	}
    
    
    public PointGraphic nextPoint()
    {
        if (dataLists[0].blockIndexer.hasNext())
        {
            point.x = point.y = point.z = 0.0;
            dataLists[0].blockIndexer.next();
            
            // adjust geometry to fit projection
            projection.adjust(geometryCrs, point);

            // add point to bbox if needed
            addToExtent(currentBlockInfo, point);
            
            return point;
        }
        
        return null;
    }
    
    
    @Override
    public void computeBoundingBox()
    {
        this.wantComputeExtent = true;
        this.resetIterators();
                
        while (nextBlock() != null)
            while (nextPoint() != null);
    }


    @Override
	public void updateDataMappings()
	{
        ScalarParameter param;
        String propertyName;
        Object value;
        
        // reset all parameters
        point = new PointGraphic();
        this.clearAllMappers();
               
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();          
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericXMapper(point, param.getMappingFunction()));                
            }
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericYMapper(point, param.getMappingFunction()));                
            }
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericZMapper(point, param.getMappingFunction()));                
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
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        addPropertyMapper(propertyName, new GenericRedMapper(point, param.getMappingFunction()));              
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
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        addPropertyMapper(propertyName, new GenericGreenMapper(point, param.getMappingFunction()));               
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
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        addPropertyMapper(propertyName, new GenericBlueMapper(point, param.getMappingFunction()));             
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
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        addPropertyMapper(propertyName, new GenericAlphaMapper(point, param.getMappingFunction()));              
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new PointSizeMapper(point, param.getMappingFunction()));              
                }
            }
        }
        
        // point orientation
        param = this.symbolizer.getGraphic().getRotation();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.orientation = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new PointOrientationMapper(point, param.getMappingFunction()));              
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
        dataNode = dataItem.getDataProvider().getDataNode();
        
        if (dataNode.isNodeStructureReady())
        {
            if (dataLists.length == 0)
                updateDataMappings();
                        
    		visitor.visit(this);
        }
	}
}