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

import java.util.List;

import org.vast.ows.OWSLayerCapabilities;
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
 * <p>Copyright (c) 2006</p>
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
		} else if(caps instanceof WMSLayerCapabilities) {
			return getWMSOptions();
		} else if(caps instanceof WCSLayerCapabilities) {
			return getWCSOptions();
		} else if(caps instanceof WFSLayerCapabilities) {
			return getWFSOptions();
		} else
			return null;
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

