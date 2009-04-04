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

package org.vast.stt.project;

import java.text.ParseException;
import java.util.*;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.stt.project.tree.DataTreeReader;
import org.vast.util.*;
import org.w3c.dom.*;


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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class ProjectReader extends XMLReader
{
	private DOMHelper dom;    
	private DataTreeReader dataReader;
    private Project project;
	private boolean cancelled = false;
    
	public ProjectReader()
	{
        objectIds = new Hashtable<String, Object>();
        dataReader = new DataTreeReader();
        dataReader.setObjectIds(objectIds);
	}
	
	public void cancelRead(){
		cancelled = true;
		project = null;
	}
	
	
	/**
	 * Reads the whole project file at the specified location
	 * @param url
	 * @return Project object
	 */
	public void readProject(String url)
	{
		try
		{
            DOMHelper dom = new DOMHelper(url, false);
			project = readProject(dom, dom.getBaseElement());
//			System.err.println("Project assigned in ProjectReader.readProject");
		}
		catch (DOMHelperException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private Project readProject(DOMHelper dom, Element projectElt)
	{
		Element listElt;
		Project project = new Project();
		this.dom = dom;
		
		readMetadata(project, projectElt);
		readExtensions(project, projectElt);
		
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
	 * Make sure we load all extension plugin classes so that
	 * all plugins are activated
	 * @param projectElt
	 */
	protected void readExtensions(Project project, Element projectElt)
	{
		NodeList extensionElts = dom.getElements("Extension");
		int listSize = extensionElts.getLength();
		ArrayList<String> extensionList = new ArrayList<String>(listSize);
		
		for (int i=0; i<listSize; i++)
		{
			Element extElt = (Element)extensionElts.item(i);
			String className = dom.getAttributeValue(extElt, "name");
			
			try
			{
				// load the class so that the whole plugin is activated
				Class.forName(className);
				extensionList.add(className);
			}
			catch (ClassNotFoundException e)
			{
				ExceptionSystem.display(new Exception("Plugin " + className + " not found"));
			}			
		}
		
		project.setExtensionList(extensionList);
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
        {
            resource = readResourceList(resourceElt);
            resource.setName(dom.getElementValue(resourceElt, "name"));
            resource.setDescription(dom.getElementValue(resourceElt, "description"));
        }
        else
            resource = dataReader.readDataProvider(dom, resourceElt);

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
    
    public Project getProject() {
		return project;
	}

}
