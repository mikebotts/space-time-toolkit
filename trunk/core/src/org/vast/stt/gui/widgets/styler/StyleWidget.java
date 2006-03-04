package org.vast.stt.gui.widgets.styler;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.vast.stt.gui.widgets.CheckOptionTable;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.scene.DataItem;
import org.vast.stt.style.CompositeStyler;
import org.vast.stt.style.DataStyler;
import org.vast.stt.style.StylerFactory;
 
/**
 * <p><b>Title:</b><br/>
 * StyleWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling styler options for a DataItem.  Note that I 
 *  used a trial version of SWTDesigner to build portions of this widget.     
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 14, 2006
 * @version 1.0
 * 
 */
public class StyleWidget extends CheckOptionTable
{ 
	java.util.List<DataStyler> stylerAL;
	DataStyler activeStyler;
	enum StylerType { point, line };
	OptionListener optListener;
	
	public StyleWidget(Composite parent){
		stylerAL  = new ArrayList<DataStyler>();
		checkboxTableLabel = "Styles:";
		init(parent);
		setCheckboxTableContentProvider(new StyleTableContentProvider());
		setCheckboxTableLabelProvider(new StyleTableLabelProvider());
	}
	
	public OptionChooser createOptionChooser(Composite parent){
		optListener = new OptionListener();
		StyleOptionChooser basicOptionChooser = new StyleOptionChooser(parent, optListener);
		return basicOptionChooser;
	}

	public void setDataItem(DataItem item){
		super.setDataItem(item);
		setStyler(item.getStyler());
	}
	
	
	/**
	 * Make this DataStyler the currently active Styler in the StyleWidget
	 * 
	 * @param newStyler
	 */
	public void setStyler(DataStyler newStyler){
		//  Check for CompositeStyler first...
		if(newStyler instanceof CompositeStyler) 
			stylerAL = ((CompositeStyler)newStyler).getStylerList();
		else {  // this is a single DataStyler
			stylerAL = new ArrayList<DataStyler>();
			stylerAL.add(newStyler);
		}
		//  Change cbTableViewer contents
		checkboxTableViewer.setInput(stylerAL);	
		Iterator it = stylerAL.iterator();
		DataStyler stylerTmp;
		//  Set init state of checkboxes
		while(it.hasNext()){
			stylerTmp = (DataStyler)it.next();
			checkboxTableViewer.setChecked(stylerTmp, stylerTmp.isEnabled());
		}
		checkboxTableViewer.getTable().setSelection(0);
		ISelection selection = checkboxTableViewer.getSelection();
		checkboxTableViewer.setSelection(selection);	
	}
	
	void addStyle(DataStyler styler){
		//  Add Checkbox to stylers Set and rerender Table
		stylerAL.add(styler);
		dataItem.addStyler(styler);
		activeStyler = null;
		//  Change options panel to show Point options
		checkboxTableViewer.setInput(stylerAL);		
	}
	
	/**
	 * create a new styler and call addStyle() with it
	 */
	public void createNewStyler(String stylerName, int stylerType){
		DataStyler newStyler = null;
		switch(stylerType){
		case 0:
			newStyler = 
				StylerFactory.createDefaultPointStyler(dataItem.getDataProvider());
			//  Hack to set geom
			newStyler.getSymbolizer().setGeometry(activeStyler.getSymbolizer().getGeometry());
			break;
		case 1:
			newStyler = 
				StylerFactory.createDefaultLineStyler(dataItem.getDataProvider());
			//  Hack to set geom 
			newStyler.getSymbolizer().setGeometry(activeStyler.getSymbolizer().getGeometry());
			break;
		default:
			System.err.println("StylerType note supported in createNewStyler()");
			break;
		}
		if(newStyler != null) {
			newStyler.setName(stylerName);
			newStyler.updateDataMappings();
			newStyler.setEnabled(false);
			addStyle(newStyler);
		}
		
	}

	private void removeStyle(DataStyler styler){
		stylerAL.remove(styler);
		//  reset activeStyler
		// ...
		//TableItem [] items = checkboxTableViewer.getTable().getItems();
		checkboxTableViewer.setInput(stylerAL);
	}

	//  enabling checkbox causes ckState AND selChanged events
	public void checkStateChanged(CheckStateChangedEvent e) {
		// TODO Auto-generated method stub
		//  e.getElement returns checked Styler
		DataStyler styler = (DataStyler)e.getElement();
		styler.setEnabled(e.getChecked());
	}
	
	//  Selecting label causes ONLY selChanged event
	public void selectionChanged(SelectionChangedEvent e) {
		System.err.println("sel source is" + e.getSource());
		StructuredSelection selection = (StructuredSelection)e.getSelection();
		DataStyler styler = (DataStyler)selection.getFirstElement();
		//  Check for empty selection (happens when buildControls() is called)
		if(styler == null) {
			Iterator it = stylerAL.iterator();
			if(!it.hasNext()){
				//  stylerSet is currently empty
				optionChooser.removeOldControls();
				return;
			}
			//  Reset selected to first in Table
			checkboxTableViewer.getTable().setSelection(0);			
			selection = (StructuredSelection)checkboxTableViewer.getSelection();
			styler = (DataStyler)selection.getFirstElement();
		}
		//  Check to see if selected Styler has really changed
		if(styler == activeStyler){
			System.err.println("Selection not really changed");
			return;
		}
		System.err.println("Selection CHANGED");
		activeStyler = styler;
		optionChooser.buildControls(styler);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		Control control = (Control)e.getSource();
		if(control == addButton){
			openAddStyleDialog();
		} else if  (control == deleteButton){
			if(activeStyler != null)
				removeStyle (activeStyler);  //  remove currently selected row   
		} else if (control == enabledButton){
			if(dataItem != null)
				dataItem.setEnabled(enabledButton.getSelection());
		} else if (control == advancedButton){
			createAdvancedStyleDialog();
		}
	}
	
	private void createAdvancedStyleDialog(){
		new AdvancedStyleDialog(dataItem, activeStyler, optListener);
	}
	
	private void openAddStyleDialog(){
		AddStylerDialog asd = new AddStylerDialog(this);
	}
	
	//  Called when parent styleView is closed.  Set basicControls and
	//  basicStyler to null in OptListener (not sure this is sufficient 
	//  for the case of StyleView being closed, but AdvancedDialog still 
	//  open.  Also not sure if optListener will still be valid).
	public void close(){
		optListener.setBasicController(null);
	}
	
}

class StyleTableContentProvider implements IStructuredContentProvider{

	DataStyler[] stylers;
	
	public Object [] getElements (Object inputElement){
		ArrayList<DataStyler> stylers = (ArrayList<DataStyler>)inputElement;
		DataStyler [] stylerArr = stylers.toArray(new DataStyler[]{});
		return stylerArr;
	}
		
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		//System.err.println("Input changed is " + );
		//  Reset selected to first in Table
		//  NOTE that including the next 3 lines somehow triggers
		//  table.releaseWidget(0 bug on exit
		//  See Eclipse bug report #45708 - fixed in Eclipse 3.2
//		((CheckboxTableViewer)viewer).getTable().setSelection(0);
//		ISelection selection = viewer.getSelection();
//		viewer.setSelection(selection);
	}
	
}

class StyleTableLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		DataStyler styler = (DataStyler)element;
		return styler.getName();
	}
}
