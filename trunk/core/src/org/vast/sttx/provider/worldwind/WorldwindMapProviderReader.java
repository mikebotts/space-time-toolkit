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

package org.vast.sttx.provider.worldwind;

import org.vast.io.xml.DOMReader;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.w3c.dom.Element;


public class WorldwindMapProviderReader extends XMLReader implements XMLModuleReader
{

    public WorldwindMapProviderReader()
    {

    }


    public Object read(DOMReader dom, Element providerElt)
    {
        WorldwindMapProvider provider = new WorldwindMapProvider();
        
        String layerId = dom.getElementValue(providerElt, "layerId");
        if (layerId != null)
            ((WorldwindMapProvider)provider).setLayer(layerId);
        
        return provider;
    }
}
