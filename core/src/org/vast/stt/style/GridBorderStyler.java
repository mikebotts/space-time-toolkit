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

import org.vast.ows.sld.GridBorderSymbolizer;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;


/**
 * <p><b>Title:</b><br/>
 * Grid Border Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Used to render a grid border.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class GridBorderStyler extends AbstractGridStyler
{
    
    public GridBorderStyler()
    {
        super();
    }
    
    
    public GridBorderSymbolizer getSymbolizer()
    {
        return (GridBorderSymbolizer)symbolizer;
    }


    public void setSymbolizer(Symbolizer sym)
    {
        this.symbolizer = (GridBorderSymbolizer)sym;
        this.setCrs(sym.getGeometry().getCrs());
    }

    
    @Override
    public void updateDataMappings()
    {
        super.updateDataMappings();
        GridBorderSymbolizer sym = (GridBorderSymbolizer)this.symbolizer;
        ScalarParameter param;
        Object value;
        
        // point red
        param = sym.getStroke().getColor().getRed();
        updateMappingRed(point, param);
        
        // point green
        param = sym.getStroke().getColor().getGreen();
        updateMappingGreen(point, param);
        
        // point blue
        param = sym.getStroke().getColor().getBlue();
        updateMappingBlue(point, param);
        
        // point alpha
        param = sym.getStroke().getColor().getAlpha();
        updateMappingAlpha(point, param);
        
        // line width
        param = sym.getStroke().getWidth();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                patch.lineWidth = (Float)value;
            }
        }
        
        mappingsUpdated = true;
    }
    
    
    public void accept(StylerVisitor visitor)
    {
        dataNode = dataItem.getDataProvider().getDataNode();

        if (dataNode.isNodeStructureReady())
        {
            if (!mappingsUpdated)
                updateDataMappings();

            visitor.visit(this);
        }       
    }
}
