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

import org.ogc.cdm.common.DataType;


/**
 * <p><b>Title:</b><br/>
 * Image Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Graphic Object used to transfer a whole image to the
 * renderer. Usually used for efficiency (i.e. cached
 * in video memory by OpenGL). It is less flexible than
 * getting pixels one at a time though.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class RasterImageGraphic extends GraphicObject
{
	enum ImageType {}; // use same as openGL !
    
    public int width;
    public int height;
    public int depth;
	public Object data;
    public DataType dataType;
    public ImageType imageType;
    public boolean updated;
    //public ColorMap colorMap;
}
