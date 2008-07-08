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

import java.util.ArrayList;
import java.util.List;

import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.sas.SASLayerCapabilities;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.wcs.WCSLayerCapabilities;
import org.vast.ows.wfs.WFSLayerCapabilities;
import org.vast.ows.wms.WMSLayerCapabilities;

/**
 * <p><b>Title:</b>
 *  TODO:  LaeyerInfo
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  Gets Layer Info as nice String []'s for display in LayerTree
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Aug 30, 2006
 * @version 1.0
 */

public class LayerInfo {

	OWSLayerCapabilities caps;
	
	public LayerInfo(OWSLayerCapabilities caps){
		this.caps = caps;
	}
	
	public List [] getOptions(){
		if(caps instanceof SOSLayerCapabilities) {
			return getSOSOptions();
		} else if(caps instanceof SASLayerCapabilities) {
			return getSASOptions();
		} else if(caps instanceof WMSLayerCapabilities) {
			return getWMSOptions();
		} else if(caps instanceof WCSLayerCapabilities) {
			return getWCSOptions();
		} else if(caps instanceof WFSLayerCapabilities) {
			return getWFSOptions();
		} else
			return null;
	}
	/*
	public List [] getSASOptions(){
		SASLayerCapabilities sasCaps = (SASLayerCapabilities)caps;
		return SASCreateList(sasCaps);
	}
	
	private List[] SASCreateList(SASLayerCapabilities sasCaps) {
		
		List<String> subscriptionOfferingIDList = new ArrayList<String>(1);
		List<String> sensorIDList = new ArrayList<String>(1);
		List<String> messageStructureNameList = new ArrayList<String>(1);
		List<String> messageStructureList = new ArrayList<String>(1);
		List<String> frequencyList = new ArrayList<String>(1);
		
		subscriptionOfferingIDList.add(sasCaps.getSubscriptionOfferingID());
		sensorIDList.add(sasCaps.getSensorID());
		messageStructureNameList.add(sasCaps.getMessageStructureName());
		messageStructureList.add(sasCaps.getMessageStructure());
		if(sasCaps.getFrequency() == null){
			frequencyList.add("unknown");}
		else frequencyList.add(sasCaps.getFrequency());
		
		List [] listFromString = new List[] {
				subscriptionOfferingIDList,
				sensorIDList,
				messageStructureNameList,
				messageStructureList		
		};
		return listFromString;
	} */

	public List [] getSASOptions(){
		SASLayerCapabilities sasCaps = (SASLayerCapabilities)caps;
		return new List[] {
				sasCaps.getSubscriptionOfferingIDList(),
				sasCaps.getSensorIDList(),
				sasCaps.getMessageStructureNameList(),
				sasCaps.getMessageStructureList()
		};
	}
	
	public List [] getSOSOptions(){
		SOSLayerCapabilities sosCaps = (SOSLayerCapabilities)caps;
		return new List[] {
				sosCaps.getObservableList(),
				sosCaps.getFormatList(),
				sosCaps.getBboxList(),
				sosCaps.getTimeList()
		};
	}

	public List [] getWMSOptions(){
		WMSLayerCapabilities wmsCaps = (WMSLayerCapabilities)caps;
		return new List[] {
				wmsCaps.getChildLayers(),
				wmsCaps.getSrsList(),
				wmsCaps.getStyleList(),
				wmsCaps.getFormatList(),
				wmsCaps.getBboxList(),
				wmsCaps.getTimeList()
		};
	}

	//  TODO add all opts
	public List [] getWCSOptions(){
		WCSLayerCapabilities wcsCaps = (WCSLayerCapabilities)caps;
		return new List[] {
				wcsCaps.getFormatList(),
				wcsCaps.getTimeList()
		};
	}

	//  TODO add all opts
	public List [] getWFSOptions(){
		WFSLayerCapabilities wfsCaps = (WFSLayerCapabilities)caps;
		return new List[] {
				wfsCaps.getFormatList(),
				wfsCaps.getBboxList(),
				wfsCaps.getSrsList(),
		};
	}
}

