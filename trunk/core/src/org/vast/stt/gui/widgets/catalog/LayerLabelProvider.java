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

import org.eclipse.jface.viewers.LabelProvider;
import org.vast.ows.OWSLayerCapabilities;
import org.vast.ows.sos.SOSLayerCapabilities;
import org.vast.ows.util.Bbox;
import org.vast.ows.util.TimeInfo;
import org.vast.ows.wcs.WCSLayerCapabilities;
import org.vast.ows.wfs.WFSLayerCapabilities;
import org.vast.ows.wms.WMSLayerCapabilities;
import org.vast.util.DateTimeFormat;

/**
 * <p><b>Title:</b>
 *  LayerLabelProvider
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  TODO: Add Description
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 31, 2006
 * @version 1.0
 */

public class LayerLabelProvider extends LabelProvider {
	
	OWSLayerCapabilities caps;
	
	public LayerLabelProvider(){
	}

	public void setCapabilities(OWSLayerCapabilities caps){
		this.caps = caps;
	}
	
	@Override
	public String getText(Object element){
		if(caps instanceof SOSLayerCapabilities)
			return getSOSText(element);
		else if(caps instanceof WMSLayerCapabilities)
			return getWMSText(element);
		else if(caps instanceof WFSLayerCapabilities)
			return getWFSText(element);
		else if(caps instanceof WCSLayerCapabilities)
			return getWCSText(element);
		else
			return element.toString();
	}
	
	public String getSOSText(Object element)
	{
		SOSLayerCapabilities sosCaps = (SOSLayerCapabilities)caps;
		if(element instanceof List) {
			if(element == sosCaps.getBboxList())
				return "BBOXes";
			else if (element == sosCaps.getObservableList())
				return "Observables";
			else if (element == sosCaps.getFormatList())
				return "Formats";
			else if (element == sosCaps.getTimeList()) 
				return "Times";
			else
				return "WTF?";
		} else if (element instanceof OWSLayerCapabilities) {
			return ((OWSLayerCapabilities)element).getName();
		} else if(element instanceof TimeInfo) {
			return getTimeText((TimeInfo)element);
		} else
			return element.toString();
	}	
	
	public String getWMSText(Object element)
	{
		WMSLayerCapabilities wmsCaps = (WMSLayerCapabilities)caps;
		if(element instanceof List) {
			if(element == wmsCaps.getBboxList())
				return "BBOXes";
			else if (element == wmsCaps.getSrsList())
				return "SRS";
			else if (element == wmsCaps.getStyleList())
				return "Styles";
			else if (element == wmsCaps.getFormatList())
				return "Formats";
			else if (element == wmsCaps.getTimeList()) 
				return "Times";
			else if (element == wmsCaps.getChildLayers()) 
				return "Child Layers";
			else
				return "Heh?";
		} else if (element instanceof OWSLayerCapabilities) {
			return ((OWSLayerCapabilities)element).getName();
		} else if(element instanceof TimeInfo) {
			return getTimeText((TimeInfo)element);
		} else if(element instanceof Bbox) {
			Bbox bbox = (Bbox)element;
			return bbox.getMinX() + "," + bbox.getMinY() + " - " + bbox.getMaxX() + "," + bbox.getMaxY(); 
		} else {
			if (element != null)
			    return element.toString();
			return "NULL"; //  Why am I getting this?
		}
	}		

	public String getWCSText(Object element){
		return element.toString();
	}

	public String getWFSText(Object element){
		return element.toString();
	}

	public String getTimeText(TimeInfo ti){
		String isoStart = DateTimeFormat.formatIso(ti.getStartTime(), 0); 
		String isoStop = DateTimeFormat.formatIso(ti.getStopTime(), 0); 
		return isoStart + "/" + isoStop + "/" + ti.getStepTime();
	}
}

