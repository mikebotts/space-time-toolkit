package org.vast.stt.style;

import org.ogc.cdm.common.DataBlock;


/**
 * <p><b>Title:</b><br/>
 * Line Break Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode to use as line break.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class LineBreakMapper extends PropertyMapper
{
    LinePointGraphic linePoint;
    
    
    public LineBreakMapper(LinePointGraphic linePoint)
    {
        this.linePoint = linePoint;
    }


    public void mapData(DataBlock data)
    {
        linePoint.lineBreak = data.getBooleanValue();  
    }    
}
