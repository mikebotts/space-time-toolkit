package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.data.DataProvider;
import org.vast.stt.gui.widgets.StyleWidget;
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
public class StyleView extends ViewPart implements ISelectionListener{
	
	public static final String ID = "STT.StyleView";
	StyleWidget styleWidget;
	Label itemLabel;
	Button enableCb, advancedBtn, colorBtn;
	Composite parent;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		initView(parent);
		getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);		
	}
	
	public void initView(Composite parent){
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
}
