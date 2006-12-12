package org.vast.stt.style;

import org.vast.cdm.common.DataBlock;
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
public class GridLengthMapper extends DimensionMapper
{
    GridPatchGraphic patch;
    
    
    public GridLengthMapper(int dimensionIndex, GridPatchGraphic patch, MappingFunction mappingFunction)
    {
        this.dimensionIndex = dimensionIndex;
        this.patch = patch;
        this.mappingFunction = mappingFunction;
        if (mappingFunction != null)
            this.useMappingFunction = true;
    }

    
    public void mapData(DataBlock data)
    {
        if (useMappingFunction)
        {
            double val = data.getDoubleValue();
            patch.length = (int)mappingFunction.compute(val);
        }
        else
            patch.length = data.getIntValue();
    }
    
    
    public void setDimensionSize(int size)
    {
        patch.length = size;
    }
}
