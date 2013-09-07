/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>    Tony Cook <tcook@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.gui.widgets.catalog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
import org.vast.cdm.common.CDMException;
import org.vast.cdm.common.DataComponent;
import org.vast.data.DataArray;
import org.vast.data.DataGroup;
import org.vast.data.DataValue;
import org.vast.math.Vector3d;
import org.vast.ows.OWSUtils;
import org.vast.ows.sas.SASLayerCapabilities;
import org.vast.ows.sas.SASSubscribeRequest;
import org.vast.ows.sas.SASSubscribeResponse;
import org.vast.ows.sas.SASSubscribeResponseReader;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sas.SASLayerCapabilities;
import org.vast.stt.data.DataException;
import org.vast.stt.gui.widgets.symbolizer.AdvancedGeometryTab;
import org.vast.sweCommon.SWECommonUtils;
import org.vast.sweCommon.SWEReader;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLReaderException;
import org.w3c.dom.Element;

/**
 * <p><b>Title:</b>
 * </p>
 *
 * <p><b>Description:</b><br/>
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Gregoire Berthiau & Susan Ingenthron
 * @date Mar 21, 2008
 * @version 1.0
 */

public class SASMappingPage extends WizardPage implements SelectionListener
{
	
	SASLayerCapabilities caps;
	private AdvancedGeometryTab geometryComp;
	Button procBtn, directBtn;
	List<String>possibleMappings = new ArrayList<String>(3);
	//  mappings contains possible mappings for each offering
	HashMap<String, String []> mappings = new HashMap<String, String[]>(3);
	//  selectedMappings contains user-selected mappings for each offering (from the combos)
	HashMap<String, String []> selectedMappings = new HashMap<String, String[]>(3);
	
	int offeringIndex = 0;
	private Vector3d foiLocation;
	
	public SASMappingPage(SASLayerCapabilities caps){
		super("Map Offerings");
		this.caps = caps;
		setDescription("Map the SAS Offering components to Display space");
	}
	
	public void setSubscription(){
		String [] dataComponents = null;
		dataComponents = getComponents();
		if(dataComponents != null) {
			geometryComp.setMappableItems(dataComponents);
			assignDefaultComponents(dataComponents);
		} else
			; //  disable combos, require ProcessChain?
	}
	
	private String [] getComponents(){
		setTitle(caps.getTitle() + " Geometry Mapper");
		String [] components = mappings.get(caps.getTitle());
		if(components == null){
			components = requestComponents();
			if(components == null)
				return null;//  didn't work, return null
			else 
				mappings.put(caps.getTitle(), components);
		}
		return components;
	}
	
	private String [] requestComponents(){
		DataComponent component = null; 
		try {
			component = getSweReader().getDataComponents();
		} catch (DataException e) {
			e.printStackTrace();
			//  TODO: report error and disable this offering (or force to processChain mapping)
			return null;
		}
		//  Get DataComponents;
		//  TODO  store these so they don't have to be reloaded in the event
		//  of user selecting prev/next repeatedly
		if(component != null)
			findPossibleMappings(component, component.getName());
		//  Add lat/lon from FOI- this assumes FOI is a simple point, which may not always
		//  be the case, but the SOSResponseReader is using a Vector3d, so it needs to 
		//  be modified there first to support more complex geom in FOI.
		if(foiLocation != null) {
			possibleMappings.add("FeatureOfInterest/latitude");
			possibleMappings.add("FeatureOfInterest/longitude");
			possibleMappings.add("FeatureOfInterest/altitude");
		}
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
	
	// This parse the SWECommon description held in the layerCap
	private SWEReader getSweReader() throws DataException {
		
		SWEReader sweReader = null;
		try {
			byte[] bytes = caps.getMessageStructure().getBytes("UTF-8");
			InputStream messageStructureStream = new ByteArrayInputStream(bytes);
			
			DOMHelper dom = new DOMHelper(messageStructureStream, false);
			Element rootElement = dom.getRootElement();
			
			Element defElt = dom.getElement(rootElement, "DataDefinition");
			if (defElt == null)
			    defElt = dom.getElement(rootElement, "DataBlockDefinition");            
			Element dataElt = dom.getElement(defElt, "dataComponents");
			if (dataElt == null)
			    dataElt = dom.getElement(defElt, "components");
			
			Element encElt = dom.getElement(defElt, "encoding");		
			SWECommonUtils utils = new SWECommonUtils();
			sweReader.setDataComponents(utils.readComponentProperty(dom, dataElt));
			if (encElt == null)
				sweReader.setDataEncoding(utils.readEncodingProperty(dom, encElt));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMHelperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLReaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sweReader;		
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
	
	
	public HashMap<String, String []> getSelectedMappings(){
		return selectedMappings;
	}
	
	//  Store selected Mappings so we can contruct the Symbolizer at the completion of the wizard
	private void storeSelectedMappings(){
		String [] selMappings = new String[5];
		selMappings[0] = geometryComp.getMapFromX();
		selMappings[1] = geometryComp.getMapFromY();
		selMappings[2] = geometryComp.getMapFromZ();
		selMappings[3] = geometryComp.getMapFromTime();
		selMappings[4] = geometryComp.getMapFromBreak();
	//	selectedMappings.put(offerings[offeringIndex], selMappings);
	}
	
	public IWizardPage getNextPage(){
		storeSelectedMappings();
	//	if(++offeringIndex < offerings.length) {
			String [] dataComponents = getComponents();
			if(dataComponents != null) {
				geometryComp.setMappableItems(dataComponents);
				assignDefaultComponents(dataComponents);
			}
			return this;
	//	}
	//	return ((AddSASItemWizard)this.getWizard()).symPage;
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		//  change page options
	}

	public Vector3d getFoiLocation() {
		return foiLocation;
	}
}


