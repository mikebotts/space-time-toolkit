package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.data.DataProvider;
import org.vast.stt.scene.DataEntry;
import org.vast.stt.scene.DataItem;

import com.sun.org.apache.bcel.internal.generic.PUSH;

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
 * @TODO  Modify other scrolled widgets to use the convention here.
 * @TODO  Try wrapping entire widget in ScrolledComp now
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
		///ScrolledComposite scroller = new ScrolledComposite(parent, SWT.SHADOW_ETCHED_OUT | SWT.V_SCROLL | SWT.H_SCROLL);
        //scroller.setExpandHorizontal(true);
	    //scroller.setExpandVertical(true);

		GridLayout mainLayout = new GridLayout(1, false);
		mainLayout.verticalSpacing = 5;
		parent.setLayout(mainLayout);
		
		//  Create Item Label
		itemLabel = new Label(parent, SWT.SHADOW_IN);
		itemLabel.setText("DataItem Name Goes Here");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		itemLabel.setLayoutData(gridData);

		//  Create enabled cb
		enableCb = new Button(parent, SWT.CHECK);
		enableCb.setText("enabled");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalIndent = 5;
		enableCb.setLayoutData(gridData);

		//  Create Styles Label/Btns
		Composite styleBtnComp = new Composite(parent, 0x0);
		GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		layout.makeColumnsEqualWidth = true;
		styleBtnComp.setLayout(layout);
		gridData = new GridData();
		gridData.verticalIndent = 5;
		gridData.verticalAlignment = SWT.BOTTOM;
		styleBtnComp.setLayoutData(gridData);
		
		Label styleLabel = new Label(styleBtnComp, SWT.SHADOW_IN );
		styleLabel.setText("Styles:");
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.verticalAlignment = SWT.BOTTOM;
		styleLabel.setLayoutData(gridData);
		Button addBtn = new Button(styleBtnComp, SWT.PUSH | SWT.SHADOW_IN);
		addBtn.setText("+");
		addBtn.setToolTipText("Add a style");
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		addBtn.setLayoutData(gridData);	
		Button subBtn = new Button(styleBtnComp, SWT.PUSH);
		subBtn.setText("-");
		subBtn.setToolTipText("Remove a style");
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = SWT.FILL;
//		gridData.grabExcessVerticalSpace = true;
		subBtn.setLayoutData(gridData);	
		
		//  ScrolledComp for styles
		ScrolledComposite styleScroller = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		styleScroller.setExpandHorizontal(true);
		styleScroller.setExpandVertical(true);
		//  without setting LayoutData for the ScrolledComposite, I could NOT get the scrollbars to appear when the  
		//  parent of the scrolledComposite also uses a GridLayout.  Instead, the ScrolledComposite sized up to fit all 
		//  requested children, and was subsequesntly clipped.  The GridData constructor below is setting horizontal and 
		//  vertical span to 1, and setting grabExcess*Space to true.  Magic occurs, and this behaves as desired.
		styleScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));		//styleScroller.setSize(100,100);
		Composite styleGroup = new Composite(styleScroller, SWT.NONE);
		styleScroller.setContent(styleGroup);
		styleGroup.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		//styleGroup.setTe
		layout = new GridLayout();
		layout.numColumns = 1;
		//layout.verticalSpacing = ;
		styleGroup.setLayout(layout);
		//  Creating points and lines stylers by default for now.  TC
		Button styleBtn = new Button(styleGroup, SWT.CHECK);
		styleBtn.setText("points");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		styleBtn.setLayoutData(gridData);
		//  use this to hilite the Btn when selected (this doesn't fill the width though)
		styleBtn.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		styleBtn = new Button(styleGroup, SWT.CHECK);
		styleBtn.setText("lines");
		styleScroller.setMinSize(styleGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
		
		//  Create Optss Label/Btns/List
		Label optLabel = new Label(parent, SWT.SHADOW_IN | SWT.LEFT);
		optLabel.setText("Options:");
		gridData = new GridData();
		gridData.verticalIndent = 5;
		optLabel.setLayoutData(gridData);

		//  ScrolledComp for options
		ScrolledComposite optSc = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		optSc.setExpandHorizontal(true);
		optSc.setExpandVertical(true);
		optSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));		//styleScroller.setSize(100,100);
		Composite optGroup = new Composite(optSc, SWT.NONE);
		optSc.setContent(optGroup);
		optGroup.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		//  optGroup contents to be determined dynamically
		//  TODO  Create pretty options
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		//layout.verticalSpacing = ;
		optGroup.setLayout(layout);
		//  Creating points and lines stylers by default for now.  TC
		Label sizeLabel = new Label(optGroup, 0x0);
		sizeLabel.setText("Point Size:");
		Combo sizeCombo = new Combo(optGroup, SWT.DROP_DOWN| SWT.READ_ONLY);
		sizeCombo.setItems(new String [] {"1", "2", "3", "4", "5", "6", "7", "8", "9"});
		sizeCombo.select(1);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		sizeCombo.setLayoutData(gridData);
		
		//Combo colorCombo = new Combo(optGroup, SWT.DROP_DOWN);
		//colorCombo.set
		
		
		//  And be sure to set minSize of Scrolled Composite
		optSc.setMinSize(optGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));		

		// Create Advanced Button
		Button advancedBtn = new Button(parent, SWT.PUSH);
		advancedBtn.setText("Advanced...");
		advancedBtn.setToolTipText("Configure " +
				"Advanced Options");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		advancedBtn.setLayoutData(gridData);
		
		//  Must give sroller sufficient size
		//scroller.setMinSize(mainGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
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
				//DataStyler [] styler = ((DataItem)selectedItem).getDataStylers();
//				if(stylers != null) {
//					//  populate styles/options widgets
//				}
			} else {
				//  May add support for Composite items later
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
