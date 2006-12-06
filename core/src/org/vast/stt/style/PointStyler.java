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

import org.vast.data.AbstractDataBlock;
import org.vast.ows.sld.GraphicImage;
import org.vast.ows.sld.GraphicMark;
import org.vast.ows.sld.GraphicSource;
import org.vast.ows.sld.PointSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.style.PointGraphic.ShapeType;
import org.vast.util.MessageSystem;


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
public class PointStyler extends AbstractStyler implements DataStyler1D
{
    protected PointGraphic point;
    protected PointSymbolizer symbolizer;	
    protected int[] pointIndex = new int[1];
    protected boolean useIcons;
    
	
	public PointStyler()
	{
		point = new PointGraphic();
	}
    
    
    public PointGraphic nextPoint()
    {
        point.x = point.y = point.z = 0.0;
        
        if (nextItem())
        {
            // adjust geometry to fit projection
            if (projection != null)
                projection.adjust(geometryCrs, point);
            
            return point;
        }
        
        return null;
    }
    
    
    public int getNumPoints()
    {
        if (dataLists[0].indexOffset == 0)
            return dataLists[0].blockIterator.getList().getSize();
        else
            return 0;
    }
    
    
    public PointGraphic getPoint(int u)
    {
        point.x = point.y = point.z = 0.0;
        
        if (dataLists[0].indexOffset == 0)
        {
            AbstractDataBlock dataBlock = dataLists[0].blockIterator.getList().get(u);
            dataLists[0].blockIndexer.setData(dataBlock);
            dataLists[0].blockIndexer.reset();
            dataLists[0].blockIndexer.next();
        }
        else
        {
            pointIndex[0] = u;
            dataLists[0].blockIndexer.getData(pointIndex);
        }
        
        // adjust geometry to fit projection
        if (projection != null)
            projection.adjust(geometryCrs, point);
        
        return point;
    }
    
    
    @Override
    public void computeBoundingBox()
    {
        this.resetIterators();
        PrimitiveGraphic point;
        
        while (nextBlock() != null)
            while ((point = nextPoint()) != null)
                addToExtent(point);
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
            
            // shape
            param = ((GraphicMark)glyph).getShape();
            if (param != null)
            {
                if (param.isConstant())
                {
                    String shapeName = (String)param.getValue();
                    if (shapeName.equalsIgnoreCase("square"))
                        point.shape = ShapeType.SQUARE;
                    else if (shapeName.equalsIgnoreCase("circle"))
                        point.shape = ShapeType.CIRCLE;
                    else if (shapeName.equalsIgnoreCase("triangle"))
                        point.shape = ShapeType.TRIANGLE;
                    else if (shapeName.equalsIgnoreCase("star"))
                        point.shape = ShapeType.STAR;
                    else
                        MessageSystem.display("Unknown shape: " + shapeName, true);
                }
                else
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        //addPropertyMapper(propertyName, new GenericShapeMapper(point, param.getMappingFunction()));              
                    }
                }
            }
        }
        
        else if (glyph instanceof GraphicImage)
        {
            param = ((GraphicImage)glyph).getUrl();
            if (param != null)
            {
                if (param.isConstant())
                {
                    value = param.getConstantValue();
                    point.iconUrl = ((GraphicImage)glyph).getBaseFolder() + (String)value;
                }
                else
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        addPropertyMapper(propertyName, new IconUrlMapper(point, param.getMappingFunction())); 
                    }
                }
                
                useIcons = true;
            }
        }
        
        // point size
        param = this.symbolizer.getGraphic().getSize();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.size = ((Float)value).floatValue();
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
        
        dataLists[0].indexOffset = 0;
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


    public boolean useIcons()
    {
        return useIcons;
    }
}