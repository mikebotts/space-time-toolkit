package org.vast.stt.style;

import org.vast.stt.data.BlockListItem;
import org.vast.stt.renderer.RendererInfo;


/**
 * <p><b>Title:</b><br/>
 * Graphic Object
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract Base class for all Graphic Object produced by DataStylers.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class GraphicObject
{
    public RendererInfo rendererInfo;
    public BlockListItem block;
    public boolean discard;
}
