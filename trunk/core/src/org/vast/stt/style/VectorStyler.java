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

import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.VectorSymbolizer;


/**
 * <p><b>Title:</b><br/>
 * Vector Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Converts source data to a sequence of VectorGraphic objects
 * that the renderer can access and render sequentially. 
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 12, 2007
 * @version 1.0
 */
public class VectorStyler extends AbstractStyler
{
    protected VectorGraphic vector;
    protected LinePointGraphic point;
    protected VectorSymbolizer symbolizer;	
    protected int[] pointIndex = new int[1];
    protected boolean useIcons;
    protected double vectorLength = 1.0;
    protected double steps = 10;
    protected double currentStep = 0;
	
    
	public VectorStyler()
	{
        vector = new VectorGraphic();
        point = new LinePointGraphic();
	}
    
    
    @Override
    public void resetIterators()
    {
        super.resetIterators();
        currentStep = 0;
    }
    
    
    public LinePointGraphic nextPoint()
    {
        point.x = point.y = point.z = 0.0;
        
        if (currentStep == 0)
        {
            // add break at beginning of vector
            point.graphBreak = true;
            for (int i=0; i<dataLists.length; i++)
            {
                if (dataLists[i].blockIndexer.hasNext())
                    dataLists[i].blockIndexer.next();
                else
                    return null;
            }
        }
        
        LinePointGraphic p1 = vector.point1;
        LinePointGraphic p2 = vector.point2;
        
        double ratio = currentStep / (steps - 1.0);
        point.x = p1.x + (p2.x - p1.x)*ratio;
        point.y = p1.y + (p2.y - p1.y)*ratio;
        point.z = p1.z + (p2.z - p1.z)*ratio;
        
        // adjust geometry to fit projection
        if (projection != null)
            projection.adjust(geometryCrs, point);
        
        // increment/reset currentStep
        currentStep++;        
        if (currentStep >= steps)
            currentStep = 0;
        
        return point;
    }
        
    
    protected void transformPoint2()
    {
        LinePointGraphic p1 = vector.point1;
        LinePointGraphic p2 = vector.point2;
        
        switch (this.symbolizer.getDirectionType())
        {
            case ABS:
                break;
            
            case DIFF:
                p2.x = p1.x + p2.x * vectorLength;
                p2.y = p1.y + p2.y * vectorLength;
                p2.z = p1.z + p2.z * vectorLength;
                break;
            
            case ROT:
                // TODO handle ROT case in VectorStyler
                break;
        }
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
        vector = new VectorGraphic();
        this.clearAllMappers();
               
        // geometry X1
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                vector.point1.x = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericXMapper(vector.point1, param.getMappingFunction()));                
                }
            }
        }
        
        //geometry Y1
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                vector.point1.y = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();            
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericYMapper(vector.point1, param.getMappingFunction()));                
                }
            }
        }
        
        // geometry Z1
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                vector.point1.z = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();            
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericZMapper(vector.point1, param.getMappingFunction()));                
                }
            }
        }
        
        // geometry X2
        param = this.symbolizer.getDirection().getX();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                vector.point2.x = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericXMapper(vector.point2, param.getMappingFunction()));                
                }
            }
        }
        
        // geometry Y2
        param = this.symbolizer.getDirection().getY();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                vector.point2.y = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericYMapper(vector.point2, param.getMappingFunction()));                
                }
            }
        }
        
        // geometry Z2
        param = this.symbolizer.getDirection().getZ();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                vector.point2.z = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericZMapper(vector.point2, param.getMappingFunction()));                
                }
            }
        }
        
        // geometry length
        param = this.symbolizer.getLength();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                vectorLength = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    //addPropertyMapper(propertyName, new GenericZMapper(vector.point2, param.getMappingFunction()));                
                }
            }
        }
        
        // stroke color - red 
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericRedMapper(point, param.getMappingFunction()));              
                }
            }
        }
        
        // stroke color - green 
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGreenMapper(point, param.getMappingFunction()));               
                }
            }
        }
        
        // stroke color - blue 
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericBlueMapper(point, param.getMappingFunction()));             
                }
            }
        }
        
        // stroke color - alpha 
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericAlphaMapper(point, param.getMappingFunction()));              
                }
            }
        }
        
        // stroke width 
        param = this.symbolizer.getStroke().getWidth();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.width = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new LineWidthMapper(point, param.getMappingFunction()));              
                }
            }
        }
        
        dataLists[0].indexOffset = 0;
	}
	
	
	public VectorSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (VectorSymbolizer)sym;
        this.setCrs(sym.getGeometry().getCrs());
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