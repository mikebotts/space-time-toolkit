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

package org.vast.stt.provider.ows;

import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.OWSQuery;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.OWSUtils;
import org.vast.stt.project.Service;
import org.vast.stt.provider.AbstractProvider;


/**
 * <p><b>Title:</b><br/>
 * OWS Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Common abstract class for all data providers implementing a
 * client for OGC/OWS data services (WMS, WFS, WCS, SOS).
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public abstract class OWSProvider extends AbstractProvider
{
    protected String layerID;
    protected Service service;
    protected OWSServiceCapabilities serviceCaps;
    protected OWSUtils owsUtils = new OWSUtils();
    
    
	public abstract OWSQuery getQuery();
    public abstract void setQuery(OWSQuery query);
    public abstract void createDefaultQuery();
    public abstract OWSLayerCapabilities getLayerCapabilities();
    
    
    public OWSServiceCapabilities getServiceCapabilities()
    {
        return this.serviceCaps;
    }
    
    
    public void setServiceCapabilities(OWSServiceCapabilities serviceCaps)
    {
        this.serviceCaps = serviceCaps;
    }
    

    public String getLayerID()
    {
        return this.layerID;
    }


    public void setLayerID(String layerID)
    {
        this.layerID = layerID;
    }


    public Service getService()
    {
        return this.service;
    }


    public void setService(Service service)
    {
        this.service = service;
    }
}
