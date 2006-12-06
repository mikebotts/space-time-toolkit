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

package org.vast.stt.project.tree;

import java.util.*;
import org.vast.io.xml.DOMReader;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.vast.stt.project.XMLRegistry;
import org.vast.stt.project.feedback.FeedbackActionReader;
import org.vast.stt.project.feedback.UserAction;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.ExtentReader;
import org.vast.stt.provider.tiling.SpatialTilingProvider;
import org.w3c.dom.*;


/**
 * <p><b>Title:</b><br/>
 * Data Tree Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Regroups utility methods to read DataTree, DataFolder and
 * DataItem from project XML. 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class DataTreeReader extends XMLReader
{
    protected SLDReader sldReader;
    protected ExtentReader extentReader;
    protected FeedbackActionReader actionReader;
    protected Scene parentScene;
    
	
	public DataTreeReader()
	{
        sldReader = new SLDReader();
        extentReader = new ExtentReader();
        actionReader = new FeedbackActionReader();
	}
    
    
    @Override
    public void setObjectIds(Hashtable<String, Object> objectIds)
    {
        super.setObjectIds(objectIds);
        extentReader.setObjectIds(objectIds);
        actionReader.setObjectIds(objectIds);
    }
    
    
    /**
     * Reads the whole Data Tree with items and folders
     * @param dataListElt
     * @param parentScene
     * @return
     */
    public DataTree readDataTree(DOMReader dom, Element dataTreeElt)
    {
        DataFolder folder = (DataFolder)readDataEntry(dom, dataTreeElt);
        DataTree dataTree = new DataTree(folder);
        registerObjectID(dom, dataTreeElt, dataTree);
        return dataTree;
    }
	
	
	/**
	 * Reads any DataEntry type (decides which method to call)
	 * @param dataEntryElt
	 * @return
	 */
    public DataEntry readDataEntry(DOMReader dom, Element dataEntryElt)
	{
		DataEntry dataEntry = null;
		
        // try to get it from the table
        Object obj = findExistingObject(dom, dataEntryElt);
        if (obj != null)
            return (DataEntry)obj;
        
        // if not found parse and instantiate a new one
        if (dataEntryElt.getLocalName().equals("DataList"))
			dataEntry = readDataList(dom, dataEntryElt);
		else if (dataEntryElt.getLocalName().equals("DataItem"))
			dataEntry = readDataItem(dom, dataEntryElt);
        else
        {
            XMLModuleReader reader = XMLRegistry.createReader(dataEntryElt.getLocalName());
            if (reader != null)
            {
                reader.setObjectIds(this.objectIds);
                dataEntry = (DataEntry)reader.read(dom, dataEntryElt);
            }
        }
		
        // only if we got a result
		if (dataEntry != null)
        {
		    // set name
            dataEntry.setName(dom.getElementValue(dataEntryElt, "name"));
            
		    // add this new instance to the table
            registerObjectID(dom, dataEntryElt, dataEntry);
		}
        
		return dataEntry;
	}
	
	
	/**
	 * Reads Data Item List and fill it with DataEntries
	 * @param listElt
	 * @return
	 */
    public DataFolder readDataList(DOMReader dom, Element listElt)
	{
		NodeList memberElts = dom.getElements(listElt, "member");
		int listSize = memberElts.getLength();
        DataFolder dataList = new DataFolder(listSize);
		
		// members
		for (int i=0; i<listSize; i++)
		{
			Element propElt = (Element)memberElts.item(i);
            Element dataElt = dom.getFirstChildElement(propElt);
			DataEntry dataEntry = readDataEntry(dom, dataElt);
            
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
	public DataItem readDataItem(DOMReader dom, Element dataItemElt)
	{
        DataItem dataItem = new DataItem();
        
		// data provider
		Element providerElt = dom.getElement(dataItemElt, "dataProvider/*");
        DataProvider provider = readDataProvider(dom, providerElt);
        dataItem.setDataProvider(provider);
		
		// style/symbolizer list
		NodeList symElts = dom.getElements(dataItemElt, "style");
		int listSize = symElts.getLength();
		
		// read all stylers
        for (int i=0; i<listSize; i++)
        {
            Element symElt = (Element)symElts.item(i);
            Symbolizer symbolizer = readSymbolizer(dom, symElt);
            if (symbolizer != null)
                dataItem.getSymbolizers().add(symbolizer);
        }
        
        // event list
        NodeList eventElts = dom.getElements(dataItemElt, "event");
        listSize = eventElts.getLength();
        
        // read all actions
        for (int i=0; i<listSize; i++)
        {
            Element eventElt = (Element)eventElts.item(i);
            UserAction action = (UserAction)actionReader.read(dom, eventElt);
            if (action != null)
                dataItem.getActions().add(action);
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
     * Reads a DataProvider description
     * @param providerElt
     * @return
     */
    public DataProvider readDataProvider(DOMReader dom, Element providerElt)
    {
        DataProvider provider = null;
        
        // try to get the provider from the list
        Object obj = findExistingObject(dom, providerElt);
        if (obj != null)
            return (DataProvider)obj;
        
        // otherwise create appropriate reader
        XMLModuleReader reader = XMLRegistry.createReader(providerElt.getLocalName());
        reader.setObjectIds(objectIds);
        provider = (DataProvider)reader.read(dom, providerElt);
        registerObjectID(dom, providerElt, provider);
        
        // read name
        String name = dom.getElementValue(providerElt, "name");
        provider.setName(name);
        
        // read spatial extent
        Element spElt = dom.getElement(providerElt, "spatialExtent");
        extentReader.readSpatialExtent(provider, dom, spElt);
        
        // read time extent
        Element timeElt = dom.getElement(providerElt, "timeExtent");
        extentReader.readTimeExtent(provider, dom, timeElt);
        
        // read quadTree option
        String text = dom.getAttributeValue(providerElt, "quadTree");
        if (text != null && text.equalsIgnoreCase("true"))
            provider = new SpatialTilingProvider(provider);
        
        return provider;
    }
    
    
    /**
     * Reads an SLD Symbolizer description (uses SLDReader)
     * @param provider
     * @param symElt
     * @return
     */
    public Symbolizer readSymbolizer(DOMReader dom, Element styleElt)
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


    public void setParentScene(Scene parentScene)
    {
        this.parentScene = parentScene;
    }
}
