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

package org.vast.stt.provider.swe;

import org.vast.xml.DOMHelper;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.w3c.dom.Element;


public class SWEProviderReader extends XMLReader implements XMLModuleReader
{

    public SWEProviderReader()
    {
        
    }
    
    
    public Object read(DOMHelper dom, Element providerElt)
    {
        SWEProvider provider = new SWEProvider();
        
        // format and url
        provider.setFormat(dom.getElementValue(providerElt, "format"));
        provider.setSweDataUrl(dom.getAttributeValue(providerElt, "sweData/@href"));
        provider.setRawDataUrl(dom.getAttributeValue(providerElt, "rawData/@href"));
        
        return provider;
    }
}
