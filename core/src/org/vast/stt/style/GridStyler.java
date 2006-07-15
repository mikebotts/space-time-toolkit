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
import org.vast.ows.sld.GridSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.data.BlockListItem;


/**
 * <p><b>Title:</b><br/>
 * Grid Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class GridStyler extends AbstractStyler
{
	protected GridSymbolizer symbolizer;
    protected GridPatchGraphic patch;
    protected GridPointGraphic point;
    
	
	public GridStyler()
	{
        patch = new GridPatchGraphic();
        point = new GridPointGraphic();
	}
    
    
    public GridPatchGraphic nextPatch()
    {
        ListInfo listInfo = dataLists[0]; 
        
        // if no more items in the list, just return null
        if (!listInfo.blockList.hasNext())
            return null;
        
        // otherwise get the next item
        BlockListItem nextItem = listInfo.blockList.next();
        
        // TODO implement block filtering here
        
        // setup indexer with new data 
        AbstractDataBlock nextBlock = nextItem.getData();
        listInfo.blockIndexer.setData(nextBlock);
        listInfo.blockIndexer.reset();
        listInfo.blockIndexer.getData(0,0,0);
        
        // ensure a block info is present
        nextItem.ensureInfo();
        
        // TODO scan and compute block BBOX and Time Range
        
        // copy current item info in the patch object
        patch.info = nextItem.getInfo();
        return patch;
    }
    
    
    public GridPointGraphic getGridPoint(int u, int v, boolean normalize)
    {
        dataLists[0].blockIndexer.getData(v, u, 0);        
        return point;
    }
    
    
    public void updateBoundingBox()
	{
		// TODO Auto-generated method stub
	}


	public void updateDataMappings()
	{
        ScalarParameter param;
        Object value;
        String propertyName = null;
        boolean fill = (this.symbolizer.getFill() != null);
        
        // reset all parameters
        patch = new GridPatchGraphic();
        point = new GridPointGraphic();
        this.clearAllMappers();
        patch.fill = fill;
        
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
        
        // grid width
        param = this.symbolizer.getDimensions().getWidth();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GridWidthMapper(patch, param.getMappingFunction()));
            }
        }
        
        // grid length
        param = this.symbolizer.getDimensions().getLength();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GridLengthMapper(patch, param.getMappingFunction()));
            }
        }
        
        // grid depth
        param = this.symbolizer.getDimensions().getDepth();
        if (param != null)       
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GridDepthMapper(patch, param.getMappingFunction()));
            }
        }
        
        // get colors from fill or stroke as specified
        // point red
        if (fill) param = this.symbolizer.getFill().getColor().getRed();
        else param = this.symbolizer.getStroke().getColor().getRed();
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
        
        // point green
        if (fill) param = this.symbolizer.getFill().getColor().getGreen();
        else param = this.symbolizer.getStroke().getColor().getGreen();
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
        
        // point blue
        if (fill) param = this.symbolizer.getFill().getColor().getBlue();
        else param = this.symbolizer.getStroke().getColor().getBlue();
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
        
        // point alpha
        if (fill) param = this.symbolizer.getFill().getColor().getAlpha();
        else param = this.symbolizer.getStroke().getColor().getAlpha();
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
	
	
	public GridSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (GridSymbolizer)sym;
	}


	public void accept(StylerVisitor visitor)
	{
        dataNode = dataItem.getDataProvider().getDataNode();

        if (dataNode.isNodeStructureReady())
        {
            if (dataLists.length == 0 || dataLists[0].blockList.getSize() == 0)
                updateDataMappings();
                
            visitor.visit(this);
        }		
	}
}
