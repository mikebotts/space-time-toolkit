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

package org.vast.stt.provider.sml;

import org.vast.io.xml.DOMReader;
import org.vast.process.DataProcess;
import org.vast.process.IOSelector;
import org.vast.sensorML.reader.SystemReader;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class SMLProviderReader extends XMLReader implements XMLModuleReader
{

    public SMLProviderReader()
    {
    }


    public Object read(DOMReader dom, Element providerElt)
    {
        SMLProvider provider = new SMLProvider();
        
        try
        {
            // read process chain using SML SystemReader
            Element processElt = dom.getElement(providerElt, "process");
            SystemReader systemReader = new SystemReader(dom);
            systemReader.setReadMetadata(false);
            systemReader.setCreateExecutableProcess(true);
            DataProcess process = systemReader.readProcessProperty(processElt);
            provider.setProcess(process);
            
            // read custom values and assign them to chain signals
            NodeList valueElts = dom.getElements(providerElt, "value");
            for (int i=0; i<valueElts.getLength(); i++)
            {
                Element nextElt = (Element)valueElts.item(i); 
                String dataPath = dom.getAttributeValue(nextElt, "@path");
                IOSelector selector = new IOSelector(process.getParameterList(), dataPath);
                String value = dom.getElementValue(nextElt, "");
                selector.getComponent().getData().setStringValue(value);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return provider;
    }

}
