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
 * <p>Copyright (c) 2007</p>
 * @author Tony Cook
 * @date Aug 31, 2006
 * @version 1.0
 */

public class LayerLabelProvider extends LabelProvider {
	
	OWSLayerCapabilities caps;
	
	public LayerLabelProvider(){
	}

	@Override
	public String getText(Object element){
		if(element instanceof OWSLayerCapabilities)
			caps = (OWSLayerCapabilities)element;
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
				return "Unknown";
		} else if (element instanceof OWSLayerCapabilities) {
			return ((OWSLayerCapabilities)element).getId();
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
				return "Unknown";
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
		return isoStart + "/" + isoStop + "/" + ti.getTimeStep();
	}
	
	public void setCapabilities(OWSLayerCapabilities caps){
		this.caps = caps;
	}
	
}

