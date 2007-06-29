/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.gui.widgets.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.vast.cdm.common.DataComponent;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.ows.OWSUtils;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.sos.SOSQuery;
import org.vast.ows.sos.SOSResponseReader;
import org.vast.ows.util.TimeInfo;
import org.vast.stt.data.DataException;
import org.vast.stt.gui.widgets.symbolizer.AdvancedGeometryTab;

/**
 * <p><b>Title:</b>
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Mar 21, 2007
 * @version 1.0
 */

public class SOSMappingPage extends WizardPage implements SelectionListener
{
	SOSLayerCapabilities caps;
	String [] offerings;
	private AdvancedGeometryTab geometryComp;
	Button procBtn, directBtn;
	List<String>possibleMappings = new ArrayList<String>(3);
	HashMap<String, String []> mappings = new HashMap<String, String[]>(3);
	int offeringIndex = 0;
	
	public SOSMappingPage(SOSLayerCapabilities caps){
		super("Map Offerings");
		this.caps = caps;
		setDescription("Map the SOS Offering components to Display space");
	}
	
	public void setOfferings(String [] offerings){
		this.offerings = offerings;
		offeringIndex = 0;
		String [] dataComponents = getComponents(offeringIndex);
		if(dataComponents != null) {
			geometryComp.setMappableItems(dataComponents);
			assignDefaultComponents(dataComponents);
		} else
			; //  disable combos, require ProcessChain?
	}
	
	private String [] getComponents(int index){
		setTitle(offerings[offeringIndex] + " Geometry Mapper");
		String [] components = mappings.get(offerings[index]);
		if(components == null){
			components = requestComponents();
			if(components == null)
				return null;//  disable mapping controls for this observation
			else 
				mappings.put(offerings[index], components);
		}
		return components;
	}
	
	private String [] requestComponents(){
		DataComponent component = null; 
		try {
			component = issueRequest();
		} catch (DataException e) {
			e.printStackTrace();
			//  TODO: report error and disable this offering (or force to processChain mapping)
			return null;
		}
		//  Get DataComponents;
		//  TODO  store these so they don't have to be reloaded in the event
		//  of user selecting prev/next repeatedlyd
		if(component != null)
			findPossibleMappings(component, component.getName());
		String [] mappings = possibleMappings.toArray(new String[]{});
		possibleMappings.clear();
		return(mappings);
	}
	
	/**
	 * Assign defaults for X,Y,Z, and time, if they are present in the
	 * available Mapping componenets.  Also, should skip any fields
	 * where user has ovverridden the defaults
	 * 
	 *  @TODO
	 */
	private void assignDefaultComponents(String [] mapping){
		//  assign X to lon
		for(int index=0; index<mapping.length; index++){
			if(mapping[index].indexOf("longitude")!=-1)
				geometryComp.setMapFromX(index);
			else if(mapping[index].indexOf("latitude")!=-1)
				geometryComp.setMapFromY(index);
			else if(mapping[index].indexOf("altitude")!=-1 || mapping[index].indexOf("elevation")!=-1)
				geometryComp.setMapFromZ(index);
			else if(mapping[index].indexOf("time")!=-1)
				geometryComp.setMapFromTime(index);
			else if(mapping[index].indexOf("break")!=-1)
				geometryComp.setMapFromBreak(index);
				
		}
	}
	
	//  This creates a dummy request just to get the DataComponents back
	//  This is a problematic approach, though.  It will be impossible 
	//  to predict what constitutes a valid request, since time beheavior and
	//  bbox behavior or both highly servlet-dependent.
	//  Fudging some stuff now just to make it work, in the hopes that future
	//  SOS implemntations (including our own) will support the getTemplate() 
	//  method.    TC 3/22/07
	private DataComponent issueRequest() throws DataException {
		SOSQuery query = new SOSQuery();
		query.setRequest("GetObservation");
		query.setGetServer(caps.getParent().getGetServers().get("GetObservation"));
		query.setPostServer(caps.getParent().getGetServers().get("GetObservation"));
		query.setOffering(caps.getId());
		query.getObservables().add(caps.getObservableList().get(0));
		query.getProcedures().add(caps.getProcedureList().get(0));
		query.setVersion("0.0.31");  //  ?
		query.setFormat(caps.getFormatList().get(0));
		query.setService("SOS");
		caps.getParent().getGetServers();
		//  TimeInfo kludge
		TimeInfo capsTime = caps.getTimeList().get(0);
		TimeInfo requestTime = capsTime.copy();
		//  if this is a realtime dataset, mod the requestTime
		//  to request some data for a short period
		if(capsTime.isEndNow() == true)
			requestTime.setBeginNow(true);
		else  //  hardwire 10 minute request, if not rt
			requestTime.setStopTime(requestTime.getStartTime() + 600.0);
		query.setTime(requestTime);
		
		InputStream dataStream = null;
		try {
			// create reader
			SOSResponseReader reader = new SOSResponseReader();

			// select request type (post or get)
			boolean usePost = false;
			OWSUtils owsUtils = new OWSUtils();
			dataStream = owsUtils.sendRequest(query, usePost).getInputStream();

			// parse response
			reader.parse(dataStream);

			// display data structure and encoding
			DataComponent dataInfo = reader.getDataComponents();
			return dataInfo;
		} catch (Exception e) {
			String server = query.getPostServer();
			if (server == null)
				server = query.getGetServer();
			throw new DataException("Error while reading data from " + server, e);
		} finally {
			try {
				if (dataStream != null) 
					dataStream.close();
			} catch (IOException e){
			}
		}		
	}
	
	private void findPossibleMappings(DataComponent component, String componentPath) {
        // for each array, build an array mapper
        if (component instanceof DataArray)
        {  
        	possibleMappings.add(componentPath);
            DataComponent childComponent = component.getComponent(0);
            String childPath = componentPath + '/' + childComponent.getName();
            findPossibleMappings(childComponent, childPath);
        }
        
        // just descend into DataGroups
        else if (component instanceof DataGroup)
        {
        	possibleMappings.add(componentPath);
            for (int i = 0; i < component.getComponentCount(); i++)
            {
                DataComponent childComponent = component.getComponent(i);
                String childPath = componentPath + '/' + childComponent.getName();
                findPossibleMappings(childComponent, childPath);
            }
        }
        
        // handle DataValues
        else if (component instanceof DataValue)
        {
        	possibleMappings.add(componentPath);
        }
    }

	
	public void createControl(Composite parent){
		Composite mainComp = new Composite(parent, 0x0);
		GridLayout layout = new GridLayout(2, false);
		mainComp.setLayout(layout);
		directBtn = new Button(mainComp, SWT.RADIO);
		directBtn.setText("Direct Mapping");
		GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		directBtn.setLayoutData(gd);
		directBtn.setSelection(true);
		directBtn.addSelectionListener(this);
		procBtn = new Button(mainComp, SWT.RADIO);
		procBtn.setText("Discover Process");
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		procBtn.setLayoutData(gd);
		procBtn.addSelectionListener(this);
		procBtn.setEnabled(false);
		geometryComp = new AdvancedGeometryTab(mainComp);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		geometryComp.setLayoutData(gd);
		setControl(mainComp);
	}
	
	public boolean canFlipToNextPage() {
	     return true;
	}
	
	public IWizardPage getPreviousPage(){
		if(offeringIndex == 0)
			return ((AddSOSItemWizard)this.getWizard()).sosChooserPage;
		offeringIndex--;
		String [] dataComponents = getComponents(offeringIndex);
		if(dataComponents != null) {
			geometryComp.setMappableItems(dataComponents);
			assignDefaultComponents(dataComponents);
		}
		return this;
	}
	
	public IWizardPage getNextPage(){
		//storeCurrentMappings();
		if(++offeringIndex < offerings.length) {
			String [] dataComponents = getComponents(offeringIndex);
			if(dataComponents != null) {
				geometryComp.setMappableItems(dataComponents);
				assignDefaultComponents(dataComponents);
			}
			return this;
		}
		return ((AddSOSItemWizard)this.getWizard()).symPage;
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		//  change page options
	}
}


