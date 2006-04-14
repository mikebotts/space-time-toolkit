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

package org.vast.stt.renderer.opengl;

import org.vast.stt.renderer.RendererInfo;


/**
 * <p><b>Title:</b><br/>
 * OpenGLInfo
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Class for storing OpenGL specific options.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class OpenGLInfo implements RendererInfo
{
    public int programNumber = -1;   // OpenGL shader program number (-1 if none)
    public int objectID = -1;        // OpenGL name for texture, vertex buffer or display list (-1 if none)
    public int indexBufferID = -1;   // OpenGL name for additional Index Buffer Object (-1 if none)
    
    
    public OpenGLInfo()
    {        
    }
}
