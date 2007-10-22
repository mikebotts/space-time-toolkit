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

import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.TextSymbolizer;


/**
 * <p><b>Title:</b><br/>
 * Label Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Converts source data to a sequence of LabelGraphic objects
 * that the renderer can access and render sequentially. 
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class LabelStyler extends AbstractStyler
{
    protected LabelGraphic label;
    protected TextSymbolizer symbolizer;
    protected int labelDensity = 10;
    protected int labelSpacing;
    	
	
	public LabelStyler()
	{
        label = new LabelGraphic();
	}
    
    
    public LabelGraphic nextPoint()
    {
        label.x = label.y = label.z = 0.0;
        
        if (nextItem())
        {
            // adjust geometry to fit projection
            if (projection != null)
                projection.adjust(geometryCrs, label);
            
            return label;
        }
        
        return null;
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
        label = new LabelGraphic();
        this.clearAllMappers();
               
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.x = (Double)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericXMapper(label, param.getMappingFunction()));
                }
            }
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.y = (Double)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericYMapper(label, param.getMappingFunction()));
                }
            }
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.z = (Double)value;
            }
            else
            {
                propertyName = param.getPropertyName();          
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericZMapper(label, param.getMappingFunction()));
                }
            }
        }
        
        // color - red 
        param = symbolizer.getFill().getColor().getRed();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.r = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericRedMapper(label, param.getMappingFunction()));              
                }
            }
        }
        
        // color - green 
        param = symbolizer.getFill().getColor().getGreen();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.g = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGreenMapper(label, param.getMappingFunction()));               
                }
            }
        }
        
        // color - blue 
        param = symbolizer.getFill().getColor().getBlue();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.b = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericBlueMapper(label, param.getMappingFunction()));             
                }
            }
        }
        
        // color - alpha 
        param = symbolizer.getFill().getColor().getAlpha();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.a = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericAlphaMapper(label, param.getMappingFunction()));              
                }
            }
        }
        
        // label text
        param = this.symbolizer.getLabel();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.text = (String)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new LabelTextMapper(label, param.getMappingFunction()));              
                }
            }
        }
        
        // text size
        param = this.symbolizer.getFont().getSize();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.size = ((Float)value).intValue();
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new LabelSizeMapper(label, param.getMappingFunction()));              
                }
            }
        }
        
        // label orientation
        param = null;//this.symbolizer.getPlacement().getRotation();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.orientation = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new LabelOrientationMapper(label, param.getMappingFunction()));              
                }
            }
        }
	}
	
	
	public TextSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (TextSymbolizer)sym;
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
}