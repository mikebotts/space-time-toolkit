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

import java.text.ParseException;

import org.vast.stt.dynamics.RealTimeUpdater;
import org.vast.stt.dynamics.SceneBboxUpdater;
import org.vast.stt.dynamics.SceneTimeUpdater;
import org.vast.stt.dynamics.SpatialExtentUpdater;
import org.vast.stt.dynamics.TimeExtentUpdater;
import org.vast.stt.project.XMLReader;
import org.vast.stt.project.world.WorldScene;
import org.vast.util.DateTimeFormat;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;


/**
 * <p><b>Title:</b>
 * Extent Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Helper reader for reading TimeExtent and SpatialExtent
 * </p>
 *
 * <p>Copyright (c) 2007</p>
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
    public STTTimeExtent readTimeExtent(DOMHelper dom, Element timeElt) throws NumberFormatException
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
    	                {
    	                	double baseTime = DateTimeFormat.parseIso(val);
    	                    timeExtent.setBaseTime(baseTime);
    	                    timeExtent.setDefaultBaseTime(baseTime);
    	                }
                    }  // else {
                    //   val==null, no baseTime present.  Set def here if desired
                    //   otherwise, def is Jan 1, 1970...  TC
                    //  {
	                
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
	                
	                val = dom.getElementValue(extentElt, "timeBias");
	                if (val != null) 
	                    timeExtent.setTimeBias(Double.parseDouble(val));

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
	            //  
	            if(scene == null){
	            	throw new IllegalStateException("Invalid SCENE_ID for time attribtue 'autoUpdate': " + updateVal);
	            }
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
            		updater.setUpdatePeriod(60.0);
        		}
 	            timeExtent.setUpdater(updater);
 	            updater.setEnabled(true);
        	} else 
         		throw new IllegalStateException("Invalid value for time attribtue 'autoUpdate': " + updateVal);
        }
        
        return timeExtent;
    }
}
