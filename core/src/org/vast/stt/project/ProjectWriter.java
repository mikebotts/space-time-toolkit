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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.vast.xml.DOMHelper;
import org.vast.stt.project.tree.DataEntry;
import org.vast.stt.project.tree.DataFolder;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.WorldScene;
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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class ProjectWriter
{
	private DOMHelper dom;
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
			dom = new DOMHelper();
			dom.createDocument("STTProject");
			writeProject(project, dom, dom.getBaseElement());

			String filePath = (new URL(url)).getPath();
			OutputStream outputStream = new FileOutputStream(filePath);
			dom.serialize(dom.getBaseElement(), outputStream, null);
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
	public void writeProject(Project project, DOMHelper dom, Element projectElt)
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
			WorldScene scene = (WorldScene)displayList.get(i);
			writeScene(scene, memberElt);
		}
		
		return listElt;
	}


	/**
	 * Writes a Scene description
	 * @param scene
	 * @param parentElt
	 */
	protected Element writeScene(WorldScene scene, Element parentElt)
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
	
	//  Main stub for testing local project write
	public static void main(String [] args) throws Exception {
		ProjectReader reader = new ProjectReader();
//		File file = new File("C:/tcook/work/STT3/projects/Greensburg.xml");
//		reader.readProject("file:///C:/tcook/work/STT3/projects/Greensburg.xml");
//		reader.readProject("file:///C:/tcook/work/STT3/projects/Hsv.xml");  //  use to test DataList refs...
		reader.readProject("file:///C:/tcook/work/STT3/projects/Nasa.xml");
		Project proj = reader.getProject();
		File outFile = new File("C:/tcook/work/STT3/misc/testProjWrite.xml");
    	String urlStr = null;
		try {
			urlStr = outFile.toURI().toURL().getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProjectWriter writer = new ProjectWriter();
    	writer.writeProject(proj, urlStr);
	}
}
