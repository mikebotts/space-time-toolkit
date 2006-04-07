package org.vast.stt.style;

import org.vast.data.DataVisitor;
import org.vast.ows.sld.MappingFunction;


/**
 * <p><b>Title:</b><br/>
 * Property Mapper
 * </p>
 *
 * <p><b>Description:</b><br/>
 * This abstract property mapper adds the ability to send the value
 * through a mapping function before it is used for display.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Mar 15, 2006
 * @version 1.0
 */
public abstract class PropertyMapper implements DataVisitor
{
    protected MappingFunction mappingFunction = null;
    protected boolean useMappingFunction = false;
}



