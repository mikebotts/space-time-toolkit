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

import org.vast.ows.OWSLayerCapabilities;


/**
 * <p><b>Title:</b><br/>
 * Service Layer Descriptor
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Describe a given service layer capabilities
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 2, 2005
 * @version 1.0
 */
public class ServiceDataSet extends AbstractResource
{
	protected String layerID;
	protected Service service;
	protected OWSLayerCapabilities capabilities;
	

	public String getLayerID()
	{
		return layerID;
	}


	public void setLayerID(String layerID)
	{
		this.layerID = layerID;
	}


	public OWSLayerCapabilities getCapabilities()
	{
		return capabilities;
	}


	public void setCapabilities(OWSLayerCapabilities capabilities)
	{
		this.capabilities = capabilities;
	}


	public Service getService()
	{
		return service;
	}


	public void setService(Service service)
	{
		this.service = service;
	}
}
