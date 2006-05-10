package org.vast.stt.gui.widgets.dataProvider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
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
import org.vast.stt.project.DataProvider;
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
{ 
	java.util.List<DataProvider> providerAL;
	DataProvider activeProv;
	
	public DataProviderWidget(Composite parent){
		providerAL  = new ArrayList<DataProvider>(2);
		checkboxTableLabel = "Providers:";
		allowAddRemove = false;
		init(parent);
		setCheckboxTableContentProvider(new TableContentProvider());
		setCheckboxTableLabelProvider(new LabelProvider());
	}
	
	public OptionChooser createOptionChooser(Composite parent){
		return new DataProviderOptionChooser(parent);
	}

	public void setDataItem(DataItem item){
		super.setDataItem(item);
		DataProvider prov = item.getDataProvider();
		//  Here, I'm getting either a SWEDataProv or SensorMLDataProv
		
		//  TODO:  Rearrange methods so OptChooser controls get removed regardless here.
		if(prov == null)
			return;
		setProvider(prov);
	}
	
	/**
	 * Make this DataProvider the currently active Provider
	 * 
	 * @param newProv
	 */
	public void setProvider(DataProvider newProv){		
		providerAL.clear();
		providerAL.add(newProv);
		
		if (newProv instanceof SWEProvider) {
			System.err.println("DataProvWidget:  Provider type not supported yet: " + newProv);
		} else 
			System.err.println("DataProvWidget:  Provider type not recognized: " + newProv);
				
		//optionChooser.buildControls(newProv);
	}
	
	//  enabling checkbox causes ckState AND selChanged events
	public void checkStateChanged(CheckStateChangedEvent e) {
		// TODO Auto-generated method stub
	}
	
	//  Selecting label causes ONLY selChanged event
	public void selectionChanged(SelectionChangedEvent e) {
		System.err.println("sel source is" + e.getSource());
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
	
	public void close(){
		//  TODO: dispose of any resources
	}
}

class TableContentProvider implements IStructuredContentProvider{

	DataProvider[] providers;
	
	public Object [] getElements (Object inputElement){
		ArrayList<DataProvider> provs = (ArrayList<DataProvider>)inputElement;
		DataProvider [] provArr = provs.toArray(new DataProvider[]{});
		return provArr;
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
		DataProvider prov = (DataProvider)element;
		return prov.getClass().getName();
	}
}
