package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.gui.widgets.bbox.BboxWidget;
import org.vast.stt.project.DataEntry;
import org.vast.stt.project.DataItem;

/**
 *  View Class for modding geographic region of a dataItem.
 * 
 * @author tcook
 * @date 12/13/05
 * 
 * TODO  Could support other formats (dd'mm"ss, dd'mm.mm")  like the old CompassPanel, but 
 *       I need to figure out how to enforce editor behavior on SWT Text Widgets to make this work.
 */

public class SpatialExtentView  extends ViewPart implements  ISelectionListener
{
	public static final String ID = "STT.SpatialExtentView";
	BboxWidget bboxWidget;
	
	public void createPartControl(Composite parent)
	{
		bboxWidget = new BboxWidget(parent);
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);
	}
	
	/**
	 * initView
	 * @param parent
	 */
	@Override
	public void setFocus()
	{
		
	}

	@Override
	public void dispose()
	{
		//getSite().getPage().removePostSelectionListener(this);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		if (part instanceof SceneTreeView)
		{
			DataEntry selectedItem = (DataEntry)((IStructuredSelection)selection).getFirstElement();
			if(selectedItem instanceof DataItem) {
				bboxWidget.setDataItem((DataItem)selectedItem);
			} else {
				//  TODO  support BBox for DataItemList
			}
		}		
	}

}
