
package org.vast.stt.gui.views;

import java.util.*;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ISharedImages;
import org.vast.stt.apps.STTConfig;
import org.vast.stt.project.Service;


public class ServiceTreeView extends ViewPart
{
	public static final String ID = "STT.ServiceTreeView";
	private Tree serviceTree;


	@Override
	public void createPartControl(Composite parent)
	{
		serviceTree = new Tree(parent, SWT.SINGLE);
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
		IAction action1 = new Action()
		{
			public void run()
			{
				
			}
		};		
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
		site.getActionBars().getToolBarManager().add(action1);
		
		// register listener
		//STTEventManager.getInstance().addResourceEventListener(this);
	}
	
	
	@Override
	public void dispose()
	{
		// de register listener
		//STTEventManager.getInstance().removeResourceEventListener(this);
	}
	
	
	@Override
	public void setFocus()
	{
		serviceTree.setFocus();
	}


	public void resourceAdded()
	{
		if (serviceTree == null)
			return;
		
		Runnable updateTree = new Runnable()
		{
			public void run()
			{
				Hashtable<String, TreeItem> treeEntries = new Hashtable<String, TreeItem>();
				serviceTree.removeAll();
				TreeItem rootItem = new TreeItem(serviceTree, SWT.NONE);
				rootItem.setText(0, "Services");
								
				ArrayList<Service> servList = STTConfig.getInstance().getCurrentProject().getServiceList();
				
				if (servList != null)
				{
					for (int i=0; i<servList.size(); i++)
		        	{
						Service service = servList.get(i);
						String serviceType = service.getType();
						TreeItem catItem;
						
						if (!treeEntries.containsKey(serviceType))
						{
							catItem = new TreeItem(rootItem, SWT.NONE);
							catItem.setText(0, serviceType);
							treeEntries.put(serviceType, catItem);
						}
						else
						{
							catItem = treeEntries.get(serviceType);
						}
						
						TreeItem newItem = new TreeItem(catItem, SWT.NONE);
						newItem.setText(service.getName());
		        	}
				}
			}
		};
		
		serviceTree.getDisplay().asyncExec(updateTree);
	}
}