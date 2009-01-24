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


/**
 * <p><b>Title:</b><br/>
 * Point Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO PointGraphic Class Description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class PointGraphic extends PrimitiveGraphic
{
    public enum ShapeType
    {
        SQUARE, CIRCLE, TRIANGLE, STAR
    }
    
    public float orientation;
	public float size = 1.0f;	
    public boolean smooth = false;
    public ShapeType shape = ShapeType.SQUARE;
    public int iconId = -1;
    public int iconOffsetX = 0;
    public int iconOffsetY = 0;
    
    
    public PointGraphic copy(PointGraphic p)
    {
        if (p == null)
            p = new PointGraphic();
        
        super.copy(p);
        
        p.orientation = this.orientation;
        p.size = this.size;
        p.smooth = this.smooth;
        p.shape = this.shape;
        p.iconId = this.iconId;
        p.iconOffsetX = this.iconOffsetX;
        p.iconOffsetY = this.iconOffsetY;
        
        return p;
    }
}
