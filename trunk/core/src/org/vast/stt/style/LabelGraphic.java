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
 * Label Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO LabelGraphic Class Description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class LabelGraphic extends PrimitiveGraphic
{
    public float orientation = 0;
    public int size = 10;
    public int anchorX = 0;
    public int anchorY = 0;
    public int offsetX = 0;
    public int offsetY = 10;
    public String text = "Text";
}
