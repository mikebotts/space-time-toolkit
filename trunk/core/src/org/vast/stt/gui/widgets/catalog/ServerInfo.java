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

package org.vast.stt.gui.widgets.catalog;

/**
 * <p><b>Title:</b>
 *  ServerInfo
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Simple class to store server Info for Cap Server Tree
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class ServerInfo {

	public String name;
	public String version;
	public String url;
	public static enum ServiceType 
		{ WMS, WCS, WFS, SOS };
	public ServiceType type;
	
	public void setServiceType(String typeStr){
		if(typeStr.equalsIgnoreCase("wms"))
			type = ServiceType.WMS;
		else if(typeStr.equalsIgnoreCase("wcs"))
			type = ServiceType.WCS;
		else if(typeStr.equalsIgnoreCase("wfs"))
			type = ServiceType.WFS;
		else if(typeStr.equalsIgnoreCase("sos"))
			type = ServiceType.SOS;
	}
}

