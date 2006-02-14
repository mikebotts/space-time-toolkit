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

package org.vast.stt.readers;

import java.io.*;

import org.ogc.cdm.common.CDMException;
import org.vast.cdm.reader.*;
import org.vast.io.xml.*;
import org.w3c.dom.*;


public class SWEResourceReader extends CDMReader
{
	String resultUri;
	CDMFilter streamFilter;
	
	
	public void parse(InputStream inputStream) throws CDMException
	{
		try
		{
			streamFilter = new CDMFilter(inputStream);
			streamFilter.setDataElementName("data");
			
//			int val;
//			try
//			{
//				do
//				{
//					val = streamFilter.read();
//					System.out.print((char)val);				
//				}
//				while (val != -1);
//			}
//			catch (IOException e){}
//			System.exit(0);
			
			// parse xml header using DataComponent and DataEncoding readers
			DOMReader domReader = new DOMReader(streamFilter, false);			
			Element dataElt = domReader.getBaseElement();
			Element defElt = domReader.getElement(dataElt, "dataComponents");
			Element encElt = domReader.getElement(dataElt, "encoding");
			
			DataComponentsReader infReader = new DataComponentsReader(domReader);
			EncodingReader encReader = new EncodingReader(domReader);	
			this.dataComponents = infReader.readComponentProperty(defElt);
			this.dataEncoding = encReader.readEncodingProperty(encElt);
			
			// read external link if present
			resultUri = domReader.getAttributeValue(dataElt, "data/externalLink");
		}
		catch (DOMReaderException e)
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
}
