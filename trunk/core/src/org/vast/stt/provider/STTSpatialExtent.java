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

import org.vast.stt.dynamics.SpatialExtentUpdater;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;
import org.vast.util.SpatialExtent;

/**
 * <p><b>Title:</b><br/>
 * STT Spatial Extent
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Class for storing the definition of a spatial domain.
 * This mainly includes enveloppe coordinates and a crs.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class STTSpatialExtent extends SpatialExtent implements STTEventProducer
{
    protected STTEventListeners listeners;
    protected SpatialExtentUpdater updater;
    protected float xTiles = 1;
    protected float yTiles = 1;
    protected float zTiles = 1;
    protected boolean tilingEnabled;


    public STTSpatialExtent()
    {
        listeners = new STTEventListeners(1);    
    }
    
    
    /**
     * Returns an exact copy of this STTSpatialExtent
     * Copies everu field except the listeners and the updater 
     * @return
     */
    public STTSpatialExtent copy()
    {
        STTSpatialExtent bbox = new STTSpatialExtent();
        
        bbox.crs = this.crs;
        bbox.minX = this.minX;
        bbox.minY = this.minY;
        bbox.minZ = this.minZ;
        bbox.maxX = this.maxX;
        bbox.maxY = this.maxY;
        bbox.maxZ = this.maxZ;
        bbox.tilingEnabled = this.tilingEnabled;
        bbox.xTiles = this.xTiles;
        bbox.yTiles = this.yTiles;
        bbox.zTiles = this.zTiles;
        
        return bbox;
    }

    
    public boolean isTilingEnabled()
    {
        return tilingEnabled;
    }


    public void setTilingEnabled(boolean tilingEnabled)
    {
        this.tilingEnabled = tilingEnabled;
    }


    public float getXTiles()
    {
        return xTiles;
    }


    public void setXTiles(float tiles)
    {
        xTiles = tiles;
    }


    public float getYTiles()
    {
        return yTiles;
    }


    public void setYTiles(float tiles)
    {
        yTiles = tiles;
    }


    public float getZTiles()
    {
        return zTiles;
    }


    public void setZTiles(float tiles)
    {
        zTiles = tiles;
    }
    
    
    public SpatialExtentUpdater getUpdater()
    {
        return updater;
    }


    public void setUpdater(SpatialExtentUpdater updater)
    {
        this.updater = updater;
        updater.setSpatialExtent(this);
    }
    
    
    public void addListener(STTEventListener listener)
    {
        listeners.add(listener);
    }


    public void removeListener(STTEventListener listener)
    {
        listeners.remove(listener);
    }


    public void removeAllListeners()
    {
        listeners.clear();
    }
    
    
    public boolean hasListeners()
    {
        return !listeners.isEmpty();
    }


    public void dispatchEvent(STTEvent event, boolean merge)
    {
        event.producer = this;
        listeners.dispatchEvent(event, merge);
    }
}
