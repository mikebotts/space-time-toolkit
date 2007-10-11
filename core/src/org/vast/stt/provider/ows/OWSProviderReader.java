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

import org.vast.xml.DOMHelper;
import org.vast.ows.OWSException;
import org.vast.ows.OWSRequest;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.OWSUtils;
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
    protected OWSUtils owsUtils = new OWSUtils();
    
    
    public OWSProviderReader()
    {        
    }
    
    
    public Object read(DOMHelper dom, Element providerElt)
    {
        OWSProvider provider = null;
        
        // get request type
        Element requestElt = dom.getElement(providerElt, "request/*");
        Element capsElt = dom.getElement(providerElt, "capabilities/*");
        
        try
        {
            // parse request
            OWSRequest query = owsUtils.readXMLQuery(dom, requestElt);
            query.setGetServer(dom.getAttributeValue(providerElt, "request/@getUrl"));
            query.setPostServer(dom.getAttributeValue(providerElt, "request/@postUrl"));
            
            // instantiate provider
            String serviceType = query.getService();
            if (serviceType.equals("SOS"))
                provider = new SOSProvider();                           
            
            provider.setQuery(query);
            
            // parse capabilities if present
            if (capsElt != null)
            {
                OWSServiceCapabilities caps = owsUtils.readCapabilities(dom, capsElt, query.getService());
                provider.setServiceCapabilities(caps);
            }
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
