package org.vast.stt.style;

import org.ogc.cdm.common.DataBlock;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Icon Url Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode and use it as a an icon url.
 * It will use a mapping function if provided.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class IconUrlMapper extends PropertyMapper
{
    PointGraphic point;
    
    
    public IconUrlMapper(PointGraphic point, MappingFunction mappingFunction)
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
            //double val = data.getDoubleValue();
            point.iconUrl = null; //TODO map icon url
        }
        else
            point.iconUrl = data.getStringValue();
    }    
}
