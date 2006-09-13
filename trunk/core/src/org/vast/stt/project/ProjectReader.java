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
import org.vast.stt.project.tree.DataTreeReader;
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
public class ProjectReader extends XMLReader
{
	private DOMReader dom;    
	private DataTreeReader dataReader;
    
	
	public ProjectReader()
	{
        objectIds = new Hashtable<String, Object>();
        dataReader = new DataTreeReader();
        dataReader.setObjectIds(objectIds);
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
		
        listElt = dom.getElement(projectElt, "SceneList");
        project.setDisplayList(readDisplayList(listElt));
        
		listElt = dom.getElement(projectElt, "ResourceList");
        if (listElt != null)
            project.setResourceList((ResourceList)readResource(listElt));
		
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
        registerObjectID(dom, serviceElt, service);
        
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
            resource = dataReader.readDataProvider(dom, resourceElt);
        
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
	 * Reads Scene List and fill it with SceneDescriptors
	 * @param listElt
	 * @return
	 */
	protected ArrayList<STTDisplay> readDisplayList(Element listElt)
	{
		if (listElt == null)
			return null;
		
		NodeList displayElts = dom.getElements(listElt, "member/*");
		int listSize = displayElts.getLength();		
		ArrayList<STTDisplay> displayList = new ArrayList<STTDisplay>(listSize);
		
		for (int i=0; i<listSize; i++)
		{
			Element sceneElt = (Element)displayElts.item(i);
			STTDisplay display = readDisplay(sceneElt);
            displayList.add(display);
		}
		
		return displayList;
	}
    
    
    /**
     * Reads a display element
     * Create the right reader using XMLModuleRegistry
     * @param displayElt
     * @return
     */
    protected STTDisplay readDisplay(Element displayElt)
    {
        STTDisplay display = null;
        
        // try to get the provider from the list
        Object obj = findExistingObject(dom, displayElt);
        if (obj != null)
            return (STTDisplay)obj;
        
        // otherwise create appropriate reader
        XMLModuleReader reader = XMLRegistry.createReader(displayElt.getLocalName());
        if (reader != null)
        {
            reader.setObjectIds(objectIds);
            display = (STTDisplay)reader.read(dom, displayElt);            
        }
			
        if (display != null)
        {
            // read name
            String name = dom.getElementValue(displayElt, "name");
            display.setName(name);
            
            // register in ID table
            registerObjectID(dom, displayElt, display);
        }
               
        return display;
    }
	
	
	protected ArrayList<DataProcess> readProcessList(Element listElt)
	{
		return null;
	}
}
