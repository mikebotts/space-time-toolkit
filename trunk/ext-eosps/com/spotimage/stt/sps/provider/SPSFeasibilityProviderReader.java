/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit SPS Plugin".
 
 The Initial Developer of the Original Code is Spotimage S.A.
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Alexandre Robin <alexandre.robin@spotimage.fr> for more
 information.
 
 Contributor(s): 
    Alexandre Robin <alexandre.robin@spotimage.fr>
 
******************************* END LICENSE BLOCK ***************************/

package com.spotimage.stt.sps.provider;

import org.vast.xml.DOMHelper;
import org.vast.ows.OWSRequest;
import org.vast.ows.OWSUtils;
import org.vast.ows.sps.DescribeTaskingRequest;
import org.vast.stt.project.XMLReader;
import org.vast.stt.project.XMLModuleReader;
import org.w3c.dom.Element;


/**
 * <p><b>Title:</b>
 * SPS Feasibility Provider Reader
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Read SPS Feasibility Provider options from project XML 
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Mar 11, 2008
 * @version 1.0
 */
public class SPSFeasibilityProviderReader extends XMLReader implements XMLModuleReader
{
    protected OWSUtils owsUtils = new OWSUtils();
    
    
    public SPSFeasibilityProviderReader()
    {        
    }
    
    
    public Object read(DOMHelper dom, Element providerElt)
    {
        SPSFeasibilityProvider provider = new SPSFeasibilityProvider();
        
        try
        {
        	// read request
        	Element requestElt = dom.getElement(providerElt, "request/*");
        	
        	// parse request
            OWSRequest request = owsUtils.readXMLQuery(dom, requestElt);
            request.setGetServer(dom.getAttributeValue(providerElt, "request/@getUrl"));
            request.setPostServer(dom.getAttributeValue(providerElt, "request/@postUrl"));
            provider.setDescribeTaskingRequest((DescribeTaskingRequest)request);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return provider;
    }

}
