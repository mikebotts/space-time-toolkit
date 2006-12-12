package org.vast.stt.style;

import org.vast.cdm.common.DataBlock;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Alpha Color Component Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode to the Alpha Component value.
 * It will use a mapping function if provided.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class GenericGrayMapper extends PropertyMapper
{
    PrimitiveGraphic graphic;
    
    
    public GenericGrayMapper(PrimitiveGraphic graphic, MappingFunction mappingFunction)
    {
        this.graphic = graphic;
        this.mappingFunction = mappingFunction;
        if (mappingFunction != null)
            this.useMappingFunction = true;
    }

    
    public void mapData(DataBlock data)
    {
        if (useMappingFunction)
        {
            double val = data.getDoubleValue();
            float grayVal = (float)mappingFunction.compute(val);
            graphic.r = grayVal;
            graphic.g = grayVal;
            graphic.b = grayVal;
        }
        else
        {
            float val = data.getFloatValue();
            graphic.r = val;
            graphic.g = val;
            graphic.b = val;
        }
    }    
}
