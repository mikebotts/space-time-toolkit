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

package org.vast.stt.provider.ows;

import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.OWSRequest;
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
 * <p>Copyright (c) 2007</p>
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
    
    
	public abstract OWSRequest getQuery();
    public abstract void setQuery(OWSRequest query);
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
