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
import org.vast.xml.DOMHelper;
import org.vast.stt.dynamics.RealTimeUpdater;
import org.vast.stt.dynamics.SceneBboxUpdater;
import org.vast.stt.dynamics.SceneTimeUpdater;
import org.vast.stt.dynamics.SpatialExtentUpdater;
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
    public STTSpatialExtent readSpatialExtent(DOMHelper dom, Element spElt)
    {
        STTSpatialExtent spatialExtent = new STTSpatialExtent();
        Element bboxElt = dom.getElement(spElt, "BoundingBox");

        if (bboxElt != null) {
	        // try to get the bbox from the list
	        Object obj = findExistingObject(dom, bboxElt);
	        if (obj != null)
	            return (STTSpatialExtent)obj;
	    
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
        // read autoUpdate element
        if (dom.existAttribute(spElt, "@autoUpdate"))
        {
            String sceneId = dom.getAttributeValue(spElt, "@autoUpdate").substring(1);
            WorldScene scene = (WorldScene)objectIds.get(sceneId);
            SpatialExtentUpdater updater = new SceneBboxUpdater(scene);
            spatialExtent.setUpdater(updater);
            updater.setEnabled(true);
        }
        
        return spatialExtent;
    }
    
    /**
     * Reads the provider time extent data
     * @param provider
     * @param dom
     * @param spElt
     */
    public STTTimeExtent readTimeExtent(DOMHelper dom, Element timeElt)
    {
    	STTTimeExtent timeExtent = new STTTimeExtent();
    	
    	if (timeElt != null)
    	{    		
	        Element extentElt = dom.getElement(timeElt, "TimeExtent");
	        String val = null;            
	        
	        try
	        {    
	            if (extentElt != null)
	            {
	                // try to get the time extent from the list
	                Object obj = findExistingObject(dom, extentElt);
	                if (obj != null)
	                {
	                    return (STTTimeExtent)obj;
	                }
	                
	                // read base time
	                val = dom.getElementValue(extentElt, "baseTime");
                    if (val != null)
                    {
    	                if (val.equalsIgnoreCase("now"))
    	                    timeExtent.setBaseAtNow(true);
    	                else
    	                    timeExtent.setBaseTime(DateTimeFormat.parseIso(val));
                    }
	                
	                // read lag time
	                val = dom.getElementValue(extentElt, "lagTime");
	                if (val != null)
	                    timeExtent.setLagTimeDelta(Double.parseDouble(val));
	                
	                // read lead time
	                val = dom.getElementValue(extentElt, "leadTime");
	                if (val != null)
	                    timeExtent.setLeadTimeDelta(Double.parseDouble(val));
	                
	                // read step time
	                val = dom.getElementValue(extentElt, "stepTime");
	                if (val != null)
	                    timeExtent.setTimeStep(Double.parseDouble(val));
	            
	                // store that in the hashtable
	                registerObjectID(dom, extentElt, timeExtent);
	            }
	        }
	        catch (ParseException e)
	        {
	            throw new IllegalStateException("Invalid time: " + val);
	        }
    	}
        // read autoUpdate element
        if (dom.existAttribute(timeElt, "@autoUpdate"))
        {
        	String updateVal = dom.getAttributeValue(timeElt, "@autoUpdate");
        	if(updateVal.startsWith("#")){
	            String sceneId = updateVal.substring(1);
	            WorldScene scene = (WorldScene)objectIds.get(sceneId);
	            TimeExtentUpdater updater = new SceneTimeUpdater(scene);
	            timeExtent.setUpdater(updater);
	            updater.setEnabled(true);
        	} else if("realtime".equalsIgnoreCase(updateVal)){
        		RealTimeUpdater updater = new RealTimeUpdater();
        		String periodStr = dom.getAttributeValue(timeElt, "@updatePeriod");
        		if(periodStr != null){
        			double pd = Double.parseDouble(periodStr);
        			updater.setUpdatePeriod(pd);
        		} else {
            		updater.setUpdatePeriod(5.0);
        		}
        		//  Need a way to spec updatePeriod for realTime 
 	            timeExtent.setUpdater(updater);
 	            updater.setEnabled(true);
        	} else 
         		throw new IllegalStateException("Invalid value for time attribtue 'autoUpdate': " + updateVal);
        }
        
        return timeExtent;
    }
}
