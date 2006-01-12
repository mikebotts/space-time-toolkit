package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.data.DataProvider;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataItem;

/**
 * <p><b>Title:</b><br/>
 * TimeSettingsView
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
 * @TODO  add +/- to Bias
 * @TODO  add control for buttons
 */
public class StyleView extends ViewPart implements ISelectionListener, SelectionListener{
	
	public static final String ID = "STT.StyleView";
	Label itemLabel;
	Button enableCb;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		initView(parent);
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);		
	}
	
	public void initView(Composite parent){
		//  Scrolled Composite to hold everything
		ScrolledComposite scroller = new ScrolledComposite(parent, SWT.SHADOW_ETCHED_OUT | SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
	    scroller.setExpandVertical(true);

	    //  Main Group Holds itemLabel, enabled checkbox, styles list, options list, and advanced buttons
		Group mainGroup = new Group(scroller, SWT.SHADOW_NONE);
		scroller.setContent(mainGroup);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		mainGroup.setLayout(layout);
		
		//  Create Label
		itemLabel = new Label(mainGroup, SWT.SHADOW_IN);
		itemLabel.setText("DataItem Name Goes Here");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		itemLabel.setLayoutData(gridData);
		
		//  Create enabled cb
		enableCb = new Button(mainGroup, SWT.CHECK);
		enableCb.setText("enabled");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		enableCb.setLayoutData(gridData);

		//  Create Styles Label/Btns/List
		final Table table =  new Table(mainGroup,SWT.MULTI|SWT.CHECK);
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("Styles:");
		col1.setWidth(80);
//		TableColumn col2 = new TableColumn(table, SWT.LEFT);
//		col2.setText("Coloumn 2");
//		col2.setWidth(80);

		TableItem item1 = new TableItem(table, 0);
		item1.setText(new String[] { "points" });
		TableItem item2 = new TableItem(table, 0);
		item2.setText(new String[] { "lines" });

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Create Options Label and List
		
		// Create Advanced Button
		
		//  Must give sroller sufficient size
		scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
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
				//itemLabel.setText(selectedItem.getName());
				DataProvider prov = ((DataItem)selectedItem).getDataProvider();
				//  If provider is null, this widget isn't supported.
				if(prov!=null) {
				}
			} else {
				//  TODO  support BBox for DataItemList
			}
		}		
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
//		if(e.widget == useAbsTimeBtn){
//			//  item.useAbsTime;
//		} else if (e.widget == continusUpdateBtn) {
//			//  item.enableContUpdate;
//		} else if(e.widget == updateNowBtn){
//			//  item.setUpdateNow;
//		} else {
//			System.err.println(e);
//		}
	}
	
}
