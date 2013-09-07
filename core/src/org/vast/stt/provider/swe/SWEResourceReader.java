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

package org.vast.stt.provider.swe;

import java.io.*;
import org.vast.cdm.common.DataHandler;
import org.vast.sweCommon.SWEFilter;
import org.vast.sweCommon.SWEReader;
import org.vast.sweCommon.SWECommonUtils;
import org.vast.sweCommon.URIStreamHandler;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLReaderException;
import org.w3c.dom.*;


public class SWEResourceReader extends SWEReader
{
	protected String resultUri;
    protected SWEFilter streamFilter;
	
	
	public void parse(InputStream inputStream, DataHandler handler) throws IOException
	{
		try
		{
			dataHandler = handler;
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
			this.dataParser = createDataParser();
			
			// Override external link if present; otherwise, don't change it (we have a set method for this variable, too.
			//  The value that gets set was being overriden here and reset to null ater SweProvider set it.
			String resUriTmp = dom.getAttributeValue(dataElt, "data/externalLink");
			if(resUriTmp != null)
				resultUri = resUriTmp;
			
			// launch data stream parser if handler was provided
            if (dataHandler != null)
            {
                dataParser.setDataHandler(dataHandler);
                dataParser.parse(getDataStream());
            }
		}
		catch (DOMHelperException e)
		{
			throw new XMLReaderException("Error while parsing Observation XML", e);
		}
	}
	
	
	public InputStream getDataStream() throws IOException
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
