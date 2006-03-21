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
 * DataProviderView
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	View for managing options of DataProviders   
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Mar 2, 2006
 * @version 1.0
 *
 * 
 */
public class DataProviderView extends ViewPart implements ISelectionListener{
	
	public static final String ID = "STT.DataProviderView";
	DataProviderWidget dpWidget;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		initView(parent);
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);		
	}
	
	public void initView(Composite parent){
		dpWidget = new DataProviderWidget(parent);
	}

		@Override
	public void setFocus() {
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		if (part instanceof SceneTreeView)
		{
			DataEntry selectedItem = (DataEntry)((IStructuredSelection)selection).getFirstElement();
			if(selectedItem instanceof DataItem) { 
				System.err.println("item " + selectedItem);
				//dpWidget.setDataItem((DataItem)selectedItem);
				dpWidget.setDataItem((DataItem)selectedItem);
			} else {
				//  May add support for Composite items later
			}
		}		
	}
	
	public void dispose(){
		dpWidget.close();
		super.dispose();
		//System.err.println("SV disposed!");
	}
}
