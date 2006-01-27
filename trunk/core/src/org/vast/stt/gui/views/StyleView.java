package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.gui.widgets.dataProvider.DataProviderWidget;
import org.vast.stt.gui.widgets.styler.StyleWidget;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataItem;

/**
 * <p><b>Title:</b><br/>
 * StyleView
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	View for manipulating the Style/Symbolizers of DataItems   
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 11, 2006
 * @version 1.0
 * 
 */
public class StyleView extends ViewPart implements ISelectionListener{
	
	public static final String ID = "STT.StyleView";
	StyleWidget styleWidget;
	//test only
	DataProviderWidget dpWidget;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		initView(parent);
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);		
	}
	
	public void initView(Composite parent){
		//dpWidget = new DataProviderWidget(parent);
		styleWidget = new StyleWidget(parent);
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
				//dpWidget.setDataItem((DataItem)selectedItem);
				styleWidget.setDataItem((DataItem)selectedItem);
			} else {
				//  May add support for Composite items later
			}
		}		
	}
}
