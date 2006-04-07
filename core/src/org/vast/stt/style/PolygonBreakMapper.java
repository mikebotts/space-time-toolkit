package org.vast.stt.style;

import org.ogc.cdm.common.DataBlock;


/**
 * <p><b>Title:</b><br/>
 * Polygon Break Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode to use as polygon break.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class PolygonBreakMapper extends PropertyMapper
{
    PolygonPointGraphic point;
    
    
    public PolygonBreakMapper(PolygonPointGraphic point)
    {
        this.point = point;
    }


    public void mapData(DataBlock data)
    {
        point.polyBreak = data.getBooleanValue();  
    }    
}
