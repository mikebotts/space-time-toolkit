/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.project.scene;

import java.text.ParseException;
import java.util.Hashtable;

import org.vast.io.xml.DOMReader;
import org.vast.math.Vector3d;
import org.vast.ows.sld.Color;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.vast.stt.project.tree.DataTree;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.util.DateTime;
import org.vast.util.DateTimeFormat;
import org.w3c.dom.Element;


/**
 * <p><b>Title:</b>
 * Scene Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Create Scene object and read options and contents from project XML
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Sep 11, 2006
 * @version 1.0
 */
public class SceneReader extends XMLReader implements XMLModuleReader
{
    protected DataTreeReader dataTreeReader;
    
    
    public SceneReader()
    {
        dataTreeReader = new DataTreeReader();
    }
    
    
    @Override
    public void setObjectIds(Hashtable<String, Object> objectIds)
    {
        super.setObjectIds(objectIds);
        dataTreeReader.setObjectIds(objectIds);
    }


    public Object read(DOMReader dom, Element sceneElt)
    {
        Scene scene = new Scene();
        registerObjectID(dom, sceneElt, scene);
        
        // set scene properties
        scene.setName(dom.getElementValue(sceneElt, "name"));
        
        // read time settings
        Element timeSettingsElt = dom.getElement(sceneElt, "time/TimeSettings");
        TimeSettings timeSettings = readTimeSettings(dom, timeSettingsElt);
        scene.setTimeSettings(timeSettings);
        
        // read view settings
        Element viewSettingsElt = dom.getElement(sceneElt, "view/ViewSettings");
        ViewSettings viewSettings = readViewSettings(dom, viewSettingsElt);
        scene.setViewSettings(viewSettings);
        
        // read data item list
        Element listElt = dom.getElement(sceneElt, "contents/DataList");
        dataTreeReader.setParentScene(scene);
        DataTree dataTree = dataTreeReader.readDataTree(dom, listElt);
        scene.setDataTree(dataTree);
        
        return scene;
    }
    
    
    /**
     * Reads Scene Time Settings
     * @param timeSettingsElt
     * @return
     */
    protected TimeSettings readTimeSettings(DOMReader dom, Element timeSettingsElt)
    {
        // try to get it from the table
        Object obj = findExistingObject(dom, timeSettingsElt);
        if (obj != null)
            return (TimeSettings)obj;
                
        String val;
        TimeSettings timeSettings = new TimeSettings();
                
        // current time
        val = dom.getElementValue(timeSettingsElt, "currentTime");
        if (val != null)
        {
            double julianTime = 0;
            
            try
            {
                julianTime = DateTimeFormat.parseIso(val);                
            }
            catch (ParseException e)
            {
            }
            
            timeSettings.setCurrentTime(new DateTime(julianTime));
        }
        
        // lag time
        val = dom.getElementValue(timeSettingsElt, "lagTime");
        if (val != null)
        {
            double time = Double.parseDouble(val);
            timeSettings.setLagTime(time);
        }
        
        // lead time
        val = dom.getElementValue(timeSettingsElt, "leadTime");
        if (val != null)
        {
            double time = Double.parseDouble(val);
            timeSettings.setLeadTime(time);
        }
        
        // step time
        val = dom.getElementValue(timeSettingsElt, "stepTime");
        if (val != null)
        {
            double time = Double.parseDouble(val);
            timeSettings.setStepTime(time);
        }
        
        // real time mode
        val = dom.getElementValue(timeSettingsElt, "realTimeMode");
        if (val.equals("on") || val.equals("yes"))
        {
            timeSettings.setRealTime(true);
        }
        else
            timeSettings.setRealTime(false);
        
        return timeSettings;
    }
    
    
    /**
     * Reads Scene View Settings (camera, target, ortho, colors...)
     * @param viewSettingsElt
     * @return
     */
    protected ViewSettings readViewSettings(DOMReader dom, Element viewSettingsElt)
    {
        // try to get it from the table
        Object obj = findExistingObject(dom, viewSettingsElt);
        if (obj != null)
            return (ViewSettings)obj;
                
        String val;
        ViewSettings viewSettings = new ViewSettings();
        
        // camera target tripod
        String showTripod = dom.getAttributeValue(viewSettingsElt, "@showTripod");
        if (showTripod != null && showTripod.equalsIgnoreCase("true"))
            viewSettings.setShowCameraTarget(true);
        else
            viewSettings.setShowCameraTarget(false);
        
        // arcball circles
        String showArcball = dom.getAttributeValue(viewSettingsElt, "@showArcball");
        if (showArcball != null && showArcball.equalsIgnoreCase("true"))
            viewSettings.setShowArcball(true);
        else
            viewSettings.setShowArcball(false);
        
        // selected item ROI
        String showROI = dom.getAttributeValue(viewSettingsElt, "@showROI");
        if (showROI != null && showROI.equalsIgnoreCase("true"))
            viewSettings.setShowItemROI(true);
        else
            viewSettings.setShowItemROI(false);
        
        // background color
        String colorText = dom.getElementValue(viewSettingsElt, "backgroundColor");
        if (colorText != null)
        {
            Color backColor = new Color(colorText.substring(1));
            viewSettings.setBackgroundColor(backColor);
        }
        
        // intended projection
        String projText = dom.getElementValue(viewSettingsElt, "projection");
        if (projText != null)
        {
            // read argument
            double arg = 0.0;
            try {arg = Double.parseDouble(projText.substring(3));}
            catch (NumberFormatException e) {}
            
            if (projText.equals("ECEF"))
                viewSettings.setProjection(new Projection_ECEF());
            else if (projText.startsWith("LLA"))
                viewSettings.setProjection(new Projection_LLA(arg * Math.PI/180));
            else if (projText.startsWith("MERC"))
                viewSettings.setProjection(new Projection_Mercator(arg * Math.PI/180));
        }
        
        // camera position
        Vector3d cameraPos = readVector(dom, dom.getElement(viewSettingsElt, "cameraPos"));
        if (cameraPos != null)
            viewSettings.setCameraPos(cameraPos);
        
        // camera target position
        Vector3d targetPos = readVector(dom, dom.getElement(viewSettingsElt, "targetPos"));
        if (targetPos != null)
            viewSettings.setTargetPos(targetPos);
        
        // camera up direction
        Vector3d upDir = readVector(dom, dom.getElement(viewSettingsElt, "upDirection"));
        if (upDir != null)
            viewSettings.setUpDirection(upDir);
        
        // ortho projection width
        val = dom.getElementValue(viewSettingsElt, "orthoWidth");       
        if (val != null)
        {
            double orthoWidth = Double.parseDouble(val);
            viewSettings.setOrthoWidth(orthoWidth);
        }
        
        // Z buffer / Rendering near clipping plane
        val = dom.getElementValue(viewSettingsElt, "nearClip");
        if (val != null)
        {
            double nearClip = Double.parseDouble(val);
            viewSettings.setNearClip(nearClip);
        }
        
        // Z buffer / Rendering far clipping plane
        val = dom.getElementValue(viewSettingsElt, "farClip");
        if (val != null)
        {
            double farClip = Double.parseDouble(val);
            viewSettings.setFarClip(farClip);
        }
        
        // Z buffer fudge factor
        val = dom.getElementValue(viewSettingsElt, "zFudgeFactor");
        if (val != null)
        {
            int zFudge = Integer.parseInt(val);
            viewSettings.setZDepthFudgeFactor(zFudge);
        }
        
        // add this new instance to the table
        registerObjectID(dom, viewSettingsElt, viewSettings);
        
        return viewSettings;
    }
    
    
    /**
     * Reads 3D vector coordinates from a comma separated number list
     * @param vectorElt
     * @return
     */
    protected Vector3d readVector(DOMReader dom, Element vectorElt)
    {
        if (vectorElt == null)
            return null;
        
        String[] coords = dom.getElementValue(vectorElt, "").split(" ");
        
        double x = Double.parseDouble(coords[0]);
        double y = Double.parseDouble(coords[1]);
        double z = Double.parseDouble(coords[2]);
        
        Vector3d vect = new Vector3d(x, y, z);      
        return vect;
    }
}
