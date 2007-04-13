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
 * Vector Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO VectorGraphic Class Description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 10, 2007
 * @version 1.0
 */
public class VectorGraphic extends GraphicObject
{
    public enum ShapeType
    {
        ARROW_SIMPLE
    }
    
    public LinePointGraphic point1 = new LinePointGraphic();
    public LinePointGraphic point2 = new LinePointGraphic();
    
    public ShapeType shape = ShapeType.ARROW_SIMPLE;
    public String iconUrl;
    public int iconOffsetX = 0;
    public int iconOffsetY = 0;
}
