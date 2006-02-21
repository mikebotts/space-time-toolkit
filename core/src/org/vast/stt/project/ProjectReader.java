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
import org.vast.math.Vector3D;
import org.vast.ows.*;
import org.vast.ows.sld.Color;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sos.*;
import org.vast.ows.wms.*;
import org.vast.ows.wcs.*;
import org.vast.stt.data.DataProvider;
import org.vast.stt.data.OWSProvider;
import org.vast.stt.data.SOSProvider;
import org.vast.stt.data.SWEProvider;
import org.vast.stt.data.WMSProvider;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataItem;
import org.vast.stt.scene.DataEntryList;
import org.vast.stt.scene.Scene;
import org.vast.stt.scene.ViewSettings;
import org.vast.stt.style.CompositeStyler;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.StylerFactory;
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
	private Hashtable<String, Service> serviceIds;
	private Hashtable<String, Resource> resourceIds;
	private StylerFactory stylerFactory;
	private SLDReader sldReader;
	
	
	public ProjectReader()
	{
		serviceIds = new Hashtable<String, Service>();
		resourceIds = new Hashtable<String, Resource>();
		stylerFactory = new StylerFactory();
		sldReader = new SLDReader();
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
		
		listElt = dom.getElement(projectElt, "ResourceList");
		project.setResourceList((ResourceList)readResource(listElt));
		
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
		
		// if id is present, add ref to hashtable
		if (dom.existAttribute(serviceElt, "id"))
			serviceIds.put(dom.getAttributeValue(serviceElt, "id"), service);
		
		return service;
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
		}
		else if (resourceName.equals("ServiceDataSet"))
		{
			resource = readServiceDataSet(resourceElt);
		}
		else if (resourceName.equals("StaticDataSet"))
		{
			resource = readStaticDataSet(resourceElt);
		}
		else
			return null;
				
		// if id is present, add ref to hashtable
		if (dom.existAttribute(resourceElt, "id"))
			resourceIds.put(dom.getAttributeValue(resourceElt, "id"), resource);
		
		// name & description
		resource.setName(dom.getElementValue(resourceElt, "name"));
		resource.setDescription(dom.getElementValue(resourceElt, "description"));
		
		return resource;
	}
	
	
	/**
	 * Reads a Static Data Set description
	 * @param dataSetElt
	 * @return
	 */
	protected StaticDataSet readStaticDataSet(Element dataSetElt)
	{
		StaticDataSet staticData = new StaticDataSet();
		
		// format and url
		staticData.setFormat(dom.getElementValue(dataSetElt, "format"));
		staticData.setSweDataUrl(dom.getAttributeValue(dataSetElt, "sweData/@href"));
        staticData.setRawDataUrl(dom.getAttributeValue(dataSetElt, "rawData/@href"));
		
		return staticData;
	}
	
	
	/**
	 * Reads a Service Data Set description
	 * @param dataSetElt
	 * @return
	 */
	protected ServiceDataSet readServiceDataSet(Element dataSetElt)
	{
		ServiceDataSet serviceLayer = new ServiceDataSet();
		
		// parse layerId
		serviceLayer.setLayerID(dom.getElementValue(dataSetElt, "layerId"));
		
		// parse or link to Service EndPoint description
		String serviceRef = dom.getAttributeValue(dataSetElt, "service/href");
		if (dom.existAttribute(dataSetElt, "service/href") && serviceRef.startsWith("#"))
		{
			String id = serviceRef.substring(1);
			serviceLayer.setService(serviceIds.get(id));
		}
		else
		{
			Element serviceElt = dom.getElement(dataSetElt, "service/Service");
			Service service = readService(serviceElt);
			serviceLayer.setService(service);
		}
		
		// parse service layer capabilities
		try
		{
			OWSServiceCapabilities caps = null;
			Element capsElt = dom.getElement(dataSetElt, "capabilities/*");
			
			if (capsElt != null)
			{			
				String serviceType = serviceLayer.getService().getType();	
				
				// use right capabilities parser based on service type
				if (serviceType.equalsIgnoreCase("WMS"))
				{
					WMSCapabilitiesReader reader = new WMSCapabilitiesReader();
					caps = reader.readCapabilities(capsElt);
				}
				if (serviceType.equalsIgnoreCase("WFS"))
				{
					// TODO WFS layer
				}
				if (serviceType.equalsIgnoreCase("WCS"))
				{
					// TODO WCS layer 
				}
				if (serviceType.equalsIgnoreCase("SOS"))
				{
					SOSCapabilitiesReader reader = new SOSCapabilitiesReader();
					caps = reader.readCapabilities(capsElt);
				}
				
				serviceLayer.setCapabilities(caps.getLayers().get(0));
			}
		}
		catch (OWSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return serviceLayer;
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
		
		// set scene properties
		scene.setName(dom.getElementValue(sceneElt, "name"));
		
		// read view settings
		Element viewSettingsElt = dom.getElement(sceneElt, "view/ViewSettings");
		ViewSettings viewSettings = readViewSettings(viewSettingsElt);
		scene.setViewSettings(viewSettings);
		
		// read data item list
		Element listElt = dom.getElement(sceneElt, "contents/DataList");
		DataEntryList dataList = (DataEntryList)readDataEntry(listElt);
		scene.setDataList(dataList);
		
		return scene;
	}
	
	
	/**
	 * Reads Scene View Settings (camera, target, ortho, colors...)
	 * @param viewSettingsElt
	 * @return
	 */
	protected ViewSettings readViewSettings(Element viewSettingsElt)
	{
		String val;
		ViewSettings viewSettings = new ViewSettings();
		
		// background color
		String colorText = dom.getElementValue(viewSettingsElt, "backgroundColor");
		if (colorText != null)
		{
			Color backColor = new Color(colorText.substring(1));
			viewSettings.setBackgroundColor(backColor);
		}
		
		// camera position
		Vector3D cameraPos = readVector(dom.getElement(viewSettingsElt, "cameraPos"));
		if (cameraPos != null)
			viewSettings.setCameraPos(cameraPos);
		
		// camera target position
		Vector3D targetPos = readVector(dom.getElement(viewSettingsElt, "targetPos"));
		if (targetPos != null)
			viewSettings.setTargetPos(targetPos);
		
		// camera up direction
		Vector3D upDir = readVector(dom.getElement(viewSettingsElt, "upDirection"));
		if (upDir != null)
			viewSettings.setUpDirection(upDir);
		
		// ortho projection width
		val = dom.getElementValue(viewSettingsElt, "orthoWidth");		
		if (val != null)
		{
			double orthoWidth = Double.parseDouble(val);
			viewSettings.setOrthoWidth(orthoWidth);
		}
		
		// ortho projection height
		val = dom.getElementValue(viewSettingsElt, "orthoHeight");
		if (val != null)
		{
			double orthoHeight = Double.parseDouble(val);
			viewSettings.setOrthoHeight(orthoHeight);
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
		
		return viewSettings;
	}
	
	
	/**
	 * Reads 3D vector coordinates from a comma separated number list
	 * @param vectorElt
	 * @return
	 */
	protected Vector3D readVector(Element vectorElt)
	{
		if (vectorElt == null)
			return null;
		
		String[] coords = dom.getElementValue(vectorElt, "").split(" ");
		
		double x = Double.parseDouble(coords[0]);
		double y = Double.parseDouble(coords[1]);
		double z = Double.parseDouble(coords[2]);
		
		Vector3D vect = new Vector3D(x, y, z);		
		return vect;
	}
	
	
	/**
	 * Reads any DataEntry type (decides which method to call)
	 * @param dataEntryElt
	 * @return
	 */
	protected DataEntry readDataEntry(Element dataEntryElt)
	{
		DataEntry dataEntry = null;
		
		if (dataEntryElt.getLocalName().equals("DataList"))
			dataEntry = readDataList(dataEntryElt);
		else if (dataEntryElt.getLocalName().equals("DataItem"))
			dataEntry = readDataItem(dataEntryElt);
		
		// if successful, setup common parameters
		if (dataEntry != null)
		{
			// name
			dataEntry.setName(dom.getElementValue(dataEntryElt, "name"));
			
			// enabled ?
			String enabled = dom.getAttributeValue(dataEntryElt, "enabled");
			if (enabled != null)
			{
				if (enabled.equalsIgnoreCase("true"))
					dataEntry.setEnabled(true);
				else
					dataEntry.setEnabled(false);
			}
		}
		
		return dataEntry;
	}
	
	
	/**
	 * Reads Data Item List and fill it with DataEntries
	 * @param listElt
	 * @return
	 */
	protected DataEntryList readDataList(Element listElt)
	{
		NodeList memberElts = dom.getElements(listElt, "member/*");
		int listSize = memberElts.getLength();
		DataEntryList dataList = new DataEntryList(listSize);
		
		// members
		for (int i=0; i<listSize; i++)
		{
			Element dataElt = (Element)memberElts.item(i);
			DataEntry dataEntry = readDataEntry(dataElt);			
			if (dataEntry != null)
				dataList.add(dataEntry);
		}
		
		return dataList;
	}
	
	
	/**
	 * Reads a DataItem entry which includes the data source (query),
	 * the different style layers and options
	 * @param dataItemElt
	 * @return
	 */
	protected DataItem readDataItem(Element dataItemElt)
	{
		DataItem dataItem = new DataItem();
						
		// data provider
		Element providerElt = dom.getElement(dataItemElt, "dataProvider/*");
		DataProvider provider = readDataProvider(providerElt);
		dataItem.setDataProvider(provider);
		
		// style/symbolizer list
		NodeList symElts = dom.getElements(dataItemElt, "style/*");
		int listSize = symElts.getLength();
		
		if (listSize > 1)
		{
			CompositeStyler compositeStyler = new CompositeStyler(listSize);
			
			// read all stylers
			for (int i=0; i<listSize; i++)
			{
				Element symElt = (Element)symElts.item(i);
				DataStyler styler = readSymbolizer(provider, symElt);
				compositeStyler.getStylerList().add(styler);
			}
			
			dataItem.setStyler(compositeStyler);
		}
		else
		{
			// read only one styler
			Element symElt = (Element)symElts.item(0);
			DataStyler styler = readSymbolizer(provider, symElt);
			dataItem.setStyler(styler);
		}		
		
		return dataItem;
	}
	
	
	/**
	 * Reads an SLD Symbolizer description (uses SLDReader)
	 * @param provider
	 * @param symElt
	 * @return
	 */
	protected DataStyler readSymbolizer(DataProvider provider, Element symElt)
	{
		Symbolizer symbolizer = sldReader.readSymbolizer(dom, symElt);
		DataStyler styler = stylerFactory.createStyler(provider, symbolizer);
		
		// read enabled attribute
		if (styler != null)
		{
			String enabled = dom.getAttributeValue(symElt, "enabled");
			if (enabled != null)
				styler.setEnabled(enabled.equalsIgnoreCase("true") ? true : false);
		}
		
		return styler;
	}
	
	
	/**
	 * Reads a DataProvider description and instantiate the appropriate object
	 * Also parses the specified request if any (for OWS Data Providers) 
	 * @param providerElt
	 * @return
	 */
	protected DataProvider readDataProvider(Element providerElt)
	{
		OWSRequestReader requestReader = null;
		DataProvider provider = null;
		
		// find out which provider to instantiate
		if (providerElt.getLocalName().equals("SOSDataProvider"))
		{
			provider = new SOSProvider();							
			requestReader = new SOSRequestReader();
		}
		else if (providerElt.getLocalName().equals("WMSDataProvider"))
		{
			provider = new WMSProvider();							
			requestReader = new WMSRequestReader();
		}
		else if (providerElt.getLocalName().equals("SWEDataProvider"))
		{
			provider = new SWEProvider();							
		}
		else
			return null;
		
		// parse or link to resource
		Resource resource = null;
		if (dom.existAttribute(providerElt, "resource/@href"))
		{
			String resId = dom.getAttributeValue(providerElt, "resource/@href").substring(1); 
			resource = resourceIds.get(resId);
		}
		else
		{
			Element resourceElt = dom.getElement(providerElt, "resource/*");
			resource = readResource(resourceElt);
		}
		provider.setResource(resource);
		
		// request			
		if (dom.existElement(providerElt, "request"))
		{
			Element requestElt = dom.getElement(providerElt, "request/*");
			try
			{
				OWSQuery query = requestReader.readRequestXML(dom, requestElt);
				((OWSProvider)provider).setQuery(query);
				query.setGetServer(dom.getAttributeValue(providerElt, "request/@getUrl"));
				query.setPostServer(dom.getAttributeValue(providerElt, "request/@postUrl"));
			}
			catch (OWSException e)
			{
				e.printStackTrace();
			}
		}
		
		return provider;		
	}
	
	
	/**
	 * Reads the right query depending on service attrubute
	 * @param requestElt
	 * @return
	 */
	protected OWSQuery readServiceRequest(Element requestElt)
	{
		try
		{
			String serviceType = dom.getAttributeValue(requestElt, "service");
			OWSRequestReader reader = null;
			
			if (serviceType.equalsIgnoreCase("WMS"))
				reader = new WMSRequestReader();
			
			else if (serviceType.equalsIgnoreCase("WCS"))
				reader = new WCSRequestReader();
			
			else if (serviceType.equalsIgnoreCase("SOS"))
				reader = new SOSRequestReader();
			
			else
				return null;
			
			OWSQuery query = reader.readRequestXML(dom, requestElt);			
			return query;
		}
		catch (OWSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	
	protected ArrayList<DataProcess> readProcessList(Element listElt)
	{
		return null;
	}
}
