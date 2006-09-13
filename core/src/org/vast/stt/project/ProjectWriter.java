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

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
//import java.util.Hashtable;

import org.vast.io.xml.*;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.util.DateTimeFormat;
import org.w3c.dom.*;


/**
 * <p><b>Title:</b><br/>
 * Project Writer
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Writes a project file using info in given Project object 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class ProjectWriter
{
	private DOMWriter dom;
	//private Hashtable<Service, String> serviceIds; // maps Service object to its xml id
	//private Hashtable<Resource, String> resourceIds; // maps Resource object to its xml id
	

	public ProjectWriter()
	{
		//serviceIds = new Hashtable<Service, String>();
		//resourceIds = new Hashtable<Resource, String>();
	}


	/**
	 * Serializes the Project to an XML file at the given path 
	 * @param project
	 * @param url
	 */
	public void writeProject(Project project, String url)
	{
		try
		{
			dom = new DOMWriter();
			dom.createDocument("STTProject");
			writeProject(project, dom, dom.getBaseElement());

			String filePath = (new URL(url)).getPath();
			OutputStream outputStream = new FileOutputStream(filePath);
			dom.writeDOM(dom.getBaseElement(), outputStream, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Writes the full STTProject element
	 * @param project
	 * @param dom
	 * @param projectElt
	 */
	public void writeProject(Project project, DOMWriter dom, Element projectElt)
	{
		this.dom = dom;
		writeMetadata(project, projectElt);
		writeDisplayList(project.getDisplayList(), projectElt);
		writeResourceList(project.getResourceList(), projectElt);
	}


	/**
	 * Writes project Metadata/Header (name, description, author, etc...)
	 * @param project
	 * @param parentElt
	 */
	protected Element writeMetadata(Project project, Element parentElt)
	{
		Element idElt = dom.addElement(parentElt, "Identification");
		
		dom.setElementValue(idElt, "name", project.getName());
		dom.setElementValue(idElt, "description", project.getDescription());
		dom.setElementValue(idElt, "author", project.getAuthor());
		String isoDate = DateTimeFormat.formatIso(project.getDate().getJulianTime(), DateTimeFormat.LOCAL);
		dom.setElementValue(idElt, "dateCreated", isoDate);
		
		return idElt; 
	}

	
	/**
	 * Writes resource list
	 * @param dataFolder
	 * @param parentElt
	 * @return
	 */
	protected Element writeResourceList(ResourceList resourceList, Element parentElt)
	{
		Element listElt = dom.addElement(parentElt, "ResourceList");
		int listSize = resourceList.size();

		// add members
		for (int i = 0; i < listSize; i++)
		{
			Element memberElt = dom.addElement(listElt, "+member");
			Resource resource = resourceList.get(i);
			writeResource(resource, memberElt);
		}
		
		return listElt;
	}
	
	
	/**
	 * Write contents of any kind of Resource object
	 * Decides which method to call
	 * @param resource
	 * @param parentElt
	 * @return
	 */
	protected Element writeResource(Resource resource, Element parentElt)
	{
		Element resourceElt = null;
		
		// TODO writeResource method
        
		return resourceElt;
	}
	

	/**
	 * Writes Scene List data from Project
	 * @param listElt
	 * @return
	 */
	protected Element writeDisplayList(ArrayList<STTDisplay> displayList, Element parentElt)
	{
		Element listElt = dom.addElement(parentElt, "SceneList");
		int listSize = displayList.size();

		// add members
		for (int i = 0; i < listSize; i++)
		{
			Element memberElt = dom.addElement(listElt, "+member");
			Scene scene = (Scene)displayList.get(i);
			writeScene(scene, memberElt);
		}
		
		return listElt;
	}


	/**
	 * Writes a Scene description
	 * @param scene
	 * @param parentElt
	 */
	protected Element writeScene(Scene scene, Element parentElt)
	{
		// add scene element
		Element sceneElt = dom.addElement(parentElt, "Scene");

		// add name
		dom.setElementValue(sceneElt, "name", scene.getName());

		// add data list
		Element contentElt = dom.addElement(sceneElt, "contents");
		writeDataEntry(scene.getDataTree(), contentElt);

		return sceneElt;
	}


	/**
	 * Writes a scene data entry
	 * @param entry
	 * @param parentElt
	 */
	protected Element writeDataEntry(DataEntry entry, Element parentElt)
	{
		Element entryElt = null;

		if (entry instanceof DataFolder)
		{
			entryElt = writeDataList((DataFolder) entry, parentElt);
		}
		else if (entry instanceof DataItem)
		{
			entryElt = writeDataItem((DataItem) entry, parentElt);
		}

		return entryElt;
	}

	
	/**
	 * Writes parameters common to all Data Entries
	 * @param entry
	 * @param entryElt
	 */
	protected void writeCommonParameters(DataEntry entry, Element entryElt)
	{
		// add name
		dom.setElementValue(entryElt, "name", entry.getName());
	}


	/**
	 * Writes the Scene Data List
	 * @param listElt
	 * @return
	 */
	protected Element writeDataList(DataFolder dataList, Element parentElt)
	{
		Element listElt = dom.addElement(parentElt, "DataList");
		writeCommonParameters(dataList, listElt);

		// add members
		int listSize = dataList.size();
		for (int i = 0; i < listSize; i++)
		{
			Element memberElt = dom.addElement(listElt, "+member");
			DataEntry data = dataList.get(i);
			writeDataEntry(data, memberElt);
		}

		return listElt;
	}


	/**
	 * Write a scene data item
	 * @param item
	 * @param parentElt
	 */
	protected Element writeDataItem(DataItem item, Element parentElt)
	{
		Element itemElt = dom.addElement(parentElt, "DataItem");
		writeCommonParameters(item, itemElt);

		// enabled flag
        String enableText = item.isEnabled() ? "true" : "false";
        dom.setAttributeValue(itemElt, "@enabled", enableText);
        
		// TODO dataProvider
		//Element providerElt = dom.addElement(parentElt, "DataItem/dataProvider");

		return itemElt;
	}

	
	/**
	 * Writes a DataProvider description
	 * @param provider
	 * @param parentElt
	 */
	protected Element writeDataProvider(Object provider, Element parentElt)
	{
//		Element providerElt = null;
//		Element requestElt = null;
//		
//		try
//		{
//			if (provider instanceof OWSDataProvider)
//			{
//				if (provider instanceof SOSDataProvider)
//				{
//					providerElt = dom.addElement(parentElt, "SOSDataProvider");
//					requestElt = dom.addElement(providerElt, "SOSDataProvider/request");
//					SOSRequestWriter requestWriter = new SOSRequestWriter();
//					Element sosRequestElt = requestWriter.buildRequestXML(((OWSDataProvider)provider).getQuery(), dom);
//					requestElt.appendChild(sosRequestElt);
//				}
//			}
//			else if (provider instanceof MapImage)
//			{
//				DataItem item = (DataItem) ((MapImage)provider).getAvailableData().get(0);
//				provider = item.getParameter("imageProvider");
//				providerElt = dom.addElement(parentElt, "WMSDataProvider");
//				requestElt = dom.addElement(providerElt, "request");
//				WMSRequestWriter requestWriter = new WMSRequestWriter();
//				Element wmsRequestElt = requestWriter.buildRequestXML(((OWSDataProvider)provider).getQuery(), dom);
//				requestElt.appendChild(wmsRequestElt);
//			}
//		}
//		catch (OWSException e)
//		{
//			e.printStackTrace();
//		}
//		
//		if (provider instanceof OWSDataProvider)
//		{
//			// add default endpoint
//			String getServer = ((OWSDataProvider)provider).getQuery().getGetServer();
//			if (getServer != null)
//				dom.setAttributeValue(requestElt, "@getUrl", getServer);
//
//			String postServer = ((OWSDataProvider)provider).getQuery().getPostServer();
//			if (postServer != null)
//				dom.setAttributeValue(requestElt, "@postUrl", postServer);
//			
//			// write resource
//			dom.addElement(providerElt, "resource");
//		}
//		
//		return providerElt;
		return null;
	}
}
