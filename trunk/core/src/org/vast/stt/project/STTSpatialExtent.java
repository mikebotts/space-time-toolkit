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

package org.vast.stt.project;

import org.vast.physics.SpatialExtent;
import org.vast.stt.dynamics.SpatialExtentUpdater;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;

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
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class STTSpatialExtent extends SpatialExtent implements STTEventProducer
{
    protected boolean tilingEnabled;
    protected int xTiles = 1;
    protected int yTiles = 1;
    protected int zTiles = 1;
    protected STTEventListeners listeners;
    protected SpatialExtentUpdater updater;


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


    public int getXTiles()
    {
        return xTiles;
    }


    public void setXTiles(int tiles)
    {
        xTiles = tiles;
    }


    public int getYTiles()
    {
        return yTiles;
    }


    public void setYTiles(int tiles)
    {
        yTiles = tiles;
    }


    public int getZTiles()
    {
        return zTiles;
    }


    public void setZTiles(int tiles)
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


    public void dispatchEvent(STTEvent event)
    {
        event.producer = this;
        listeners.dispatchEvent(event);
    }
}
