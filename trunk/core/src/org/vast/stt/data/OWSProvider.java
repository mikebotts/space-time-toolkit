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

import org.vast.ows.OWSQuery;


/**
 * <p><b>Title:</b><br/>
 * OWS Data Provider
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Common interface for all data providers implementing a
 * client for OGC/OWS data services (WMS, WFS, WCS, SOS).
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 21, 2005
 * @version 1.0
 */
public interface OWSProvider
{
    
	public OWSQuery getQuery();
    
    
    public void setQuery(OWSQuery query);
    
    
    public void createDefaultQuery();
}
