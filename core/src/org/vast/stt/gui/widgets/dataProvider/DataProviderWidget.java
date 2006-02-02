package org.vast.stt.gui.widgets.dataProvider;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vast.stt.data.DataProvider;
import org.vast.stt.gui.widgets.CheckOptionTable;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.scene.DataItem;
 
/**
 * <p><b>Title:</b><br/>
 * StyleWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling DataProvider options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 26, 2006
 * @version 1.0
 * 
 */
public class DataProviderWidget extends CheckOptionTable
	implements ICheckStateListener, ISelectionChangedListener,
		SelectionListener
		
{ 
	java.util.List<DataProvider> providerAL;
	DataProvider activeProv;
	
	public DataProviderWidget(Composite parent){
		providerAL  = new ArrayList<DataProvider>();
		init(parent);
		super.addSelectionListener(this);
		super.addCheckboxTableListener(this, this);
	}
	
	public OptionChooser createOptionChooser(Composite parent){
		return new DataProviderOptionChooser(parent);
	}

	public void setDataItem(DataItem item){
		super.setDataItem(item);
		setProvider(item.getDataProvider());
	}
	
	
	/**
	 * Make this DataStyler the currently active Styler in the StyleWidget
	 * 
	 * @param newStyler
	 */
	public void setProvider(DataProvider newProv){
		//  Check for CompositeStyler first...
		//  Move this...
		optionChooser.buildControls(newProv);
		System.err.println("New Prov is " + newProv);
		//		if(newStyler instanceof CompositeStyler) 
//			stylerAL = ((CompositeStyler)newStyler).getStylerList();
//		else {  // this is a single DataStyler
//			stylerAL = new ArrayList<DataStyler>();
//			stylerAL.add(newStyler);
//		}
		//  Change cbTableViewer contents
//		checkboxTableViewer.setInput(stylerAL);	
//		Iterator it = stylerAL.iterator();
//		DataStyler stylerTmp;
//		//  Set init state of checkboxes
//		while(it.hasNext()){
//			stylerTmp = (DataStyler)it.next();
//			checkboxTableViewer.setChecked(stylerTmp, stylerTmp.isEnabled());
//		}
	}
	//  enabling checkbox causes ckState AND selChanged events
	public void checkStateChanged(CheckStateChangedEvent e) {
		// TODO Auto-generated method stub
		//  e.getElement returns checked Styler
//		DataStyler styler = (DataStyler)e.getElement();
//		styler.setEnabled(e.getChecked());
	}
	
	//  Selecting label causes ONLY selChanged event
	public void selectionChanged(SelectionChangedEvent e) {
		System.err.println("sel source is" + e.getSource());
		StructuredSelection selection = (StructuredSelection)e.getSelection();
//		DataStyler styler = (DataStyler)selection.getFirstElement();
//		//  Check for empty selection (happens when buildControls() is called)
//		if(styler == null) {
//			Iterator it = stylerAL.iterator();
//			if(!it.hasNext()){
//				//  stylerSet is currently empty
//				optionChooser.removeOldControls();
//				return;
//			}
//			//  Reset selected to first in Table
//			checkboxTableViewer.getTable().setSelection(0);			
//			selection = (StructuredSelection)checkboxTableViewer.getSelection();
//			styler = (DataStyler)selection.getFirstElement();
//		}
//		//  Check to see if selected Styler has really changed
//		if(styler == activeStyler){
//			System.err.println("Selection not really changed");
//			return;
//		}
//		System.err.println("Selection CHANGED");
//		activeStyler = styler;
//		optionChooser.buildControls(styler);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		Control control = (Control)e.getSource();
		if(control == addButton){
		} else if  (control == deleteButton){
		} else if (control == enabledButton){
		} else if (control == advancedButton){
  		}
	}
	
}

class TableContentProvider implements IStructuredContentProvider{

	DataProvider[] providers;
	
	public Object [] getElements (Object inputElement){
		return null;
	}
		
	public void dispose() {
		// TODO Auto-generated method stub
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		//System.err.println("Input changed is " + );
	}
	
}

class TableLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return element.toString();
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}
}
