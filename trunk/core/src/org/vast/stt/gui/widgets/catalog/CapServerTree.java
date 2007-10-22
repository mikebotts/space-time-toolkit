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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
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
 * <p>Copyright (c) 2007</p>
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

