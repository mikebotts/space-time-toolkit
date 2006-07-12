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

package org.vast.stt.data;

import java.io.IOException;

import org.ogc.cdm.common.CDMException;
import org.ogc.cdm.common.DataComponent;
import org.ogc.cdm.common.DataEncoding;
import org.ogc.cdm.reader.DataStreamParser;
import org.vast.cdm.reader.URIStreamHandler;
import org.vast.stt.readers.SWEResourceReader;


/**
 * <p><b>Title:</b><br/>
 * SWE Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO SWEDataProvider type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 22, 2005
 * @version 1.0
 */
public class SWEProvider extends AbstractProvider
{
    protected String sweDataUrl;
    protected String rawDataUrl;
    protected String format; 
    protected DataStreamParser dataParser;
	protected SWEDataHandler dataHandler;
	
	
	public SWEProvider()
	{
		dataHandler = new SWEDataHandler();
	}
	
	
	@Override
	public void updateData() throws DataException
	{
		String uri = sweDataUrl;
		
		try
		{
			dataStream = URIStreamHandler.openStream(uri);
		}
		catch (CDMException e)
		{
			throw new DataException(e.getMessage());
		}
		
		// check that format is 'SWE'
		if (!format.equalsIgnoreCase("SWE"))
			throw new DataException("Invalid format: " + format);
		
		// parse response
		try
		{
			SWEResourceReader reader = new SWEResourceReader();
			reader.parse(dataStream);
						
			// display data structure and encoding
			dataParser = reader.getDataParser();
			DataComponent dataInfo = dataParser.getDataComponents();
			DataEncoding dataEnc = dataParser.getDataEncoding();
			System.out.println(dataInfo);
			System.out.println(dataEnc);
			
			// create data node if needed
			if (cachedData == null)
				cachedData = new DataNode();
			
			// clean up old data			
            cachedData.clearAll();
            BlockList blockList = cachedData.createList(dataInfo.copy());
			
			// register the CDM data handler
			dataHandler.setBlockList(blockList);
			dataParser.setDataHandler(dataHandler);
            
            // override resultUri if specified in data set
            String resultUri = rawDataUrl;
            if (resultUri != null)
                reader.setResultUri(resultUri);
			
			// start parsing if not cancelled
			if (!canceled)
			    dataParser.parse(reader.getDataStream());
		}
		catch (CDMException e)
		{
			throw new DataException("Error while parsing resource stream: " + uri, e);
		}
		finally
		{
			try
			{
				dataStream.close();
                dataParser = null;
			}
			catch (IOException e)
			{
				throw new DataException("Error while closing resource stream: " + uri);
			}
		}
	}
	
	
	@Override
	public void cancelUpdate()
	{
		dataParser.stop();
        dataParser = null;
		super.cancelUpdate();
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
