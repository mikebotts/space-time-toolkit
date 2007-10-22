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

package org.vast.stt.style;

import java.nio.Buffer;


/**
 * <p><b>Title:</b><br/>
 * Raster Tile Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Represents a tile of a raster. A tile is the atomic piece
 * of a composite tiled raster.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class RasterTileGraphic extends GraphicObject
{
	public enum BufferType
    {
        LUM, LUMA, R, G, B, RGB, BGR, RGBA, BGRA
    }    
    
    public int tileNumber;
    
    public int width = 0;
    public int height = 0;
    public int bands = 3;
    public float opacity = 1.0f;
    
    public int xPos = 0;
    public int yPos = 0;
    public int widthPadding = 0;
    public int heightPadding = 0;
    
    // if no transform is applied, image data can be made
    // accessible to the renderer directly using these fields
    public Buffer rasterData;
    public BufferType rasterType;
    public boolean hasRasterData;
}
