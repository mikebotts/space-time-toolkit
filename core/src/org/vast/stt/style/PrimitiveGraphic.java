/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.style;


/**
 * <p><b>Title:</b><br/>
 * Primitive Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO PrimitiveGraphic type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Mar 31, 2006
 * @version 1.0
 */
public abstract class PrimitiveGraphic extends GraphicObject
{
    public double x, y, z, t;
    public float r, g, b;
    public float a = 1.0f;
}
