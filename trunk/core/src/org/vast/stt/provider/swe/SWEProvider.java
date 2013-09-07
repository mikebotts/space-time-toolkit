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

import java.io.IOException;
import java.io.InputStream;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataEncoding;
import org.vast.stt.data.DataException;
import org.vast.stt.provider.AbstractProvider;
import org.vast.sweCommon.URIStreamHandler;


/**
 * <p><b>Title:</b><br/>
 * SWE Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO SWEDataProvider type description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 22, 2005
 * @version 1.0
 */
public class SWEProvider extends AbstractProvider
{
    protected String sweDataUrl;
    protected String rawDataUrl;
    protected String format; 
    protected InputStream dataStream;
    
	
	public SWEProvider()
	{
	}
    
    
    @Override
    public void init() throws DataException
    {
        try
        {
            // check that format is 'SWE'
            if (!format.equalsIgnoreCase("SWE"))
                throw new DataException("Invalid format: " + format);
            
            // parse response
            SWEResourceReader reader = new SWEResourceReader();
            dataStream = URIStreamHandler.openStream(sweDataUrl);            
            reader.parse(dataStream, null);
                        
            // display data structure and encoding
            DataComponent dataInfo = reader.getDataComponents();
            DataEncoding dataEnc = reader.getDataEncoding();
            System.out.println(dataInfo);
            System.out.println(dataEnc);
            
            // create BlockList
            dataNode.createList(dataInfo.copy());
            dataNode.setNodeStructureReady(true);
        }
        catch (IOException e)
        {
            throw new DataException("Error while parsing resource stream: " + sweDataUrl, e);
        }
        finally
        {
            try
            {
                if (dataStream != null)
                    dataStream.close();
            }
            catch (IOException e)
            {
                throw new DataException("Error while closing resource stream: " + sweDataUrl);
            }
        }
    }
	
	
	@Override
	public void updateData() throws DataException
	{
	    // init DataNode if not done yet
        if (!dataNode.isNodeStructureReady())
            init();
        
        try
		{
		    // read xml response
            SWEResourceReader reader = new SWEResourceReader();
            dataStream = URIStreamHandler.openStream(sweDataUrl);
									
			// create data handler
            SWEDataHandler dataHandler = new SWEDataHandler(this, dataNode.getListArray().get(0));
                        
            // override resultUri if specified in data set
            String resultUri = rawDataUrl;
            if (resultUri != null)
                reader.setResultUri(resultUri);
			
			// start parsing data if not cancelled
			if (!canceled)
			    reader.parse(dataStream, dataHandler);
		}
		catch (IOException e)
		{
			throw new DataException("Error while parsing resource stream: " + sweDataUrl, e);
		}
		finally
		{
			try
			{
				if (dataStream != null)
				    dataStream.close();
			}
			catch (IOException e)
			{
				throw new DataException("Error while closing resource stream: " + sweDataUrl);
			}
		}
	}
	
	
	@Override
	public void cancelUpdate()
	{
	    canceled = true;
        
        // close stream(s)
        try
        {           
            if (dataStream != null)
                dataStream.close();
            
            dataStream = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}


	public boolean isSpatialSubsetSupported()
	{
		return false;
	}


	public boolean isTimeSubsetSupported()
	{
		return false;
	}
    
    
    public String getFormat()
    {
        return format;
    }


    public void setFormat(String format)
    {
        this.format = format;
    }


    public String getRawDataUrl()
    {
        return rawDataUrl;
    }


    public void setRawDataUrl(String rawDataUrl)
    {
        this.rawDataUrl = rawDataUrl;
    }


    public String getSweDataUrl()
    {
        return sweDataUrl;
    }


    public void setSweDataUrl(String sweDataUrl)
    {
        this.sweDataUrl = sweDataUrl;
    }
}
