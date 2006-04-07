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

import java.util.Hashtable;
import org.vast.io.xml.DOMReader;
import org.vast.ows.OWSCapabilitiesReader;
import org.vast.ows.OWSException;
import org.vast.ows.OWSQuery;
import org.vast.ows.OWSRequestReader;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.sos.SOSCapabilitiesReader;
import org.vast.ows.sos.SOSRequestReader;
import org.vast.ows.wms.WMSCapabilitiesReader;
import org.vast.ows.wms.WMSRequestReader;
import org.vast.process.DataProcess;
import org.vast.sensorML.SMLException;
import org.vast.sensorML.reader.SystemReader;
import org.vast.stt.data.*;
import org.w3c.dom.*;


/**
 * <p><b>Title:</b><br/>
 * DataProvider Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Parses a Data Provider XML element from project file
 * and instantiates corresponding Provider Object. 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class DataProviderReader
{
    protected Hashtable<String, Object> objectIds;
    
    
    public DataProviderReader()
	{
        objectIds = new Hashtable<String, Object>();
	}
	
	
    /**
     * Reads a DataProvider description and instantiates the appropriate object
     * Also parses the specified request if any (for OWS Data Providers)
     * @param dom
     * @param providerElt
     * @return
     */
	public DataProvider readProvider(DOMReader dom, Element providerElt)
	{
        DataProvider provider;
        
        if (providerElt.getLocalName().equals("OWSDataProvider"))
        {
            provider = readOWSProvider(dom, providerElt);
        }
        else if (providerElt.getLocalName().equals("SWEDataProvider"))
        {
            provider = readSWEProvider(dom, providerElt);                           
        }
        else if (providerElt.getLocalName().equals("SensorMLDataProvider"))
        {
            provider = readSensorMLProvider(dom, providerElt);                           
        }
        else
            return null;
        
        return provider;
	}
    
    
    /**
     * Reads a SWE DataProvider description
     * @param dom
     * @param providerElt
     * @return
     */
    protected DataProvider readSWEProvider(DOMReader dom, Element providerElt)
    {
        SWEProvider provider = new SWEProvider();
        
        // format and url
        provider.setFormat(dom.getElementValue(providerElt, "format"));
        provider.setSweDataUrl(dom.getAttributeValue(providerElt, "sweData/@href"));
        provider.setRawDataUrl(dom.getAttributeValue(providerElt, "rawData/@href"));
        
        return provider;
    }
    
    
    /**
     * Reads an OWS provider (WMS, WFS, WCS, SOS, SAS, ...)
     * @param dom
     * @param providerElt
     * @return
     */
    public OWSProvider readOWSProvider(DOMReader dom, Element providerElt)
    {
        OWSProvider provider = null;
        OWSRequestReader requestReader = null;
        OWSCapabilitiesReader capsReader = null;
        
        // get request type
        Element requestElt = dom.getElement(providerElt, "request/*");
        Element capsElt = dom.getElement(providerElt, "capabilities/*");
        String serviceType = dom.getAttributeValue(requestElt, "service");
        
        // find out which provider to instantiate
        if (serviceType.equals("SOS"))
        {
            provider = new SOSProvider();                           
            requestReader = new SOSRequestReader();
            capsReader = new SOSCapabilitiesReader();
        }
        else if (serviceType.equals("WMS"))
        {
            provider = new WMSProvider();                           
            requestReader = new WMSRequestReader();
            capsReader = new WMSCapabilitiesReader();
        }
                
        try
        {
            // parse request
            OWSQuery query = requestReader.readRequestXML(dom, requestElt);
            provider.setQuery(query);
            
            // parse capabilities
            if (capsElt != null)
            {
                OWSServiceCapabilities caps = capsReader.readCapabilities(dom, capsElt);
                provider.setServiceCapabilities(caps);
            }
            
            query.setGetServer(dom.getAttributeValue(providerElt, "request/@getUrl"));
            query.setPostServer(dom.getAttributeValue(providerElt, "request/@postUrl"));
        }
        catch (OWSException e)
        {
            e.printStackTrace();
        }
        
        // layerID
        provider.setLayerID(dom.getElementValue(providerElt, "layerId"));
        
        // read service
        // TODO
        
        return provider;
    }
    
    
    /**
     * Reads a SensorML based provider
     * @param resourceElt
     * @return
     */
    protected DataProvider readSensorMLProvider(DOMReader dom, Element providerElt)
    {
        SensorMLProvider provider = new SensorMLProvider();
               
        try
        {
            Element processElt = dom.getElement(providerElt, "process");
            SystemReader systemReader = new SystemReader(dom);
            systemReader.setReadMetadata(false);
            systemReader.setCreateExecutableProcess(true);
            DataProcess process = systemReader.readProcessProperty(processElt);
            provider.setProcess(process);
        }
        catch (SMLException e)
        {
            e.printStackTrace();
        }
        
        return provider;
    }
}
