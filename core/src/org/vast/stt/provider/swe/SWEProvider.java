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

import java.io.IOException;

import org.vast.cdm.common.CDMException;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataEncoding;
import org.vast.cdm.common.DataStreamParser;
import org.vast.cdm.reader.URIStreamHandler;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.DataException;
import org.vast.stt.provider.AbstractProvider;


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
		dataHandler = new SWEDataHandler(this);
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
            reader.parse(dataStream);
                        
            // display data structure and encoding
            DataComponent dataInfo = reader.getDataComponents();
            DataEncoding dataEnc = reader.getDataEncoding();
            System.out.println(dataInfo);
            System.out.println(dataEnc);
            
            // create BlockList
            BlockList blockList = dataNode.createList(dataInfo.copy());
            dataNode.setNodeStructureReady(true);
            dataHandler.setBlockList(blockList);
        }
        catch (CDMException e)
        {
            throw new DataException("Error while parsing resource stream: " + sweDataUrl, e);
        }
        finally
        {
            try
            {
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
		    // parse response
            SWEResourceReader reader = new SWEResourceReader();
            dataStream = URIStreamHandler.openStream(sweDataUrl);
			reader.parse(dataStream);
			dataParser = reader.getDataParser();
						
			// register the CDM data handler
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
			throw new DataException("Error while parsing resource stream: " + sweDataUrl, e);
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
            if (dataParser != null)
                dataParser.stop();
            
            if (dataStream != null)
                dataStream.close();
            
            dataStream = null;
            dataParser = null;
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
