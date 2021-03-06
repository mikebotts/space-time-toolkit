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

import org.vast.math.Vector3d;


/**
 * <p><b>Title:</b><br/>
 * Primitive Graphic
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO PrimitiveGraphic type description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Mar 31, 2006
 * @version 1.0
 */
public abstract class PrimitiveGraphic extends TimeTaggedGraphic
{
    public double x, y, z;
    public float r, g, b;
    public float a = 1.0f;
    
    
    protected PrimitiveGraphic copy(PrimitiveGraphic p)
    {
        super.copy(p);
        
        p.x = this.x;
        p.y = this.y;
        p.z = this.z;
        p.r = this.r;
        p.g = this.g;
        p.b = this.b;
        p.a = this.a;
        
        return p;
    }
    
    
    public Vector3d toVector3d(Vector3d vector)
    {
    	if (vector == null)
    		vector = new Vector3d();
    	
    	vector.x = this.x;
    	vector.y = this.y;
    	vector.z = this.z;
    	
    	return vector;
    }
    
    
    public void fromVector3d(Vector3d vector)
    {
    	this.x = vector.x;
    	this.y = vector.y;
    	this.z = vector.z;
    }
}
