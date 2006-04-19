package org.vast.stt.style;

import org.ogc.cdm.common.DataBlock;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Grid Width Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This transfers a value from the DataNode and use it as grid width.
 * It will use a mapping function if provided.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 3, 2006
 * @version 1.0
 */
public class RasterHeightMapper extends PropertyMapper
{
    RasterTileGraphic raster;
    
    
    public RasterHeightMapper(RasterTileGraphic raster, MappingFunction mappingFunction)
    {
        this.raster = raster;
        this.mappingFunction = mappingFunction;
        if (mappingFunction != null)
            this.useMappingFunction = true;
    }

    
    public void mapData(DataBlock data)
    {
        if (useMappingFunction)
        {
            double val = data.getDoubleValue();
            raster.height = (int)mappingFunction.compute(val);
        }
        else
            raster.height = data.getIntValue();
    }    
}
