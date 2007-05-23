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

package org.vast.sttx.provider.smart;

import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;


public class PhenomenaDetectionProviderReader extends XMLReader implements XMLModuleReader
{
    
    public PhenomenaDetectionProviderReader()
    {
        
    }
    
    
    public Object read(DOMHelper dom, Element providerElt)
    {
        PhenomenaDetectionProvider provider = new PhenomenaDetectionProvider();
        
        // read min/max pressure values
        String min = dom.getElementValue(providerElt, "minPressure");
        if (min != null)
            ((PhenomenaDetectionProvider)provider).setMinPressure(Double.parseDouble(min));
        
        String max = dom.getElementValue(providerElt, "maxPressure");
        if (max != null)
            ((PhenomenaDetectionProvider)provider).setMaxPressure(Double.parseDouble(max));
        
        return provider;
    }

}
