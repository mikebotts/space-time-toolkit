
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
import org.vast.stt.project.Resource;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;


public class ResourceTreeView extends ViewPart implements STTEventListener
{
	public static final String ID = "STT.ResourceTreeView";
	private Tree dataTree;


	@Override
	public void createPartControl(Composite parent)
	{
		dataTree = new Tree(parent, SWT.SINGLE);
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
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		site.getActionBars().getToolBarManager().add(action1);
		
		// register listener
		STTConfig.getInstance().getEventManager().addResourceListener(this);
	}
	
	
	@Override
	public void dispose()
	{
		// unregister listener
		STTConfig.getInstance().getEventManager().removeResourceListener(this);
	}
	
	
	@Override
	public void setFocus()
	{
		dataTree.setFocus();
	}


	public void handleEvent(STTEvent e)
	{
		if (dataTree == null)
			return;
		
		Runnable updateTree = new Runnable()
		{
			public void run()
			{
				dataTree.removeAll();
				TreeItem rootItem = new TreeItem(dataTree, SWT.NONE);
				rootItem.setText(0, "Resources");
				
				ArrayList<Resource> dataSetList = STTConfig.getInstance().getCurrentProject().getResourceList();
				
				if (dataSetList != null)
				{
					for (int i=0; i<dataSetList.size(); i++)
		        	{
		        		TreeItem newItem = new TreeItem(rootItem, SWT.NONE);
		        		newItem.setText(dataSetList.get(i).getName());
		        		
		        		if (newItem != null)
		        			newItem.setExpanded(true);
		        	}
				}
			}
		};
		
		dataTree.getDisplay().asyncExec(updateTree);
	}
}