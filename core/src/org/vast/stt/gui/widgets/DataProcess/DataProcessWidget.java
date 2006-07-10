package org.vast.stt.gui.widgets.DataProcess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vast.process.DataProcess;
import org.vast.process.ProcessChain;
import org.vast.stt.data.SWEProvider;
import org.vast.stt.data.SensorMLProvider;
import org.vast.stt.gui.widgets.CheckOptionTable;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.process.WMS_Process;
import org.vast.stt.project.DataItem;
import org.vast.stt.project.DataProvider;
import org.vast.stt.project.DataStyler;
 
/**
 * <p><b>Title:</b><br/>
 * DataProcesWidget
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	Widget for controlling DataProcess options for a DataItem.       
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date May 10, 2006
 * @version 1.0
 * 
 * TODO  CkBoxTableViewer really only needs to be a list here, I think.
 *       Processes can't be turned on and off?
 * TODO  Should this GUI have an enable button?  If not, make it optional 
 *       in super class       
 * 
 */
public class DataProcessWidget extends CheckOptionTable
{ 
	java.util.List<DataProcess> processAL;
	DataProcess activeProcess;
	
	public DataProcessWidget(Composite parent){
		processAL  = new ArrayList<DataProcess>(2);
		checkboxTableLabel = "Providers:";
		allowAddRemove = false;
		init(parent);
		setCheckboxTableContentProvider(new TableContentProvider());
		setCheckboxTableLabelProvider(new TableLabelProvider());
	}
	
	public OptionChooser createOptionChooser(Composite parent){
		return new DataProcessOptionChooser(parent);
	}

	public void setDataItem(DataItem item){
		super.setDataItem(item);
		DataProvider prov = item.getDataProvider();
		if(prov == null)
			return;
		//  Provider should be a SensorMLProvider
		if(!(prov instanceof SensorMLProvider)) {
			System.err.println("SWE Provider not yer supported");
			return;
		}
		
		setProvider((SensorMLProvider)prov);
	}
	
	/**
	 * Make this DataProvider the currently active Provider
	 * 
	 * @param newProv
	 */
	public void setProvider(SensorMLProvider newProv){		
		DataProcess process = ((SensorMLProvider)newProv).getProcess();
		//  The provider needs to be propagated down so updateData() can be called
		//  when options are changed
		((DataProcessOptionChooser)optionChooser).setProvider(newProv);
		processAL.clear();
		if(process instanceof ProcessChain){
			//  Hack for now to test WMS Options
			List procList = ((ProcessChain)process).getProcessList();
			for(int i=0; i<procList.size(); i++){
    			DataProcess nextProcess = (DataProcess)procList.get(i);
    			//System.err.println("NextProc is " + nextProcess.getType());
    			processAL.add(nextProcess);
			}
		} else {
			processAL.add(process);
		}

		//  Display first process options initially
		optionChooser.buildControls(processAL.get(0));
		//  Change cbTableViewer contents
		checkboxTableViewer.setInput(processAL);	
		Iterator it = processAL.iterator();
//		DataProcess procTmp;
//		//  Set init state of checkboxes
//		while(it.hasNext()){
//			procTmp = (DataProcess)it.next();
//			//checkboxTableViewer.setChecked(procTmp, procTmp.);
//		}
		checkboxTableViewer.getTable().setSelection(0);
		ISelection selection = checkboxTableViewer.getSelection();
		checkboxTableViewer.setSelection(selection);	
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
		DataProcess proc = (DataProcess)selection.getFirstElement();
		//  Check for empty selection (happens when buildControls() is called)
		if(proc == null) {
			Iterator it = processAL.iterator();
			if(!it.hasNext()){
				//  stylerSet is currently empty
				optionChooser.removeOldControls();
				return;
			}
			//  Reset selected to first in Table
			checkboxTableViewer.getTable().setSelection(0);			
			selection = (StructuredSelection)checkboxTableViewer.getSelection();
			proc = (DataProcess)selection.getFirstElement();
		}
		//  Check to see if selected Styler has really changed
		if(proc == activeProcess){
			System.err.println("Selection not really changed");
			return;
		}
		System.err.println("Selection CHANGED");
		activeProcess = proc;
		optionChooser.buildControls(proc);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		Control control = (Control)e.getSource();
		if (control == enabledButton){
		} else if (control == advancedButton){
  		}
	}
	
	public void close(){
		//  TODO: dispose of any resources
	}
}

class TableContentProvider implements IStructuredContentProvider{

	DataProcess[] process;
	
	public Object [] getElements (Object inputElement){
		ArrayList<DataProcess> procs = (ArrayList<DataProcess>)inputElement;
		DataProcess [] procArr = procs.toArray(new DataProcess[]{});
		return procArr;
	}
		
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//System.err.println("Input changed is " + );
	}
	
}

class TableLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		if(element == null)
			System.err.println("???");
		DataProcess proc = (DataProcess)element;
		return proc.getName();
	}
}
