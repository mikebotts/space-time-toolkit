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
import org.vast.ows.OWSException;
import org.vast.ows.OWSQuery;
import org.vast.ows.OWSRequestReader;
import org.vast.ows.sos.SOSRequestReader;
import org.vast.ows.wms.WMSRequestReader;
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
    private Hashtable<String, Resource> resourceIds;
    
    
    public DataProviderReader()
	{
        resourceIds = new Hashtable<String, Resource>();
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
            provider = new SWEProvider();                           
        }
        else if (providerElt.getLocalName().equals("SensorMLDataProvider"))
        {
            provider = new SensorMLProvider();                           
        }
        else
            return null;
        
        // link to resource
        if (dom.existElement(providerElt, "resource"))
        {
            String resId = dom.getAttributeValue(providerElt, "resource/@href").substring(1);
            Resource resource = resourceIds.get(resId);
            provider.setResource(resource);
        }
        
        return provider;
	}
    
    
    /**
     * Reads an OWS provider (WMS, WFS, WCS, SOS, SAS, ...)
     * @param dom
     * @param providerElt
     * @return
     */
    public DataProvider readOWSProvider(DOMReader dom, Element providerElt)
    {
        DataProvider provider = null;
        OWSRequestReader requestReader = null;
        
        // get request type
        Element requestElt = dom.getElement(providerElt, "request/*");
        String serviceType = dom.getAttributeValue(requestElt, "service");
        
        // find out which provider to instantiate
        if (serviceType.equals("SOS"))
        {
            provider = new SOSProvider();                           
            requestReader = new SOSRequestReader();
        }
        else if (serviceType.equals("WMS"))
        {
            provider = new WMSProvider();                           
            requestReader = new WMSRequestReader();
        }
        
        // parse request          
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
        
        return provider;
    }


    public Hashtable<String, Resource> getResourceIds()
    {
        return resourceIds;
    }
}
