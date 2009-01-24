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

package org.vast.stt.project.tree;

import java.util.*;
import org.vast.xml.DOMHelper;
import org.vast.ows.sld.SLDReader;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.vast.stt.project.XMLRegistry;
import org.vast.stt.project.feedback.ItemActionReader;
import org.vast.stt.project.feedback.ItemAction;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.provider.CachedProvider;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.ExtentReader;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.STTTimeExtent;
import org.vast.stt.provider.tiling.SpatialTilingProvider;
import org.vast.stt.style.IconManager;
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
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class DataTreeReader extends XMLReader
{
    protected SLDReader sldReader;
    protected ExtentReader extentReader;
    protected ItemActionReader actionReader;
    protected Scene parentScene;
    
	
	public DataTreeReader()
	{
        sldReader = new SLDReader(IconManager.getInstance());
        extentReader = new ExtentReader();
        actionReader = new ItemActionReader();
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
    public DataTree readDataTree(DOMHelper dom, Element dataTreeElt)
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
    public DataEntry readDataEntry(DOMHelper dom, Element dataEntryElt)
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
            
            // set description
            dataEntry.setDescription(dom.getElementValue(dataEntryElt, "description"));
            
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
    public DataFolder readDataList(DOMHelper dom, Element listElt)
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
	public DataItem readDataItem(DOMHelper dom, Element dataItemElt)
	{
        DataItem dataItem = new DataItem();
        
		// data provider
		Element providerElt = dom.getElement(dataItemElt, "dataProvider/*");
        if (providerElt != null)
        {
			DataProvider provider = readDataProvider(dom, providerElt);
	        dataItem.setDataProvider(provider);
        }
		
        ///////////////////////////
		// style/symbolizer list //
        ///////////////////////////
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
        
        ///////////////
        // mask list //
        ///////////////
        NodeList maskElts = dom.getElements(dataItemElt, "mask");
        listSize = maskElts.getLength();
        
        // read all masks
        for (int i=0; i<listSize; i++)
        {
            Element itemElt = (Element)maskElts.item(i);
            DataItem maskItem = readMask(dom, itemElt);
            if (dataItem != null)
                dataItem.getMasks().add(maskItem);
        }
        
        ////////////////
        // event list //
        ////////////////
        NodeList eventElts = dom.getElements(dataItemElt, "event");
        listSize = eventElts.getLength();
        
        // read all actions
        for (int i=0; i<listSize; i++)
        {
            Element eventElt = (Element)eventElts.item(i);
            ItemAction action = (ItemAction)actionReader.read(dom, eventElt);
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
    public DataProvider readDataProvider(DOMHelper dom, Element providerElt)
    {
        DataProvider provider = null;
        
        // try to get the provider from the list
        Object obj = findExistingObject(dom, providerElt);
        if (obj != null)
            return (DataProvider)obj;
        
        // otherwise create appropriate reader
        XMLModuleReader reader = XMLRegistry.createReader(providerElt.getLocalName());
        if (reader == null)
        	return null;
        
        reader.setObjectIds(objectIds);
        provider = (DataProvider)reader.read(dom, providerElt);
        registerObjectID(dom, providerElt, provider);
        
        // read name
        String name = dom.getElementValue(providerElt, "name");
        provider.setName(name);
        
        // read description
        String desc = dom.getElementValue(providerElt, "description");
        provider.setDescription(desc);
        
        // read spatial extent
        Element spElt = dom.getElement(providerElt, "spatialExtent");
        if (spElt != null) {
            STTSpatialExtent spatialExtent = extentReader.readSpatialExtent(dom, spElt);
            provider.setSpatialExtent(spatialExtent);
        }
        
        // read time extent
        Element timeElt = dom.getElement(providerElt, "timeExtent");
        if (timeElt != null) {
        	 STTTimeExtent timeExtent = extentReader.readTimeExtent(dom, timeElt);
             provider.setTimeExtent(timeExtent);	
        }
        
        // read quadTree option
        // TODO implement XMLReader and handle as cache extension
        // TODO modify project files accordingly
        String text = dom.getAttributeValue(providerElt, "quadTree");
        if (text != null && text.equalsIgnoreCase("true"))
        {
            provider = new SpatialTilingProvider(provider);
            
            // read max spatial extent
            Element maxSpElt = dom.getElement(providerElt, "maxSpatialExtent");
            if (maxSpElt != null) {
                STTSpatialExtent spatialExtent = extentReader.readSpatialExtent(dom, maxSpElt);
                ((SpatialTilingProvider)provider).setMaxSpatialExtent(spatialExtent);
            }
        }
        
        // read cache options
        Element cacheElt = dom.getElement(providerElt, "cache/*");
        if (cacheElt != null)
        {
            // create appropriate reader
            reader = XMLRegistry.createReader(cacheElt.getLocalName());
            if (reader == null)
                return null;
            
            // read cache options
            CachedProvider cachedProvider = (CachedProvider)reader.read(dom, cacheElt);
            
            // wrap provider with cached provider
            cachedProvider.setSubProvider(provider);
            cachedProvider.setTimeExtent(provider.getTimeExtent());
            provider.setTimeExtent(new STTTimeExtent());          
            provider = cachedProvider;
        }
                
        return provider;
    }
    
    
    /**
     * Reads an SLD Symbolizer description (uses SLDReader)
     * @param provider
     * @param symElt
     * @return
     */
    public Symbolizer readSymbolizer(DOMHelper dom, Element styleElt)
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
     * Reads a DataItem used as a mask
     * @param dom
     * @param maskElt
     * @return
     */
    public DataItem readMask(DOMHelper dom, Element maskElt)
    {
        Element dataItemElt = dom.getFirstChildElement(maskElt);
        DataItem maskItem = readDataItem(dom, dataItemElt);
        maskItem.getOptions().put(DataEntry.MASK, true);
        maskItem.setName(dom.getElementValue(dataItemElt, "name"));
        return maskItem;
    }


    public void setParentScene(Scene parentScene)
    {
        this.parentScene = parentScene;
    }
}
