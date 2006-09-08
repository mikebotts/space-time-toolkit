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

import java.text.ParseException;
import java.util.*;
import org.vast.io.xml.DOMReader;
import org.vast.io.xml.DOMReaderException;
import org.vast.math.Vector3d;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.Symbolizer;
import org.vast.util.*;
import org.w3c.dom.*;
import org.vast.process.*;


/**
 * <p><b>Title:</b><br/>
 * Project Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Parses a project file and create a Project object.
 * Also provides public method to parse auxiliary files such as
 * service lists and color maps. 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class ProjectReader
{
	private DOMReader dom;
    private Hashtable<String, Object> objectIds;
	private SLDReader sldReader;
    private DataProviderReader providerReader;
	
	
	public ProjectReader()
	{
        objectIds = new Hashtable<String, Object>();
		sldReader = new SLDReader();
        providerReader = new DataProviderReader(objectIds);
	}
	
	
	/**
	 * Reads the whole project file at the specified location
	 * @param url
	 * @return Project object
	 */
	public Project readProject(String url)
	{
		try
		{
			DOMReader dom = new DOMReader(url, false);
			return readProject(dom, dom.getBaseElement());
		}
		catch (DOMReaderException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Project readProject(DOMReader dom, Element projectElt)
	{
		Element listElt;
		Project project = new Project();
		this.dom = dom;
		
		readMetadata(project, projectElt);
		
		listElt = dom.getElement(projectElt, "ServiceList");
        if (listElt != null)
            project.setServiceList(readServiceList(listElt));
		
		//listElt = dom.getElement(projectElt, "ResourceList");
		//project.setResourceList((ResourceList)readResource(listElt));
		
		listElt = dom.getElement(projectElt, "SceneList");
		project.setSceneList(readSceneList(listElt));
		
		return project;
	}
	
	
	/**
	 * Read project Identification section
	 * @param project
	 */
	protected void readMetadata(Project project, Element projectElt)
	{
		try
		{
			Element idElt = dom.getElement(projectElt, "Identification"); 
			project.setName(dom.getElementValue(idElt, "name"));
			project.setDescription(dom.getElementValue(idElt, "description"));
			project.setAuthor(dom.getElementValue(idElt, "author"));
			DateTime date = new DateTime(DateTimeFormat.parseIso(dom.getElementValue(idElt, "dateCreated")));
			project.setDate(date);
		}
		catch (ParseException e)
		{
		}
	}
	
	
	/**
	 * Reads Service List and fill it with ServiceDescriptors
	 * @param listElt
	 * @return
	 */
	protected ArrayList<Service> readServiceList(Element listElt)
	{
		NodeList serviceElts = dom.getElements(listElt, "member/Service");
		int listSize = serviceElts.getLength();
		ArrayList<Service> serviceList = new ArrayList<Service>(listSize);
		
		for (int i=0; i<listSize; i++)
		{
			Element serviceElt = (Element)serviceElts.item(i);
			Service service = readService(serviceElt);
			serviceList.add(service);
		}
		
		return serviceList;
	}
	
	
	/**
	 * Reads a Service Endpoint description
	 * @param serviceElt
	 * @return
	 */
	protected Service readService(Element serviceElt)
	{
		Service service = new Service();
		
		// set service properties
		service.setType(dom.getElementValue(serviceElt, "type"));
		service.setName(dom.getElementValue(serviceElt, "name"));
		service.setDescription(dom.getElementValue(serviceElt, "description"));
		service.setUrl(dom.getElementValue(serviceElt, "url"));
		service.setVersion(dom.getElementValue(serviceElt, "version"));
		
		// add this new instance to the table
        registerObjectID(serviceElt, service);
        
		return service;
	}
    
    
    /**
     * Reads a Resource and create corresponding Resource object.
     * This methods decides the right specific method to call.
     * @param resourceElt
     * @return
     */
    protected Resource readResource(Element resourceElt)
    {
        if (resourceElt == null)
            return null;
        
        String resourceName = resourceElt.getLocalName();
        Resource resource = null;
        
        if (resourceName.equals("ResourceList"))
            resource = readResourceList(resourceElt);
        else
            resource = readDataProvider(resourceElt);
        
        // name & description
        resource.setName(dom.getElementValue(resourceElt, "name"));
        resource.setDescription(dom.getElementValue(resourceElt, "description"));
        
        return resource;
    }
	
	
	/**
	 * Reads Resource List and fill it with Resource objects
	 * @param listElt
	 * @return
	 */
	protected ResourceList readResourceList(Element listElt)
	{
		if (listElt == null)
			return null;
		
		NodeList resourceElts = dom.getElements(listElt, "member/*");
		int listSize = resourceElts.getLength();
		ResourceList resourceList = new ResourceList(listSize);
		
		for (int i=0; i<listSize; i++)
		{
			Element resourceElt = (Element)resourceElts.item(i);
			Resource resource = readResource(resourceElt);
			if (resource != null)
				resourceList.add(resource);
		}
		
		return resourceList;
	}
    
    
    /**
     * Reads a DataProvider description
     * @param providerElt
     * @return
     */
    protected DataProvider readDataProvider(Element providerElt)
    {
        DataProvider provider = null;
        
        // try to get the provider from the list
        Object obj = findExistingObject(providerElt);
        if (obj != null)
            return (DataProvider)obj;
        
        // otherwise instantiate a new one
        provider = providerReader.readProvider(dom, providerElt);
        registerObjectID(providerElt, provider);
        
        return provider;
    }
	
	
	/**
	 * Reads Scene List and fill it with SceneDescriptors
	 * @param listElt
	 * @return
	 */
	protected ArrayList<Scene> readSceneList(Element listElt)
	{
		if (listElt == null)
			return null;
		
		NodeList sceneElts = dom.getElements(listElt, "member/Scene");
		int listSize = sceneElts.getLength();		
		ArrayList<Scene> sceneList = new ArrayList<Scene>(listSize);
		
		for (int i=0; i<listSize; i++)
		{
			Element sceneElt = (Element)sceneElts.item(i);
			Scene scene = readScene(sceneElt);
			sceneList.add(scene);
		}
		
		return sceneList;
	}
	
	
	/**
	 * Reads Scene properties and contents
	 * @param sceneElt
	 * @return
	 */
	protected Scene readScene(Element sceneElt)
	{
		Scene scene = new Scene();
        registerObjectID(sceneElt, scene);
        
		// set scene properties
		scene.setName(dom.getElementValue(sceneElt, "name"));
		
		// read time settings
        Element timeSettingsElt = dom.getElement(sceneElt, "time/TimeSettings");
        TimeSettings timeSettings = readTimeSettings(timeSettingsElt);
        scene.setTimeSettings(timeSettings);
        
		// read view settings
		Element viewSettingsElt = dom.getElement(sceneElt, "view/ViewSettings");
		ViewSettings viewSettings = readViewSettings(viewSettingsElt);
		scene.setViewSettings(viewSettings);
		
		// read data item list
		Element listElt = dom.getElement(sceneElt, "contents/DataList");
		DataTree dataTree = readDataTree(listElt, scene);
		scene.setDataTree(dataTree);
		
		return scene;
	}
    
    
    /**
     * Reads Scene Time Settings
     * @param timeSettingsElt
     * @return
     */
    protected TimeSettings readTimeSettings(Element timeSettingsElt)
    {
        // try to get it from the table
        Object obj = findExistingObject(timeSettingsElt);
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
	protected ViewSettings readViewSettings(Element viewSettingsElt)
	{
	    // try to get it from the table
        Object obj = findExistingObject(viewSettingsElt);
        if (obj != null)
            return (ViewSettings)obj;
                
        String val;
		ViewSettings viewSettings = new ViewSettings();
		        
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
            if (projText.equals("ECEF"))
                viewSettings.setProjection(new Projection_ECEF());
            else if (projText.startsWith("LLA"))
            {
                double centerLon;
                try
                {
                    centerLon = Double.parseDouble(projText.substring(3));
                }
                catch (NumberFormatException e)
                {
                    centerLon = 0.0;
                }
                viewSettings.setProjection(new Projection_LLA(centerLon * Math.PI/180));
            }
        }
		
		// camera position
		Vector3d cameraPos = readVector(dom.getElement(viewSettingsElt, "cameraPos"));
		if (cameraPos != null)
			viewSettings.setCameraPos(cameraPos);
		
		// camera target position
		Vector3d targetPos = readVector(dom.getElement(viewSettingsElt, "targetPos"));
		if (targetPos != null)
			viewSettings.setTargetPos(targetPos);
		
		// camera up direction
		Vector3d upDir = readVector(dom.getElement(viewSettingsElt, "upDirection"));
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
        registerObjectID(viewSettingsElt, viewSettings);
        
		return viewSettings;
	}
	
	
	/**
	 * Reads 3D vector coordinates from a comma separated number list
	 * @param vectorElt
	 * @return
	 */
	protected Vector3d readVector(Element vectorElt)
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
    
    
    /**
     * Reads the whole Data Tree with items and folders
     * @param dataListElt
     * @param parentScene
     * @return
     */
    protected DataTree readDataTree(Element dataTreeElt, Scene parentScene)
    {
        DataFolder folder = (DataFolder)readDataEntry(dataTreeElt, parentScene);
        DataTree dataTree = new DataTree(folder);
        registerObjectID(dataTreeElt, dataTree);
        return dataTree;
    }
	
	
	/**
	 * Reads any DataEntry type (decides which method to call)
	 * @param dataEntryElt
	 * @return
	 */
	protected DataEntry readDataEntry(Element dataEntryElt, Scene parentScene)
	{
		DataEntry dataEntry = null;
		
        // try to get it from the table
        Object obj = findExistingObject(dataEntryElt);
        if (obj != null)
            return (DataEntry)obj;
        
        // if not found parse and instantiate a new one        
		if (dataEntryElt.getLocalName().equals("DataList"))
			dataEntry = readDataList(dataEntryElt, parentScene);
		else if (dataEntryElt.getLocalName().equals("DataItem"))
			dataEntry = readDataItem(dataEntryElt, parentScene);
		
		// if successful, setup common parameters
		if (dataEntry != null)
		{
			// name
			dataEntry.setName(dom.getElementValue(dataEntryElt, "name"));
		}
		
		// add this new instance to the table
        registerObjectID(dataEntryElt, dataEntry);
        
		return dataEntry;
	}
	
	
	/**
	 * Reads Data Item List and fill it with DataEntries
	 * @param listElt
	 * @return
	 */
	protected DataFolder readDataList(Element listElt, Scene parentScene)
	{
		NodeList memberElts = dom.getElements(listElt, "member");
		int listSize = memberElts.getLength();
        DataFolder dataList = new DataFolder(listSize);
		
		// members
		for (int i=0; i<listSize; i++)
		{
			Element propElt = (Element)memberElts.item(i);
            Element dataElt = dom.getFirstChildElement(propElt);
			DataEntry dataEntry = readDataEntry(dataElt, parentScene);		
			if (dataEntry != null)
            {
				dataList.add(dataEntry);
                
                // set visibility if it's a DataItem
                if (dataEntry instanceof DataItem)
                {
                    String visText = dom.getAttributeValue(propElt, "visible");
                    if (visText != null && visText.equals("true"))
                        parentScene.setItemVisibility((DataItem)dataEntry, true);
                }                    
            }
		}
		
		return dataList;
	}
	
	
	/**
	 * Reads a DataItem entry which includes the data source (query),
	 * the different style layers and options
	 * @param dataItemElt
	 * @return
	 */
	protected DataItem readDataItem(Element dataItemElt, Scene parentScene)
	{
        DataItem dataItem = new DataItem();
        
		// data provider
		Element providerElt = dom.getElement(dataItemElt, "dataProvider/*");
        DataProvider provider = readDataProvider(providerElt);
        dataItem.setDataProvider(provider);
		
		// style/symbolizer list
		NodeList symElts = dom.getElements(dataItemElt, "style");
		int listSize = symElts.getLength();
		
		// read all stylers
        for (int i=0; i<listSize; i++)
        {
            Element symElt = (Element)symElts.item(i);
            Symbolizer symbolizer = readSymbolizer(dataItem, symElt);
            if (symbolizer != null)
                dataItem.getSymbolizers().add(symbolizer);
        }
        
        // enabled ?
        String enabled = dom.getAttributeValue(dataItemElt, "enabled");
        if ((enabled != null) && (enabled.equalsIgnoreCase("true")))
            dataItem.setEnabled(true);
        else
            dataItem.setEnabled(false);
        
		return dataItem;
	}
	
	
    /**
     * Reads an SLD Symbolizer description (uses SLDReader)
     * @param provider
     * @param symElt
     * @return
     */
    protected Symbolizer readSymbolizer(DataItem dataItem, Element styleElt)
    {
        Element symElt = dom.getFirstChildElement(styleElt);
        Symbolizer symbolizer = sldReader.readSymbolizer(dom, symElt);
        
        if (symbolizer == null)
            return null;
        
        // read name
        String name = dom.getAttributeValue(styleElt, "name");
        symbolizer.setName(name);
        
        // read enabled attribute
        if (symbolizer != null)
        {
            String enabled = dom.getAttributeValue(symElt, "enabled");
            if (enabled != null)
                symbolizer.setEnabled(enabled.equalsIgnoreCase("true") ? true : false);
        }
        
        return symbolizer;
    }
    
    
    /**
     * Try to retrieve an object that has already been parsed
     * and should be referenced instead of creating a new instance.
     * @param objElt
     * @return
     */
    protected Object findExistingObject(Element objElt)
    {
        Object obj = null;
        String id = dom.getAttributeValue(objElt, "id");
        if (id != null)
            obj = objectIds.get(id);
        return obj;
    }
    
    
    /**
     * Add an object to the id->object map table
     * @param objElt
     * @param obj
     */
    protected void registerObjectID(Element objElt, Object obj)
    {
        String id = dom.getAttributeValue(objElt, "id");
        
        if ((id != null) && (obj != null))
            objectIds.put(id, obj);
    }
	
	
	protected ArrayList<DataProcess> readProcessList(Element listElt)
	{
		return null;
	}
}
