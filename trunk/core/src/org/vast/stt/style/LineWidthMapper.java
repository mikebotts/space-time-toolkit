package org.vast.stt.style;

import org.ogc.cdm.common.DataBlock;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Line Width Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode and use it as line width.
 * It will use a mapping function if provided.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class LineWidthMapper extends PropertyMapper
{
    LinePointGraphic linePoint;
    
    
    public LineWidthMapper(LinePointGraphic linePoint, MappingFunction mappingFunction)
    {
        this.linePoint = linePoint;
        this.mappingFunction = mappingFunction;
        if (mappingFunction != null)
            this.useMappingFunction = true;
    }

    
    public void mapData(DataBlock data)
    {
        if (useMappingFunction)
        {
            double val = data.getDoubleValue();
            linePoint.width = (int)mappingFunction.compute(val);
        }
        else
            linePoint.width = data.getIntValue();
    }    
}
