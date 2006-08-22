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
import org.eclipse.swt.widgets.Control;

/**
 * <p><b>Title:</b>
 *  CapServerTree
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Tree to display and edit Cap Servers
 *  TODO:  tie this to an external XML file containg server info
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapServerTree
{
	TreeViewer treeViewer;
	List [] servers;
	CapServers capServers;
	
	public CapServerTree(Composite parent, CapServers capServers){
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		this.capServers = capServers;
		loadServerLists();
		treeViewer.setInput(servers);
		treeViewer.expandAll();	
	}  
	
	public Control getControl(){
		return treeViewer.getControl();
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
				if( ((List)element).size() == 0)
					return "";
				ServerInfo serverInfo = ((List<ServerInfo>)element).get(0);
				//if(serverInfo != null)
					return serverInfo.type.toString();
				//return null;
			} else if (element instanceof ServerInfo)
				return ((ServerInfo)element).name;
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
		
		// test hack
		servers = new List[2];
		servers[0] = capServers.wmsServers;
		servers[1] = capServers.sosServers;
	}
}

