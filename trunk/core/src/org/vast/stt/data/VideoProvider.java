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
import org.vast.cdm.reader.URIStreamHandler;


/**
 * <p><b>Title:</b><br/>
 * Video Provider using JMF
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO VideoProvider type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 22, 2005
 * @version 1.0
 */
public class VideoProvider extends AbstractProvider
{
    protected String videoUrl;
	
	
	public VideoProvider()
	{
	}
	
	
	@Override
	public void updateData() throws DataException
	{
		
		try
		{
			dataStream = URIStreamHandler.openStream(videoUrl);
		}
		catch (CDMException e)
		{
			throw new DataException("Error while reading video stream: " + videoUrl, e);
		}
		finally
		{
			try
			{
				dataStream.close();
			}
			catch (IOException e)
			{
				throw new DataException("Error while closing video stream");
			}
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


    public String getVideoUrl()
    {
        return videoUrl;
    }


    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }
}
