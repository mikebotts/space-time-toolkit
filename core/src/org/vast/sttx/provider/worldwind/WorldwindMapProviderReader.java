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

package org.vast.sttx.provider.worldwind;

import org.vast.xml.DOMHelper;
import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.w3c.dom.Element;


public class WorldwindMapProviderReader extends XMLReader implements XMLModuleReader
{

    public WorldwindMapProviderReader()
    {

    }


    public Object read(DOMHelper dom, Element providerElt)
    {
        WorldwindMapProvider provider = new WorldwindMapProvider();
        
        String layerId = dom.getElementValue(providerElt, "layerId");
        if (layerId != null)
            ((WorldwindMapProvider)provider).setLayer(layerId);
        
        return provider;
    }
}
