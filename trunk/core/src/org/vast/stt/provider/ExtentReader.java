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

package org.vast.stt.provider;

import java.text.ParseException;

import org.vast.io.xml.DOMReader;
import org.vast.stt.dynamics.SceneTimeUpdater;
import org.vast.stt.dynamics.TimeExtentUpdater;
import org.vast.stt.project.XMLReader;
import org.vast.stt.project.world.WorldScene;
import org.vast.util.DateTimeFormat;
import org.w3c.dom.*;


/**
 * <p><b>Title:</b>
 * Extent Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Helper reader for reading TimeExtent and SpatialExtent
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Sep 12, 2006
 * @version 1.0
 */
public class ExtentReader extends XMLReader
{
    
    public ExtentReader()
    {
    }
    
    
    /**
     * Reads the provider spatial extent data
     * @param provider
     * @param dom
     * @param spElt
     */
    public void readSpatialExtent(DataProvider provider, DOMReader dom, Element spElt)
    {
        if (spElt != null)
         {
            Element bboxElt = dom.getElement(spElt, "BoundingBox");
            
            // try to get the bbox from the list
            Object obj = findExistingObject(dom, bboxElt);
            if (obj != null)
            {
                provider.setSpatialExtent((STTSpatialExtent)obj);
                return;
            }
            
            // otherwise change the provider one
            STTSpatialExtent spatialExtent = provider.getSpatialExtent();
             
            // read bbox
            String coordText = dom.getElementValue(bboxElt, "coordinates");
            String [] coords = coordText.split(" |,");
             
            double minX = Double.parseDouble(coords[0]);
            double minY = Double.parseDouble(coords[1]);
            double maxX = Double.parseDouble(coords[2]);
            double maxY = Double.parseDouble(coords[3]);
             
            spatialExtent.setMinX(minX);
            spatialExtent.setMinY(minY);
            spatialExtent.setMaxX(maxX);
            spatialExtent.setMaxY(maxY);
             
            // read tiling info
            String tileDims = dom.getAttributeValue(spElt, "tiling");
            if (tileDims != null)
            {
                String[] dims = tileDims.split("x");
                
                int tileX = Integer.parseInt(dims[0]);
                int tileY = Integer.parseInt(dims[1]);
                 
                spatialExtent.setXTiles(tileX);
                spatialExtent.setYTiles(tileY);
                spatialExtent.setTilingEnabled(true);
            }
             
            // store that in the hashtable
            registerObjectID(dom, bboxElt, spatialExtent);
         }
    }
    
    
    /**
     * Reads the provider time extent data
     * @param provider
     * @param dom
     * @param spElt
     */
    public void readTimeExtent(DataProvider provider, DOMReader dom, Element timeElt)
    {
        if (timeElt != null)
        {
            STTTimeExtent timeExtent = provider.getTimeExtent();
            String val = null;
            
            try
            {
                Element extentElt = dom.getElement(timeElt, "TimeExtent");
                
                if (extentElt != null)
                {
                    // read base time
                    val = dom.getElementValue(extentElt, "baseTime");
                    if (val.equalsIgnoreCase("now"))
                        timeExtent.setBaseAtNow(true);
                    else
                        timeExtent.setBaseTime(DateTimeFormat.parseIso(val));
                    
                    // read lag time
                    val = dom.getElementValue(extentElt, "lagTime");
                    timeExtent.setLagTimeDelta(Double.parseDouble(val));
                    
                    // read lead time
                    val = dom.getElementValue(extentElt, "leadTime");
                    timeExtent.setLeadTimeDelta(Double.parseDouble(val));
                    
                    // read step time
                    val = dom.getElementValue(extentElt, "stepTime");
                    timeExtent.setTimeStep(Double.parseDouble(val));
                }
                // read autoUpdate element
                if (dom.existElement(timeElt, "autoUpdate"))
                {
                    String sceneId = dom.getAttributeValue(timeElt, "autoUpdate/@href").substring(1);
                    WorldScene scene = (WorldScene)objectIds.get(sceneId);
                    TimeExtentUpdater updater = new SceneTimeUpdater(scene);
                    timeExtent.setUpdater(updater);
                    updater.setEnabled(true);
                }
            }
            catch (ParseException e)
            {
                throw new IllegalStateException("Invalid time: " + val);
            }
        }
    }
}
