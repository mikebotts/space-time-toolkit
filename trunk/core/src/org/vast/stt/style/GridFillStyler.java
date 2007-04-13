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

import org.vast.ows.sld.GridFillSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


/**
 * <p><b>Title:</b><br/>
 * Grid Fill Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to render a filled grid.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class GridFillStyler extends AbstractGridStyler
{
    
    public GridFillStyler()
    {
    }
    
    
    public GridFillSymbolizer getSymbolizer()
    {
        return (GridFillSymbolizer)symbolizer;
    }


    public void setSymbolizer(Symbolizer sym)
    {
        this.symbolizer = (GridFillSymbolizer)sym;
        this.setCrs(sym.getGeometry().getCrs());
    }

    
    @Override
    public void updateDataMappings()
    {
        super.updateDataMappings();
        GridFillSymbolizer sym = (GridFillSymbolizer)this.symbolizer;
        ScalarParameter param;
        Object value;
        String propertyName = null;
        
        // point red
        param = sym.getFill().getColor().getRed();
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
        param = sym.getFill().getColor().getGreen();
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
        param = sym.getFill().getColor().getBlue();
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
        param = sym.getFill().getColor().getAlpha();
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
