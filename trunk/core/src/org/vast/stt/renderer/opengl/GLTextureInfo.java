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


/**
 * <p><b>Title:</b><br/>
 * GLTexture Info
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Class for storing GL Texture specific options.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class GLTextureInfo extends OpenGLInfo
{
    public int textureID = -1;
    public int widthPadding = 0;
    public int heightPadding = 0;
    
    
    public GLTextureInfo()
    {        
    }
}
