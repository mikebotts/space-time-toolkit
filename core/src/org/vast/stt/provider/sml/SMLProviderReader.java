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

package org.vast.stt.provider.sml;

import org.vast.xml.DOMHelper;
import org.vast.process.DataProcess;
import org.vast.process.IOSelector;
import org.vast.process.ProcessChain;
import org.vast.sensorML.SMLUtils;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class SMLProviderReader extends XMLReader implements XMLModuleReader
{
    protected SMLUtils smlUtils = new SMLUtils();
    
    
    public SMLProviderReader()
    {
    }


    public Object read(DOMHelper dom, Element providerElt)
    {
    	SMLProvider provider = new SMLProvider();
        
        try
        {
            // read process chain using SML SystemReader
            Element processElt = dom.getElement(providerElt, "process/*");
            smlUtils.setReadMetadata(true);
            smlUtils.setCreateExecutableProcess(true);
            DataProcess rootProcess = smlUtils.readProcess(dom, processElt);
            provider.setProcess(rootProcess);
            
            // read persistency of data
            String persistency = dom.getAttributeValue(providerElt, "dataPersistency");
            if (persistency != null)
                provider.setPersistency(Integer.parseInt(persistency));

            // read custom values and assign them to chain signals
            NodeList valueElts = dom.getElements(providerElt, "value");
            for (int i=0; i<valueElts.getLength(); i++)
            {
                Element nextElt = (Element)valueElts.item(i);
                String processPath = dom.getAttributeValue(nextElt, "@process");
                String dataPath = dom.getAttributeValue(nextElt, "@path");
                DataProcess process = rootProcess;
                
                // find process recursively
                if (processPath != null)
                {
                    String[] processNames = processPath.split("/");                
                    for (int p=0; p<processNames.length; p++)
                        process = ((ProcessChain)process).getProcess(processNames[p]);
                }
                
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
