/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

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
 * <p>Copyright (c) 2007</p>
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
