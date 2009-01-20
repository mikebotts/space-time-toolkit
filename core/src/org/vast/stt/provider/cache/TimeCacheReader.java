/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.

 The Original Code is the "Space Time Toolkit Cache Engine".
 
 The Initial Developer of the Original Code is Sensia Software LLC.
 Portions created by the Initial Developer are Copyright (C) 2008
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.cache;

import org.vast.xml.DOMHelper;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.w3c.dom.Element;


public class TimeCacheReader extends XMLReader implements XMLModuleReader
{

    public TimeCacheReader()
    {
    }


    public Object read(DOMHelper dom, Element providerElt)
    {
        TimeCache provider = new TimeCache();
        String text;
        
        text = dom.getElementValue(providerElt, "timeData");
        provider.setTimeData(text);
        
        // tile size in seconds
        text = dom.getElementValue(providerElt, "tileSize");
        if (text != null)
            provider.setTileSize(Double.parseDouble(text));
            
        // max cache size in MB
        text = dom.getElementValue(providerElt, "maxCacheSize");
        if (text != null)
            provider.setMaxCacheSize(Integer.parseInt(text));
        
        // max life time in minutes
        text = dom.getElementValue(providerElt, "maxLifeTime");
        if (text != null)
            provider.setMaxLifeTime(Integer.parseInt(text));
        
        return provider;
    }
}
