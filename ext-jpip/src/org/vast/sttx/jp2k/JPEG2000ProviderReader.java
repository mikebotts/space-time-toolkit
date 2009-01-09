package org.vast.sttx.jp2k;

import org.vast.stt.project.XMLModuleReader;
import org.vast.stt.project.XMLReader;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;

public class JPEG2000ProviderReader extends XMLReader implements XMLModuleReader {

	public Object read(DOMHelper dom, Element providerElt)
	{
		JPEG2000Provider provider = new JPEG2000Provider();

		// read min/max pressure values
		String localFile = dom.getElementValue(providerElt, "localFile");
		
		((JPEG2000Provider)provider).setImagePath(localFile);
		
//		if (min != null)
//			((PhenomenaDetectionProvider)provider).setMinPressure(Double.parseDouble(min));
//
//		String max = dom.getElementValue(providerElt, "maxPressure");
//		if (max != null)
//			((PhenomenaDetectionProvider)provider).setMaxPressure(Double.parseDouble(max));

		return provider;
	}
}

