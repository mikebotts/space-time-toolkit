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

package org.vast.stt.project;


/**
 * <p><b>Title:</b><br/>
 * Data File Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Describe a given data file contents
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class StaticDataSet extends AbstractResource
{
	protected String sweDataUrl;
    protected String rawDataUrl;
	protected String format;    


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
