package org.vast.stt.style;

import org.ogc.cdm.common.DataBlock;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Blue Color Component Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode to the Blue Component value.
 * It will use a mapping function if provided.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class GenericBlueMapper extends PropertyMapper
{
    PrimitiveGraphic graphic;
    
    
    public GenericBlueMapper(PrimitiveGraphic graphic, MappingFunction mappingFunction)
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
            graphic.b = (float)mappingFunction.compute(val);
        }
        else
            graphic.b = data.getFloatValue();     
    }    
}
