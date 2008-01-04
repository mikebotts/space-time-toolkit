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

package org.vast.stt.provider;

import java.util.ArrayList;
import org.vast.math.Vector3d;
import org.vast.stt.event.STTEventProducer;

/**
 * <p><b>Title:</b><br/>
 * STT Polygon Extent
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Class for storing the definition of a polygon domain.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class STTPolygonExtent extends STTSpatialExtent implements STTEventProducer
{
    protected ArrayList<Vector3d> pointList;
    

    public STTPolygonExtent()
    {
        super();
        pointList = new ArrayList<Vector3d>();
    }
    
    
    /**
     * Returns an exact copy of this STTSpatialExtent
     * Copies everu field except the listeners and the updater 
     * @return
     */
    public STTPolygonExtent copy()
    {
    	STTPolygonExtent poly = new STTPolygonExtent();
        
    	poly.crs = this.crs;
    	poly.minX = this.minX;
    	poly.minY = this.minY;
    	poly.minZ = this.minZ;
    	poly.maxX = this.maxX;
    	poly.maxY = this.maxY;
    	poly.maxZ = this.maxZ;
    	poly.tilingEnabled = this.tilingEnabled;
    	poly.xTiles = this.xTiles;
    	poly.yTiles = this.yTiles;
    	poly.zTiles = this.zTiles;
        
        return poly;
    }
    
    
    public void addPoint(Vector3d point)
    {
    	pointList.add(point);
    	this.resizeToContain(point.x, point.y, point.z);
    }


	public ArrayList<Vector3d> getPointList()
	{
		return pointList;
	}
    
}
