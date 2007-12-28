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
    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.catalog;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
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

public class CapServers
{
	//  server lists per service type
	Hashtable<String, List<ServerInfo>> allServers;


	public CapServers()
	{
		//  Init table
		allServers = new Hashtable<String, List<ServerInfo>>();
	}


	public String[] getServiceTypes()
	{
		return allServers.keySet().toArray(new String[0]);
	}


	public String[] getServers(String type)
	{
		String[] serverNames = null;
		List<ServerInfo> servers = allServers.get(type);
		
		if (servers != null)
		{
			serverNames = new String[servers.size()];
			for (int i=0; i<servers.size(); i++)
				serverNames[i] = servers.get(i).name;
		}

		return serverNames;
	}


	public ServerInfo getServerInfo(String serverName, String type)
	{
		List<ServerInfo> servers = allServers.get(type);
		
		if (servers != null)
		{
			for (ServerInfo info : servers)
			{
				if (info.name.equals(serverName))
					return info;
			}
		}
		
		return null;
	}


	public void loadDefaultServerData()
	{
		try
		{
			String fileLocation = null;
			Enumeration e = STTPlugin.getDefault().getBundle().findEntries("conf", "Servers.xml", false);
			
			if (e.hasMoreElements())
				fileLocation = (String) e.nextElement().toString();

			if (fileLocation == null)
			{
				ExceptionSystem.display(new Exception("STT error: Cannot find conf\\Servers.xml"));
				return;
			}
			
			loadServers(fileLocation, false);
		}
		catch (DOMHelperException e)
		{
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "STT Error", "Servers.xml file not found.  Capabilities Servers are empty.");
			return;
		}
	}


	public void loadServers(String fileLocation, boolean append) throws DOMHelperException
	{
		DOMHelper dom = new DOMHelper(fileLocation, false);
		Element rootElt = dom.getRootElement();

		NodeList serverNodes = rootElt.getElementsByTagName("Server");
		int numServers = serverNodes.getLength();
		ServerInfo si;

		for (int i = 0; i < numServers; i++)
		{
			Element serverElt = (Element) serverNodes.item(i);
			si = new ServerInfo();
			si.name = serverElt.getAttribute("name");
			si.version = serverElt.getAttribute("version");
			si.type = serverElt.getAttribute("type");
			Element urlElt = (Element) serverElt.getElementsByTagName("URL").item(0);
			si.url = urlElt.getTextContent();
			
			//  Add ServerInfo to List
			List<ServerInfo> serverGroup = allServers.get(si.type);
			
			// create and register list in table if first of this type
			if (serverGroup == null) 
			{
				serverGroup = new ArrayList<ServerInfo>();
				allServers.put(si.type, serverGroup);
			}
			
			// add this server to list
			serverGroup.add(si);
		}
	}


	//  TODO rewrite cap file if changes are made
	public void writeServerData()
	{

	}
}
