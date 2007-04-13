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
    protected VectorSymbolizer symbolizer;	
    protected int[] pointIndex = new int[1];
    protected boolean useIcons;
    protected double vectorLength = 1.0;
    
	
	public VectorStyler()
	{
		vector = new VectorGraphic();
	}
    
    
    public VectorGraphic nextVector()
    {
        vector.point1.x = vector.point1.y = vector.point1.z = 0.0;
        vector.point2.x = vector.point2.y = vector.point2.z = 0.0;
        
        if (nextItem())
        {
            // compute point2 absolute pos
            transformPoint2();
            
            // adjust geometry to fit projection
            if (projection != null)
            {
                projection.adjust(geometryCrs, vector.point1);
                projection.adjust(geometryCrs, vector.point2);
            }
            
            return vector;
        }
        
        return null;
    }
    
    
    public int getNumVectors()
    {
        if (dataLists[0].indexOffset == 0)
            return dataLists[0].blockIterator.getList().getSize();
        else
            return 0;
    }
    
    
    public VectorGraphic getVector(int u)
    {
        vector.point1.x = vector.point1.y = vector.point1.z = 0.0;
        vector.point2.x = vector.point2.y = vector.point2.z = 0.0;
        
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
        
        // compute point2 absolute pos
        transformPoint2();
        
        // adjust geometry to fit projection
        if (projection != null)
        {
            projection.adjust(geometryCrs, vector.point1);
            projection.adjust(geometryCrs, vector.point2);
        }
        
        return vector;
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
        VectorGraphic vector;
        
        while (nextBlock() != null)
            while ((vector = nextVector()) != null)
            {
                addToExtent(vector.point1);
                addToExtent(vector.point2);
            }
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
                vector.point1.r = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericRedMapper(vector.point1, param.getMappingFunction()));              
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
                vector.point1.g = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGreenMapper(vector.point1, param.getMappingFunction()));               
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
                vector.point1.b = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericBlueMapper(vector.point1, param.getMappingFunction()));             
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
                vector.point1.a = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericAlphaMapper(vector.point1, param.getMappingFunction()));              
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
                vector.point1.width = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new LineWidthMapper(vector.point1, param.getMappingFunction()));              
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