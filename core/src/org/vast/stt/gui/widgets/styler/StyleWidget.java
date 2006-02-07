package org.vast.stt.gui.widgets.styler;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
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
import org.vast.stt.style.LineStyler;
import org.vast.stt.style.PointStyler;
 
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
 * @TODO  Modify other scrolled widgets to use the convention here.
 * @TODO  Listeners- inline vc. separate?
 * @TODO  Tie actions to DataItem properties
 * TODO  Mod scrolled widgets to size up when parent is sized up
 */
public class StyleWidget extends CheckOptionTable
{ 
	java.util.List<DataStyler> stylerAL;
	DataStyler activeStyler;
	enum StylerType { point, line };
	//  Try this
	BasicLineController basicLineController;
	AdvancedLineController advancedLineController;
	//  Or this
	OptionListener optListener;
	
	public StyleWidget(Composite parent){
		stylerAL  = new ArrayList<DataStyler>();
		init(parent);
//		addSelectionListener(this);
//		addCheckboxTableListener(this, this);
		setCheckboxTableContentProvider(new StyleTableContentProvider());
		setCheckboxTableLabelProvider(new StyleTableLabelProvider());
	}
	
	public OptionChooser createOptionChooser(Composite parent){
		optListener = new OptionListener();
		StyleOptionChooser basicOptionChooser = new StyleOptionChooser(parent, optListener);
		//  Go ahead and create OptListener up front
		//optListener = new OptionListener();
		//basicOptionChooser.setOptionListener(optListener);
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
	}
	
	private void addStyle(DataStyler styler){
		//  Add Checkbox to stylers Set and rerender Table
		stylerAL.add(styler);
		//  I wanted activeStyler to be set to the newly added styler, but that didn't work so well... 
		//activeStyler = styler;
		//  ... so I force it to null.  Could use flag to make this work as desired. 
		activeStyler = null;
		//  Change options panel to show Point options
		checkboxTableViewer.setInput(stylerAL);		
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
			AddStylerDialog addStylerDialog = 
				new AddStylerDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			int retCode = addStylerDialog.open();
			if(retCode == SWT.OK){
				int stylerType = addStylerDialog.getStylerType();
				DataStyler newStyler;
				switch(stylerType){
				case 0:
					newStyler = new PointStyler();
					//newStyler = StylerFactory 
					newStyler.setName("More Points");
					addStyle(newStyler);
					break;
				case 1:
					newStyler = new LineStyler();
					newStyler.setName("More Lines");
					addStyle(newStyler);
					break;
				default:
					System.err.println("StylerType note recognized in addStyler()");
					break;
				}
			}
		} else if  (control == deleteButton){
			if(activeStyler != null)
				removeStyle (activeStyler);  //  remove currently selected row   
		} else if (control == enabledButton){
			System.err.println("Enable selected");
			//DataItem item = (DataItem)enabledButton.getData();
			if(dataItem != null)
				dataItem.setEnabled(enabledButton.getSelection());
		} else if (control == advancedButton){
			createAdvancedStyleDialog();
		}
	}
	
	private void createAdvancedStyleDialog(){
		AdvancedStyleDialog asd = 
			new AdvancedStyleDialog(dataItem, activeStyler, optListener);
		//asd.setOptionListener(optListener);
	}
	
	//  Called when parent styleView is closed.  Set basicControls and
	//  basicStyler to null in OptListener (not sure this is sufficient 
	//  for the case of StyleView being closed, but AdvancedDialog still 
	//  open.  Also not sure if optListener will still be valid).
	public void close(){
		optListener.setBasicController(null);
		//optListener.setBasicStyler(null);
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Object element) {
//		return element.toString();
		DataStyler styler = (DataStyler)element;
		return styler.getName();
	}
}
