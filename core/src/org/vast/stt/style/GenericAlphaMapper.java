package org.vast.stt.style;

import org.ogc.cdm.common.DataBlock;
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
public class GenericAlphaMapper extends PropertyMapper
{
    PrimitiveGraphic graphic;
    
    
    public GenericAlphaMapper(PrimitiveGraphic graphic, MappingFunction mappingFunction)
    {
        this.graphic = graphic;
        this.mappingFunction = mappingFunction;
        if (mappingFunction != null)
            this.useMappingFunction = true;
    }

    
    public void mapData(DataBlock data)
    {
        graphic.a = data.getFloatValue();
        
        if (useMappingFunction)
            graphic.a = (float)mappingFunction.compute(graphic.a);
    }    
}
