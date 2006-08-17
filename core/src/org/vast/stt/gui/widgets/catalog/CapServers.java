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

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Title:</b>
 *  CapServers
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  CapServers holds all the server Info for a user
 *  
 *  TODO  Ensure that capServers stays synched between editCapServDialog.capServerTree
 *        and CapabilitiesWidget.  Should just be a matter of forcing CapServer reread 
 *        every time a change is made in editCapServerDialog
 *  
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapServers {
	List<ServerInfo> wmsServers, wcsServers, wfsServers, sosServers;
	public enum ServiceType { WMS, WCS, WFS, SOS };
	
	public CapServers(){
		
	}
	
	//  TODO load from config XML
	public void loadServerData(){
		ServerInfo server1 = new ServerInfo();
		server1.serverName="Some WMS";
		server1.serverUrl="http://blahblahvlagh";
		server1.type = ServerInfo.ControlType.WMS;
		ServerInfo server2 = new ServerInfo();
		server2.serverName="Another WMS";
		server2.serverUrl="http://bobs.adult.movies";
		server2.type = ServerInfo.ControlType.WMS;
		
		wmsServers = new ArrayList<ServerInfo>();
		wmsServers.add(server1);
		wmsServers.add(server2);
		
		ServerInfo server3 = new ServerInfo();
		server3.serverName="Some SOS";
		server3.serverUrl="http://sos.sos.sos";
		server3.type = ServerInfo.ControlType.SOS;
		ServerInfo server4 = new ServerInfo();
		server4.serverName="UAh SOS";
		server4.serverUrl="http://vast.uah.edu";
		server4.type = ServerInfo.ControlType.SOS;
		
		sosServers = new ArrayList<ServerInfo>();
		sosServers.add(server3);
		sosServers.add(server4);
	}	
	
	public String [] getServiceTypes(){
		ServiceType[] types = ServiceType.values();
		String [] typesStr = new String[types.length];
		for(int i =0; i<types.length; i++)
			typesStr[i] = types[i].toString();
		
		return typesStr;
	}
	
	public String [] getServers(String type){
		for(ServiceType t : ServiceType.values()){
			if(type.equals(t.toString())) 
				return getServers(t);	
		}
		return null;
	}
	
	public String [] getServers(ServiceType type){
		int i = 0;
		String [] servers = null;
		switch(type){
		case WMS:
			servers = new String[wmsServers.size()];
			for(ServerInfo info : wmsServers)
				servers[i++] = info.serverName;
			break;
		case WCS:
			break;
		case WFS:
			break;
		case SOS:
			servers = new String[sosServers.size()];
			for(ServerInfo info : sosServers)
				servers[i++] = info.serverName;
			break;
		default:
			System.err.println("CapServes.getServers();  Type not supported:" + type);
		}
		return servers;
	}
}

