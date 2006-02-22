package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.data.DataProvider;
import org.vast.stt.gui.widgets.time.TimeExtentWidget;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataItem;

/**
 * <p><b>Title:</b><br/>
 * TimeSettingsView
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	View for manipulating the TimeSettings of DataItems.   
 *
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 19, 2005
 * @version 1.0
 * 
 * @TODO  add +/- to Bias
 * @TODO  add control for buttons
 */
public class TimeExtentView extends ViewPart implements ISelectionListener
{	
	public static final String ID = "STT.TimeExtentView";
	private TimeExtentWidget timeSettingsWidget;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		timeSettingsWidget = new TimeExtentWidget(parent); 
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);		
	}
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		if (part instanceof SceneTreeView)
		{
			DataEntry selectedItem = (DataEntry)((IStructuredSelection)selection).getFirstElement();
			if(selectedItem instanceof DataItem) { 
				System.err.println("item " + selectedItem);
				DataProvider prov = ((DataItem)selectedItem).getDataProvider();
				//  If provider is null, this widget isn't supported.
				if(prov!=null) {
					timeSettingsWidget.setDataItem((DataItem)selectedItem);
				}
			} else {
				//  TODO  support BBox for DataItemList
			}
		}		
	}
}
