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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * <p><b>Title:</b>
 *  CapServerTree
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Tree to display and edit Cap Servers
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapServerTree// extends TreeViewer 
{
	TreeViewer treeViewer;
	List<ServerInfo> wmsServers, wcsServers, wfsServers, sosServers;
	List [] servers;
	
	public CapServerTree(Composite parent){
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		loadServerLists();
		treeViewer.setInput(servers);
		treeViewer.expandAll();	
	}  
	
	public void setLayoutData(GridData gd){
		treeViewer.getControl().setLayoutData(gd);
	}
	
	// Label + Image provider
	class TreeLabelProvider extends LabelProvider
	{        
//        @Override
//        public Image getImage(Object element)
//		{
//			if (element instanceof WMSServer)
//                return WMSServerImg;
//			else if (element instanceof SOSServer)
//                return SOSServerImg;
//		}

		@Override
		public String getText(Object element)
		{
			if(element instanceof ArrayList) {
				ServerInfo serverInfo = ((ArrayList<ServerInfo>)element).get(0);
				if(serverInfo != null)
					return serverInfo.type.toString();
				return null;
			} else if (element instanceof ServerInfo)
				return ((ServerInfo)element).serverName;
			else 
				return null;
		}		
	}
	
	// Content provider
	class TreeContentProvider implements ITreeContentProvider
	{
		public void dispose()
		{						
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof List[]) {
				return (List [])parentElement;
			} else if(parentElement instanceof ArrayList) {
				return ((ArrayList)parentElement).toArray(new ServerInfo[]{});
			} else
				return null;
			
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			if (element instanceof List)
				return true;
			else
				return false;
		}

		public Object[] getElements(Object inputElement)
		{
			return getChildren(inputElement);
		}		
	}
	
	public void loadServerLists(){
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

		servers = new List[2];
		servers[0] = wmsServers;
		servers[1] = sosServers;
	}
}

