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
import org.vast.math.Vector3d;
import org.vast.ows.sld.LineSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.physics.MapProjection;
import org.vast.stt.data.BlockListItem;


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
    protected int oldBlockCount = 0;
        
	
	public LineStyler()
	{
		point = new LinePointGraphic();
        segment = new LineSegmentGraphic();
	}
    
    
    public LineSegmentGraphic nextLineBlock()
    {
        ListInfo listInfo = dataLists[0];
        BlockListItem nextItem;
        
        // if no more items in the list, just return null
        if (!listInfo.blockIterator.hasNext())
            return null;
    
        // otherwise get the next item
        nextItem = listInfo.blockIterator.next();
        
        // setup indexer with new data 
        AbstractDataBlock nextBlock = nextItem.getData();
        listInfo.blockIndexer.setData(nextBlock);
        listInfo.blockIndexer.reset();
        
        // get BlockInfo
        currentBlockInfo = nextItem.getInfo();
        if (currentBlockInfo.getSpatialExtent().isNull())
            computeExtents = true;
        else
            computeExtents = false;
        
        // copy current item in the patch object
        segment.block = nextItem;
        
        return segment;
    }
    
    
    public LinePointGraphic nextPoint()
    {
        if (dataLists[0].blockIndexer.hasNext)
        {
            dataLists[0].blockIndexer.getNext();
            
//            double[] ecf = MapProjection.LLAtoECF(point.y, point.x, point.z, null);
//            point.x = ecf[0];
//            point.y = ecf[1];
//            point.z = ecf[2];
            
            if (computeExtents)
            {
                Vector3d point3d = new Vector3d(point.x, point.y, point.z);
                currentBlockInfo.getSpatialExtent().resizeToContain(point3d);
            }
            
            return point;
        }
        
        return null;
    }   


    @Override
    public void updateDataMappings()
    {
        ScalarParameter param;
        String propertyName = null;
        Object value;
        
        // reset all parameters
        point = new LinePointGraphic();
        this.clearAllMappers();       
        
        // geometry breaks
        param = this.symbolizer.getGeometry().getBreaks();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new LineBreakMapper(point));               
            }
        }
        
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericRedMapper(point, param.getMappingFunction()));
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGreenMapper(point, param.getMappingFunction()));
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericBlueMapper(point, param.getMappingFunction()));
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
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericAlphaMapper(point, param.getMappingFunction()));
                }
            }
        }
        
        // line width
        param = this.symbolizer.getStroke().getWidth();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                point.width = ((Float)value).intValue();
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
        dataNode = dataItem.getDataProvider().getDataNode();

        if (dataNode.isNodeStructureReady())
        {
            if (dataLists.length == 0)
                updateDataMappings();
            
    		visitor.visit(this);
        }
	}
}