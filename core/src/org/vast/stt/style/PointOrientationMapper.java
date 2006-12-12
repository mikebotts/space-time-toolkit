package org.vast.stt.style;

import org.vast.cdm.common.DataBlock;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Point Orientation Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode and use it as point orientation.
 * It will use a mapping function if provided.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class PointOrientationMapper extends PropertyMapper
{
    PointGraphic point;
    
    
    public PointOrientationMapper(PointGraphic point, MappingFunction mappingFunction)
    {
        this.point = point;
        this.mappingFunction = mappingFunction;
        if (mappingFunction != null)
            this.useMappingFunction = true;
    }

    
    public void mapData(DataBlock data)
    {
        if (useMappingFunction)
        {
            double val = data.getDoubleValue();
            point.orientation = (float)mappingFunction.compute(val);
        }
        else
            point.orientation = data.getFloatValue();
    }    
}
