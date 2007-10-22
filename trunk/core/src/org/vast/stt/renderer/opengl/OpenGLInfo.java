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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class OpenGLInfo implements RendererInfo
{
    public int programNumber = -1;   // OpenGL shader program number (-1 if none)
    public int objectID = -1;        // OpenGL name for vertex buffer or display list (-1 if none)
    public int indexBufferID = -1;   // OpenGL name for additional Index Buffer Object (-1 if none)
    public int widthPadding = 0;
    public int heightPadding = 0;
    
    
    public OpenGLInfo()
    {        
    }
}
