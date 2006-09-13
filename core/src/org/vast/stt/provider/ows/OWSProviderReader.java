/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.ows;

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
import org.vast.stt.project.XMLReader;
import org.vast.stt.project.XMLModuleReader;
import org.w3c.dom.Element;


/**
 * <p><b>Title:</b>
 * OWS Provider Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Read OWS Provider options from project XML 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Sep 11, 2006
 * @version 1.0
 */
public class OWSProviderReader extends XMLReader implements XMLModuleReader
{
    
    public OWSProviderReader()
    {
        
    }
    
    
    public Object read(DOMReader dom, Element providerElt)
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

}
