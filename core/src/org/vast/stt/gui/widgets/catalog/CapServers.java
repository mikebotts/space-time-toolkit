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
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.catalog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.stt.apps.STTPlugin;
import org.vast.util.ExceptionSystem;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapServers {
	//  May make these HashMaps for mroe efficient lookups
	List<ServerInfo> wmsServers, wcsServers, wfsServers, sosServers;
	//public enum ServiceType { WMS, WCS, WFS, SOS };
	
	public CapServers(){
		//  Init ArrayLists
		wmsServers = new ArrayList<ServerInfo>(5);
		wcsServers = new ArrayList<ServerInfo>(5);
		wfsServers = new ArrayList<ServerInfo>(5);
		sosServers = new ArrayList<ServerInfo>(5);
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
				servers[i++] = info.name;
			break;
		case WCS:
			servers = new String[wcsServers.size()];
			for(ServerInfo info : wcsServers)
				servers[i++] = info.name;
			break;
		case WFS:
			servers = new String[wfsServers.size()];
			for(ServerInfo info : wfsServers)
				servers[i++] = info.name;
			break;
		case SOS:
			servers = new String[sosServers.size()];
			for(ServerInfo info : sosServers)
				servers[i++] = info.name;
			break;
		default:
			System.err.println("CapServes.getServers();  Type not supported:" + type);
		}
		return servers;
	}
	
	public ServerInfo getServerInfo(String serverName, ServiceType type){
		switch(type){
		case WMS:
			for(ServerInfo info : wmsServers) {
				if(info.name.equals(serverName))
					return info;
			}
			break;
		case WCS:
			for(ServerInfo info : wcsServers) {
				if(info.name.equals(serverName))
					return info;
			}
			break;
		case WFS:
			for(ServerInfo info : wfsServers) {
				if(info.name.equals(serverName))
					return info;
			}
			break;
		case SOS:
			for(ServerInfo info : sosServers) {
				if(info.name.equals(serverName))
					return info;
			}
			break;
		default:
			System.err.println("CapServes.getServers();  Type not supported:" + type);
			return null;
		}
		System.err.println("CapServes.getServers();  Info not found for ServerName:" + serverName);
		return null;
	}
	
	public void loadServerData() {
		try	{
		    readServers();
		} catch (DOMHelperException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"STT Error", "Servers.xml file not found.  Capabilities Servers are empty.");
			return;
		}
	}	
	
	public void readServers() throws DOMHelperException {
	   String fileLocation = null;
	   Enumeration e = STTPlugin.getDefault().getBundle().findEntries("conf", "Servers.xml", false);
	   if (e.hasMoreElements())
           fileLocation = (String)e.nextElement().toString();
	   
	   if(fileLocation == null) {
		   ExceptionSystem.display(new Exception("STT error: Cannot find conf\\Servers.xml"));
		   return;
	   }		
		
        DOMHelper dom = new DOMHelper(fileLocation, false);
		Element rootElt = dom.getRootElement();
		
		NodeList serverNodes = rootElt.getElementsByTagName("Server");
		Element server, urlElt;
		int numServers = serverNodes.getLength();
		ServerInfo si;
		String typeStr;
		for(int i=0; i<numServers; i++){
			server = (Element)serverNodes.item(i);
			si = new ServerInfo();
			si.name = server.getAttribute("name");
			si.version = server.getAttribute("version");
			typeStr = server.getAttribute("type");
			si.type = ServiceType.getServiceType(typeStr);
			urlElt = (Element)server.getElementsByTagName("URL").item(0);
			si.url = urlElt.getTextContent();
			//  Add ServerInfo to List
			switch(si.type){
			case WMS:
				wmsServers.add(si);
				break;
			case WCS:
				wcsServers.add(si);
				break;
			case WFS:
				wfsServers.add(si);
				break;
			case SOS:
				sosServers.add(si);
				break;
			}
		}
	}		
	
	//  TODO rewrite cap file if changes are made
	public void writeServerData(){
		
	}
}

