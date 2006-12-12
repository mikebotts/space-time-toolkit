package org.vast.stt.style;

import org.vast.cdm.common.DataBlock;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Z Coordinate Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode to the Z coordinate value.
 * It will use a mapping function if provided.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class GenericZMapper extends PropertyMapper
{
    PrimitiveGraphic graphic;
    
    
    public GenericZMapper(PrimitiveGraphic graphic, MappingFunction mappingFunction)
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
            graphic.z = mappingFunction.compute(val);
        }
        else
            graphic.z = data.getDoubleValue();  
    }    
}
