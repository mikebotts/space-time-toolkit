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

import org.vast.cdm.common.DataBlock;
import org.vast.data.DataVisitor;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Property Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This abstract property mapper adds the ability to send the value
 * through a mapping function before it is used for display.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Mar 15, 2006
 * @version 1.0
 */
public abstract class PropertyMapper implements DataVisitor
{
    protected MappingFunction mappingFunction = null;
    
    
    protected double getMappingFunctionOutput(DataBlock data)
    {
        if (mappingFunction.hasCategoricalInput())
        {
            String val = data.getStringValue();
            return mappingFunction.compute(val);
        }
        else
        {
            double val = data.getDoubleValue();
            return mappingFunction.compute(val);
        }
    }
    
    
    protected double getMappedValueAsDouble(DataBlock data)
    {
        if (mappingFunction != null)
            return getMappingFunctionOutput(data);
        else
            return data.getDoubleValue();
    }
    
    
    protected float getMappedValueAsFloat(DataBlock data)
    {
        if (mappingFunction != null)
            return (float)getMappingFunctionOutput(data);
        else
            return data.getFloatValue();
    }
    
    
    protected int getMappedValueAsInt(DataBlock data)
    {
        if (mappingFunction != null)
        {
            double val = getMappingFunctionOutput(data);
            return (Double.isNaN(val) ? -1 : (int)val);
        }
        else
            return data.getIntValue();
    }
    
    
    protected boolean getMappedValueAsBoolean(DataBlock data)
    {
        if (mappingFunction != null)
        {
            double val = getMappingFunctionOutput(data);
            return (val == 0.0 || Double.isNaN(val) ? false : true);
        }
        else
            return data.getBooleanValue();
    }
    
    
    protected String getMappedValueAsString(DataBlock data)
    {
        if (mappingFunction != null)
        {
            double val = getMappingFunctionOutput(data);
            return Double.toString(val);
        }
        else
            return data.getStringValue();
    }
}



