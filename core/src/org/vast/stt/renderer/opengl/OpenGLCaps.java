/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.renderer.opengl;

import javax.media.opengl.GL;
import org.vast.stt.style.RasterTileGraphic.BufferType;


/**
 * <p><b>Title:</b><br/>
 * OpenGL Capabilities
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Store OpenGL Capabilities and Extensions supported
 * by the local OpenGL implementation.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class OpenGLCaps
{
    public static int TEXTURE_2D_TARGET = GL.GL_TEXTURE_2D;//0x84F5;//;
    
    
    public static int getGLBufferType(BufferType bufferType)
    {
        switch (bufferType)
        {
            case LUM:
                return GL.GL_LUMINANCE;
                
            case LUMA:
                return GL.GL_LUMINANCE_ALPHA;
            
            case RGB:
                return GL.GL_RGB;
                
            case RGBA:    
                return GL.GL_RGBA;
                
            case BGR:
                return GL.GL_BGR;
                
            case BGRA:    
                return GL.GL_BGRA;
                
            case R:
                return GL.GL_RED;
                
            case G:
                return GL.GL_GREEN;
                
            case B:
                return GL.GL_BLUE;
                
            default:
                return GL.GL_RGB;
        }
    }
}
