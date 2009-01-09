package org.vast.sttx.jpip;

import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;


public class JPIPProviderReader extends XMLReader implements XMLModuleReader
{

	public Object read(DOMHelper dom, Element providerElt)
	{
		JPIPProvider provider = new JPIPProvider();

		// read server url
		String serverUrl = dom.getElementValue(providerElt, "server");		
		provider.setServer(serverUrl);
		
		// read target path
		String target = dom.getElementValue(providerElt, "target");      
        provider.setTarget(target);
        
        // read components
        
        // read footprint
        String[] coordinates = dom.getElementValue(providerElt, "footprint").split(" ");
        float[][] corners = new float[coordinates.length/2][2];
        for (int n=0; n<corners.length; n++)
        {
        	corners[n][0] = Float.parseFloat(coordinates[n*2+1]);
        	corners[n][1] = Float.parseFloat(coordinates[n*2]);
        }
        provider.setFootprint(corners);

		return provider;
	}
}

