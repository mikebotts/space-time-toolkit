package org.vast.stt.style;

import org.vast.cdm.common.DataBlock;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Label Size Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode and use it as label size.
 * It will use a mapping function if provided.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class LabelSizeMapper extends PropertyMapper
{
    LabelGraphic label;
    
    
    public LabelSizeMapper(LabelGraphic label, MappingFunction mappingFunction)
    {
        this.label = label;
        this.mappingFunction = mappingFunction;
        if (mappingFunction != null)
            this.useMappingFunction = true;
    }

    
    public void mapData(DataBlock data)
    {
        if (useMappingFunction)
        {
            double val = data.getDoubleValue();
            label.size = (int)mappingFunction.compute(val);
        }
        else
            label.size = data.getIntValue();
    }    
}
