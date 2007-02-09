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

package org.vast.stt.provider.swe;

import java.io.*;
import org.vast.cdm.common.CDMException;
import org.vast.sweCommon.SWEFilter;
import org.vast.sweCommon.SWEReader;
import org.vast.sweCommon.SWECommonUtils;
import org.vast.sweCommon.URIStreamHandler;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.*;


public class SWEResourceReader extends SWEReader
{
	protected String resultUri;
    protected SWEFilter streamFilter;
	
	
	public void parse(InputStream inputStream) throws CDMException
	{
		try
		{
			streamFilter = new SWEFilter(inputStream);
			streamFilter.setDataElementName("data");
					
			// parse xml header using DataComponent and DataEncoding readers
            DOMHelper dom = new DOMHelper(streamFilter, false);			
			Element dataElt = dom.getBaseElement();
			Element defElt = dom.getElement(dataElt, "dataComponents");
			Element encElt = dom.getElement(dataElt, "encoding");
			
            SWECommonUtils utils = new SWECommonUtils();
            this.dataComponents = utils.readComponentProperty(dom, defElt);
            this.dataEncoding = utils.readEncodingProperty(dom, encElt);
			
			// read external link if present
			resultUri = dom.getAttributeValue(dataElt, "data/externalLink");
		}
		catch (DOMHelperException e)
		{
			throw new CDMException("Error while parsing Observation XML", e);
		}
	}
	
	
	public InputStream getDataStream() throws CDMException
	{
		if (resultUri != null)
		{
			try
			{
				streamFilter.startReadingData();
				streamFilter.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return URIStreamHandler.openStream(resultUri);
		}
		else
		{
			streamFilter.startReadingData();
			return streamFilter;
		}
	}


    public String getResultUri()
    {
        return resultUri;
    }


    public void setResultUri(String resultUri)
    {
        this.resultUri = resultUri;
    }
}
